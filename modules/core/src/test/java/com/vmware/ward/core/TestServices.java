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

import static org.junit.Assert.assertEquals;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Rostislav Hristov
 */
public class TestServices {

	private MockServletContext servletContext;
	private ApplicationServletContextListener servletContextListener;
	private WebApplicationContext webApplicationContext;

	@Before
	public void before() throws ServletException {
		this.servletContext = new MockServletContext();
		this.servletContext.addInitParameter(
				ContextLoaderListener.CONTEXT_CLASS_PARAM,
				AnnotationConfigWebApplicationContext.class.getName());
		this.servletContext.addInitParameter(
				ContextLoaderListener.CONFIG_LOCATION_PARAM,
				TestConfiguration.class.getName());
		this.servletContextListener = new ApplicationServletContextListener();
		this.servletContextListener.contextInitialized(new ServletContextEvent(
				servletContext));
		this.webApplicationContext = WebApplicationContextUtils
				.getWebApplicationContext(this.servletContext);
	}

	@After
	public void after() {
		this.servletContextListener.contextDestroyed(new ServletContextEvent(
				servletContext));
	}

	@Test
	public void testServiceExport() throws Exception {
		TestService refA = (TestService) this.webApplicationContext
				.getBean("wardReferenceA");
		assertEquals(TestServiceA.class.getName(), refA.getName());
	}

	@Test
	public void testServiceList() throws Exception {
		ListBean<?> list = this.webApplicationContext.getBean(ListBean.class);
		assertEquals(2, list.size());
	}

}
