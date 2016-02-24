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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * @author Rostislav Hristov
 */
public class ApplicationServletContextListener implements ApplicationListener,
		ServletContextListener {

	private ApplicationConfig applicationConfig;
	private ClassLoader classLoader;
	private ContextLoaderListener contextLoader;
	private ServletContext servletContext;

	private final Object contextLoaderMonitor = new Object();

	@Override
	public void contextInitialized(ServletContextEvent event) {
		this.servletContext = event.getServletContext();
		this.applicationConfig = new ApplicationConfigImpl(this.servletContext);
		this.classLoader = Thread.currentThread().getContextClassLoader();
		synchronized (this.contextLoaderMonitor) {
			if (!this.applicationConfig.hasUnsatisfiedApplicationDependencies()) {
				createContextLoader();
			}
		}
		this.applicationConfig.getRegistry().addApplicationListener(this);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		try {
			if (contextLoader != null) {
				destroyContextLoader();
			}
		} finally {
			this.applicationConfig.getRegistry()
					.removeApplicationListener(this);
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		synchronized (this.contextLoaderMonitor) {
			if (!event.getApplication().equals(
					this.applicationConfig.getApplication())) {
				ClassLoader classLoader = Thread.currentThread()
						.getContextClassLoader();
				Thread.currentThread().setContextClassLoader(this.classLoader);
				if (ApplicationEvent.ADD_APPLICATION.equals(event.getType())
						&& this.contextLoader == null
						&& !this.applicationConfig
								.hasUnsatisfiedApplicationDependencies()) {
					createContextLoader();
				} else if (ApplicationEvent.REMOVE_APPLICATION.equals(event
						.getType())
						&& this.contextLoader != null
						&& this.applicationConfig
								.hasUnsatisfiedApplicationDependencies()) {
					destroyContextLoader();
				}
				Thread.currentThread().setContextClassLoader(classLoader);
			}
		}
	}

	private void createContextLoader() {
		this.contextLoader = new ContextLoaderListener();
		this.contextLoader.contextInitialized(new ServletContextEvent(
				this.servletContext));
		this.applicationConfig.getRegistry().addApplication(
				applicationConfig.getApplication());
	}

	private void destroyContextLoader() {
		this.contextLoader.contextDestroyed(new ServletContextEvent(
				this.servletContext));
		this.contextLoader = null;
		this.applicationConfig.getRegistry().removeApplication(
				applicationConfig.getApplication());
	}

	private class ContextLoaderListener extends
			org.springframework.web.context.ContextLoaderListener {

		@Override
		protected void configureAndRefreshWebApplicationContext(
				ConfigurableWebApplicationContext wac, ServletContext sc) {
			wac.addBeanFactoryPostProcessor(new ApplicationBeanFactoryPostProcessor(
					ApplicationServletContextListener.this.applicationConfig));
			super.configureAndRefreshWebApplicationContext(wac, sc);
		}
	}

}
