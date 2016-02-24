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

package com.vmware.ward.intro.common;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.vmware.ward.core.Application;
import com.vmware.ward.core.Registry;
import com.vmware.ward.core.RegistryAware;
import com.vmware.ward.core.Service;
import com.vmware.ward.common.MessageService;
import com.vmware.ward.common.OrderService;

/**
 * @author Rostislav Hristov
 */
@Component
public class CommonUtils implements RegistryAware {

	private Registry registry;

	@Override
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public String webjarsResource(String artifact, String resource)
			throws IOException {
		PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(
				Thread.currentThread().getContextClassLoader());
		Resource[] resources = resourcePatternResolver
				.getResources("classpath:META-INF/resources/webjars/"
						+ artifact + "/**");
		for (Resource res : resources) {
			String path = res.getURL().getPath();
			if (path.endsWith(resource)) {
				return path.replaceFirst(".*META-INF/resources",
						getRootContextPath());
			}
		}
		return null;
	}

	public String getRootContextPath() {
		String result = null;
		Application[] applications = registry.getApplications();
		for (Application application : applications) {
			if (result == null || result.contains(application.getContextPath())) {
				result = application.getContextPath();
			}
		}
		return result;
	}

	public Application[] getApplications() {
		Application[] applications = registry.getApplications();
		Arrays.sort(applications, OrderService.COMPARATOR);
		return applications;
	}

	public Application getApplication(HttpServletRequest request) {
		Application[] applications = registry.getApplications();
		for (Application application : applications) {
			if (application.getContextPath().equals(request.getContextPath())) {
				return application;
			}
		}
		return null;
	}

	public String getMessage(Application application, String code,
			Object[] args, Locale locale) {
		Service[] services = application
				.getServices(MessageService.class);
		for (Service service : services) {
			MessageService messageService = (MessageService) service
					.getTarget();
			String result = messageService.getMessage(code, args, locale);
			if (result != null) {
				return result;
			}
		}
		throw new NoSuchMessageException(code, locale);
	}

}
