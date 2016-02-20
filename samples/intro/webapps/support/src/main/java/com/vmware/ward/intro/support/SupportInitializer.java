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

package com.vmware.ward.intro.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.vmware.ward.core.ApplicationConfig;
import com.vmware.ward.core.ApplicationServlet;

/**
 * @author Rostislav Hristov
 */
public class SupportInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) {
		ServletRegistration.Dynamic ward = servletContext.addServlet(
				"ward", new ApplicationServlet());
		ward.setInitParameter(ApplicationConfig.APPLICATION_DEPENDENCIES_PARAM,
				"/ward");
		ward.setInitParameter(ContextLoaderListener.CONTEXT_CLASS_PARAM,
				AnnotationConfigWebApplicationContext.class.getName());
		ward.setInitParameter(ContextLoaderListener.CONFIG_LOCATION_PARAM,
				SupportConfiguration.class.getName());
		ward.setLoadOnStartup(3);
		ward.addMapping("/");
	}

}