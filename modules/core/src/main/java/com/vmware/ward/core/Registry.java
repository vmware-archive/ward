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

import java.util.Map;

/**
 * Registry that maintains a list of all <strong>Ward</strong> application
 * contexts, services and shared data.
 * 
 * @author Rostislav Hristov
 */
public interface Registry {

	/**
	 * Provides the name of the registry.
	 * 
	 * @return registry name
	 */
	String getName();

	/**
	 * Adds an application to the registry.
	 * 
	 * @param application
	 *            application instance
	 * @throws ApplicationException if the application has already been added
	 */
	void addApplication(Application application) throws ApplicationException;

	/**
	 * Removes an application from the registry.
	 * @param application
	 *            application instance
	 * @throws ApplicationException if the application doesn't exist
	 */
	void removeApplication(Application application) throws ApplicationException;

	/**
	 * Adds a service to the registry.
	 * 
	 * @param name
	 *            unique service name
	 * @param service
	 *            service instance
	 */
	void addService(String name, Service service);

	/**
	 * Removes a service from the registry.
	 * 
	 * @param name
	 *            service name
	 * @return the removed service
	 */
	Service removeService(String name);

	/**
	 * Provides a service with a specific name.
	 * 
	 * @param name
	 *            service name
	 * @return service instance or <code>null</code> if the service does not
	 *         exist
	 */
	Service getService(String name);

	/**
	 * Provides a map of all the registered services.
	 * 
	 * @return an unmodifiable map containing all services
	 */
	Map<String, Service> getServices();

	/**
	 * Provides an array of services which implement a specific interface.
	 * 
	 * @param type
	 *            interface class
	 * @return an array of services
	 */
	Service[] getServices(Class<?> type);

	/**
	 * Provides an array of services which implement a number of specific
	 * interfaces.
	 * 
	 * @param types
	 *            array of interface classes
	 * @return an array of services
	 */
	Service[] getServices(Class<?>[] interfaces);

	/**
	 * Provides an array of services which are exposed by the given application
	 * interfaces.
	 * 
	 * @param application
	 *            application instance
	 * @return an array of services
	 */
	Service[] getServices(Application application);

	/**
	 * Adds an event listener which will be notified for <strong>tc
	 * Kiss</strong> application additions and removals.
	 * 
	 * @param listener
	 *            {@link ApplicationListener} instance
	 */
	void addApplicationListener(ApplicationListener listener);

	/**
	 * Removes a registered application context event listener.
	 * 
	 * @param listener
	 *            {@link ApplicationListener} instance
	 */
	void removeApplicationListener(ApplicationListener listener);

	/**
	 * Adds an event listener which will be notified for <strong>tc
	 * Kiss</strong> service additions and removals.
	 * 
	 * @param listener
	 *            {@link ServiceListener} instance
	 */
	void addServiceListener(ServiceListener listener);

	/**
	 * Removes a registered service event listener.
	 * 
	 * @param listener
	 *            {@link ServiceListener} instance
	 */
	void removeServiceListener(ServiceListener listener);

	/**
	 * Returns a sorted unmodifiable map containing all active
	 * {@link ApplicationImpl} instances.
	 * 
	 * @return array of applications
	 */
	Application[] getApplications();

	/**
	 * Provides the {@link ApplicationImpl} instance with a specific context path.
	 * 
	 * @param contextPath
	 *            contextPath of a <strong>Ward</strong> application
	 * @return an application instance if any
	 */
	Application getApplication(String contextPath);

}
