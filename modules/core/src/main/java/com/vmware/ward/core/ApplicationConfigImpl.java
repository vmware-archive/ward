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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.util.StringUtils;

/**
 * @author Rostislav Hristov
 */
public class ApplicationConfigImpl implements ApplicationConfig {

	private static Map<String, Registry> registry = new HashMap<String, Registry>();

	private Application application;
	private String[] applicationDependencies;
	private String registryName;
	private ServletContext servletContext;

	private final Object registryMonitor = new Object();

	public ApplicationConfigImpl(ServletContext servletContext) {
		this.application = new ApplicationImpl(this);
		this.servletContext = servletContext;
		if (servletContext != null) {
			this.applicationDependencies = StringUtils
					.tokenizeToStringArray(
							servletContext
									.getInitParameter(ApplicationConfig.APPLICATION_DEPENDENCIES_PARAM),
							",");
			this.registryName = servletContext
					.getInitParameter(ApplicationConfig.REGISTRY_NAME_PARAM);
		}
	}

	public ApplicationConfigImpl(ServletConfig servletConfig) {
		this(servletConfig.getServletContext());
		String[] applicationDependencies = StringUtils
				.tokenizeToStringArray(
						servletConfig
								.getInitParameter(ApplicationConfig.APPLICATION_DEPENDENCIES_PARAM),
						",");
		if (applicationDependencies != null) {
			this.applicationDependencies = applicationDependencies;
		}
		String registryName = servletConfig
				.getInitParameter(ApplicationConfig.REGISTRY_NAME_PARAM);
		if (registryName != null) {
			this.registryName = registryName;
		}
	}

	@Override
	public Application getApplication() {
		return this.application;
	}

	@Override
	public String[] getApplicationDependencies() {
		return this.applicationDependencies;
	}

	@Override
	public boolean hasUnsatisfiedApplicationDependencies() {
		Registry registry = getRegistry();
		if (this.applicationDependencies != null) {
			for (String dependency : this.applicationDependencies) {
				if (registry.getApplication(dependency) == null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Registry getRegistry() {
		synchronized (this.registryMonitor) {
			if (!registry.containsKey(this.registryName)) {
				registry.put(this.registryName, new RegistryImpl(
						this.registryName));
			}
			return registry.get(this.registryName);
		}
	}

	@Override
	public String getRegistryName() {
		return this.registryName;
	}

	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}

}
