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

package com.vmware.ward.core;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Comparator;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * An implementation of a <strong>Ward</strong> service.
 * 
 * @author Rostislav Hristov
 */
public class ServiceBean implements BeanNameAware, BeanFactoryAware,
		DisposableBean, FactoryBean<Object>, InitializingBean, Ordered,
		ServletContextAware, ApplicationAware, RegistryAware {

	private BeanFactory beanFactory;
	private String beanName;
	private Class<?>[] interfaces;
	private int order;
	private Application application;
	private Registry registry;
	private ServletContext servletContext;
	private Object target;
	private String targetBeanName;

	private static final Comparator<Class<?>> CLASS_COMPARATOR = new Comparator<Class<?>>() {
		@Override
		public int compare(Class<?> c1, Class<?> c2) {
			return c1.getName().compareTo(c2.getName());
		}
	};

	public Application getApplication() {
		return this.application;
	}

	public String getBeanName() {
		return this.beanName;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setApplication(Application application) {
		this.application = application;
	}

	@Override
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() throws Exception {
		boolean hasNamedBean = StringUtils.hasText(this.targetBeanName);
		String beanName = (hasNamedBean ? this.targetBeanName : ObjectUtils
				.getIdentityHexString(this.target));
		this.registry.removeService(beanName);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		boolean hasNamedBean = StringUtils.hasText(this.targetBeanName);
		if (!hasNamedBean && this.target == null) {
			throw new Exception("Either a target or a reference is required.");
		}

		Class<?> targetClass;
		if (hasNamedBean) {
			if (this.beanFactory.isSingleton(this.targetBeanName)) {
				this.target = this.beanFactory.getBean(this.targetBeanName);
				targetClass = this.target.getClass();
			} else {
				targetClass = this.beanFactory.getType(this.targetBeanName);
			}
			if (this.beanFactory instanceof ConfigurableBeanFactory) {
				ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) this.beanFactory;
				if (StringUtils.hasText(this.beanName)
						&& configurableBeanFactory.containsBean(this.beanName)) {
					configurableBeanFactory.registerDependentBean(
							this.targetBeanName,
							BeanFactory.FACTORY_BEAN_PREFIX + this.beanName);
					configurableBeanFactory.registerDependentBean(
							this.targetBeanName, this.beanName);
				}
			} else {
				throw new ServiceException(
						"Service dependency registration failed.");
			}
		} else {
			targetClass = this.target.getClass();
		}

		if (this.interfaces == null) {
			throw new ServiceException(
					"At least one service interface is required.");
		}

		for (Class<?> interfaceClass : this.interfaces) {
			if (!interfaceClass.isAssignableFrom(targetClass)) {
				throw new ServiceException(
						"The supplied target does not implement the ["
								+ interfaceClass + "] interface.");

			}
		}
		String beanName = (hasNamedBean ? this.targetBeanName : ObjectUtils
				.getIdentityHexString(this.target));
		this.registry.addService(beanName, new ServiceImpl(this));
	}

	@Override
	public Object getObject() throws Exception {
		Object target = getTarget();
		if (target == null) {
			throw new ServiceException("This service is not available.");
		}
		return target;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<?> getObjectType() {
		if (this.interfaces != null) {
			return (Class<Object>) Proxy.getProxyClass(Thread.currentThread()
					.getContextClassLoader(), this.interfaces);
		}
		return null;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Class<?>[] getInterfaces() {
		return this.interfaces;
	}

	public void setInterfaces(Class<?>[] interfaces) {
		Arrays.sort(interfaces, CLASS_COMPARATOR);
		this.interfaces = interfaces;
	}

	public Object getTarget() {
		try {
			if (this.servletContext == null
					|| (this.servletContext != null && this.servletContext
							.getResource("META-INF") != null)) {
				if (this.target != null) {
					return target;
				}
				if (this.beanFactory != null && this.targetBeanName != null) {
					return this.beanFactory.getBean(this.targetBeanName);
				}
			}
		} catch (MalformedURLException e) {
			this.servletContext.log(e.getMessage(), e);
		}
		return null;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public String getTargetBeanName() {
		return this.targetBeanName;
	}

	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

}