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

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * @author Rostislav Hristov
 */
@SuppressWarnings("serial")
public class ApplicationServlet extends HttpServlet implements
		ApplicationListener {

	private ApplicationConfig applicationConfig;
	private ClassLoader classLoader;
	private DispatcherServlet servlet;

	private final Object servletMonitor = new Object();

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		this.applicationConfig = new ApplicationConfigImpl(servletConfig);
		this.classLoader = Thread.currentThread().getContextClassLoader();
		synchronized (this.servletMonitor) {
			if (!this.applicationConfig.hasUnsatisfiedApplicationDependencies()) {
				createServlet();
			}
		}
		this.applicationConfig.getRegistry().addApplicationListener(this);
	}

	@Override
	public void destroy() {
		try {
			if (this.servlet != null) {
				destroyServlet();
			}
		} finally {
			this.applicationConfig.getRegistry()
					.removeApplicationListener(this);
		}
		super.destroy();
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (this.servlet != null) {
			this.servlet.service(request, response);
		} else {
			throw new SimpleServletException(
					"Application dependencies are not satisfied.");
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		synchronized (this.servletMonitor) {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread().setContextClassLoader(this.classLoader);
			if (!this.applicationConfig.hasUnsatisfiedApplicationDependencies()
					&& ApplicationEvent.ADD_APPLICATION.equals(event.getType())
					&& this.servlet == null) {
				createServlet();
			} else if (this.applicationConfig
					.hasUnsatisfiedApplicationDependencies()
					&& ApplicationEvent.REMOVE_APPLICATION.equals(event
							.getType()) && this.servlet != null) {
				destroyServlet();
			}
			Thread.currentThread().setContextClassLoader(classLoader);
		}
	}

	private void createServlet() {
		try {
			this.servlet = new DispatcherServlet();
			this.servlet.init(getServletConfig());
			this.applicationConfig.getRegistry().addApplication(
					applicationConfig.getApplication());
		} catch (ServletException e) {
			getServletContext().log(e.getMessage(), e);
		}
	}

	private void destroyServlet() {
		this.servlet.destroy();
		this.servlet = null;
		this.applicationConfig.getRegistry().removeApplication(
				applicationConfig.getApplication());
	}

	private class DispatcherServlet extends
			org.springframework.web.servlet.DispatcherServlet {

		@Override
		protected void configureAndRefreshWebApplicationContext(
				ConfigurableWebApplicationContext wac) {
			wac.addBeanFactoryPostProcessor(new ApplicationBeanFactoryPostProcessor(
					ApplicationServlet.this.applicationConfig));
			super.configureAndRefreshWebApplicationContext(wac);
		}
	}

	private class SimpleServletException extends ServletException {

		public SimpleServletException(String message) {
			super(message);
		}

		@Override
		public Throwable fillInStackTrace() {
			return null;
		}

		@Override
		public StackTraceElement[] getStackTrace() {
			return new StackTraceElement[0];
		}

		@Override
		public String toString() {
			return getMessage();
		}
	}

}
