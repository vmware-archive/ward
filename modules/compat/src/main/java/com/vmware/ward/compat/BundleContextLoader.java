/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.ward.compat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.vmware.ward.core.ApplicationBeanFactoryPostProcessor;
import com.vmware.ward.core.ApplicationConfig;
import com.vmware.ward.core.ApplicationConfigAware;

/**
 * Creates {@link XmlApplicationContext} instances out of existing
 * bundle-context.xml and bundle-context-osgi.xml configurations.
 * 
 * @author Rostislav Hristov
 */
public class BundleContextLoader implements ApplicationConfigAware,
		ApplicationContextAware, BeanNameAware, DisposableBean,
		InitializingBean, ServletContextAware {

	private static final String MANIFEST_LOCATION = "META-INF/MANIFEST.MF";
	private static final String BUNDLE_CONTEXT_LOCATION = "META-INF/spring/bundle-context.xml";
	private static final String BUNDLE_CONTEXT_OSGI_LOCATION = "META-INF/spring/bundle-context-osgi.xml";
	private static final String BUNDLE_CONTEXT_KEY = BundleContextLoader.class
			.getName() + ".bundles";

	private ApplicationConfig applicationConfig;
	private ApplicationContext applicationContext;
	private String beanName;
	private XmlWebApplicationContext bundleApplicationContext;
	private Map<String, Object> bundles;
	private ServletContext servletContext;

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setApplicationConfig(ApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		if (servletContext.getAttribute(BUNDLE_CONTEXT_KEY) == null) {
			servletContext.setAttribute(BUNDLE_CONTEXT_KEY,
					new HashMap<String, Object>());
		}
		this.bundles = (Map<String, Object>) servletContext
				.getAttribute(BUNDLE_CONTEXT_KEY);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!this.bundles.containsKey(this.beanName)) {
			try {
				Enumeration<URL> manifests = this.servletContext
						.getClassLoader().getResources(MANIFEST_LOCATION);
				while (manifests.hasMoreElements()) {
					URL manifest = manifests.nextElement();
					InputStream is = manifest.openStream();
					Properties properties = new Properties();
					properties.load(is);
					String bundleName = properties.getProperty("Bundle-Name");
					if (StringUtils.hasText(bundleName)
							&& bundleName.equals(this.beanName)) {
						this.bundles.put(bundleName, manifest);
						is.close();
						break;
					}
				}
			} catch (IOException e) {
				this.servletContext.log(e.getMessage(), e);
			}
			if (this.bundles.containsKey(this.beanName)) {
				String bundleContext = this.bundles.get(this.beanName)
						.toString()
						.replace(MANIFEST_LOCATION, BUNDLE_CONTEXT_LOCATION);
				String bundleContextOsgi = this.bundles
						.get(this.beanName)
						.toString()
						.replace(MANIFEST_LOCATION,
								BUNDLE_CONTEXT_OSGI_LOCATION);
				this.bundleApplicationContext = new XmlWebApplicationContext();
				this.bundleApplicationContext
						.setServletContext(this.servletContext);
				this.bundleApplicationContext.setConfigLocations(new String[] {
						bundleContext, bundleContextOsgi });
				this.bundleApplicationContext
						.setParent(this.applicationContext);
				this.bundleApplicationContext
						.addBeanFactoryPostProcessor(new ApplicationBeanFactoryPostProcessor(
								this.applicationConfig));
				this.bundleApplicationContext.refresh();
			} else {
				throw new Exception("The bundle [\"" + this.beanName
						+ "\"] cannot be resolved.");
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		this.bundles.remove(this.beanName);
		this.bundleApplicationContext.close();
	}

}
