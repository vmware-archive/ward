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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

/**
 * @author Rostislav Hristov
 */
public class ApplicationImpl implements Application {

	private ApplicationConfigImpl config;

	public ApplicationImpl(ApplicationConfigImpl context) {
		this.config = context;
	}

	@Override
	public boolean isAvailable() {
		try {
			return this.config.getServletContext().getResource("META-INF") != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getContextPath() {
		ServletContext servletContext = this.config.getServletContext();
		if (servletContext != null) {
			return servletContext.getContextPath();
		}
		return null;
	}

	@Override
	public String[] getDependencies() {
		return this.config.getApplicationDependencies();
	}

	@Override
	public Service[] getServices() {
		return this.config.getRegistry().getServices(this);
	}

	@Override
	public Service[] getServices(Class<?> type) {
		List<Service> services = new ArrayList<Service>(
				Arrays.asList(this.config.getRegistry().getServices(type)));
		services.retainAll(Arrays.asList(getServices()));
		return services.toArray(new Service[] {});
	}

	@Override
	public int compareTo(Application application) {
		return getContextPath() != null && application.getContextPath() != null ? getContextPath()
				.compareTo(application.getContextPath())
				: getContextPath() != null ? -1 : 1;
	}

}
