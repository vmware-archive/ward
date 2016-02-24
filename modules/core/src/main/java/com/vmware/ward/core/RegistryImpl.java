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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ObjectUtils;

/**
 * Default {@link Registry} implementation.
 * 
 * @author Rostislav Hristov
 */
public class RegistryImpl implements Registry {

	private String name;
	private Map<String, Service> services = new ConcurrentHashMap<String, Service>();
	private Map<String, Service> serviceBeanNames = new ConcurrentHashMap<String, Service>();
	private List<Application> applications = new ArrayList<Application>();
	private List<ApplicationListener> applicationListeners = new ArrayList<ApplicationListener>();
	private List<ServiceListener> serviceListeners = new ArrayList<ServiceListener>();
	private Log logger = LogFactory.getLog(getClass());

	RegistryImpl(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void addService(String name, Service service) {
		this.services.put(name, service);
		this.serviceBeanNames.put(service.getName(), service);
		onServiceEvent(ServiceEvent.ADD_SERVICE, service);
	}

	@Override
	public Service removeService(String name) {
		Service service = services.remove(name);
		if (service != null) {
			this.serviceBeanNames.remove(service.getName());
		}
		return service;
	}

	@Override
	public Service getService(String name) {
		if (this.services.containsKey(name)) {
			return this.services.get(name);
		} else {
			return this.serviceBeanNames.get(name);
		}
	}

	@Override
	public Map<String, Service> getServices() {
		return Collections.unmodifiableMap(this.services);
	}

	@Override
	public Service[] getServices(Class<?> type) {
		return getServices(new Class<?>[] { type });
	}

	@Override
	public Service[] getServices(Class<?>[] interfaces) {
		Service[] result = null;
		// TODO: Implement some sort of caching
		for (Service service : this.services.values()) {
			boolean match = true;
			for (Class<?> clazz : interfaces) {
				try {
					match = match
							&& clazz.isAssignableFrom(service.getTarget()
									.getClass());
				} catch (Exception e) {
					match = false;
				}
			}
			if (match) {
				result = ObjectUtils.addObjectToArray(result, service);
			}
		}
		if (result != null) {
			Arrays.sort(result);
		}
		return result;
	}

	@Override
	public Service[] getServices(Application application) {
		Service[] result = null;
		for (Service service : this.services.values()) {
			if (service.getApplication().equals(application)) {
				result = ObjectUtils.addObjectToArray(result, service);
			}
		}
		return result;
	}

	@Override
	public void addApplicationListener(ApplicationListener listener) {
		this.applicationListeners.add(listener);
	}

	@Override
	public void removeApplicationListener(ApplicationListener listener) {
		this.applicationListeners.remove(listener);
	}

	@Override
	public void addServiceListener(ServiceListener listener) {
		this.serviceListeners.add(listener);
	}

	@Override
	public void removeServiceListener(ServiceListener listener) {
		this.serviceListeners.remove(listener);
	}

	@Override
	public Application[] getApplications() {
		Application[] result = null;
		for (Application application : this.applications) {
			if (application.isAvailable()) {
				result = ObjectUtils.addObjectToArray(result, application);
			}
		}
		return result;
	}

	@Override
	public Application getApplication(String contextPath) {
		for (Application application : this.applications) {
			if (application.getContextPath().equals(contextPath)) {
				return application;
			}
		}
		return null;
	}

	@Override
	public void addApplication(Application application)
			throws ApplicationException {
		if (this.applications.contains(application)) {
			throw new ApplicationException("Application ["
					+ application.getContextPath()
					+ "] has already been added to the registry.");
		}
		this.applications.add(application);
		Collections.sort(this.applications);
		logger.info("Application [" + application.getContextPath()
				+ "] has been added.");
		onApplicationEvent(ApplicationEvent.ADD_APPLICATION, application);
	}

	@Override
	public void removeApplication(Application application)
			throws ApplicationException {
		if (!this.applications.contains(application)) {
			throw new ApplicationException("Application ["
					+ application.getContextPath()
					+ "] doesn't exist in the registry.");
		}
		this.applications.remove(application);
		logger.info("Application [" + application.getContextPath()
				+ "] has been removed.");
		onApplicationEvent(ApplicationEvent.REMOVE_APPLICATION, application);
	}

	private void onApplicationEvent(String type, Application application) {
		ApplicationEvent event = new ApplicationEvent(this, type, application);
		for (ApplicationListener listener : this.applicationListeners) {
			listener.onApplicationEvent(event);
		}
	}

	private void onServiceEvent(String type, Service service) {
		ServiceEvent event = new ServiceEvent(this, type, service);
		for (ServiceListener listener : this.serviceListeners) {
			listener.onServiceEvent(event);
		}
	}

}
