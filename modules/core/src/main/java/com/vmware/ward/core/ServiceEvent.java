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

/**
 * @author Rostislav Hristov
 */
public class ServiceEvent {

	public static final String ADD_SERVICE = "addService";
	public static final String REMOVE_SERVICE = "removeService";

	private Service service;
	private Registry source;
	private String type;

	public ServiceEvent(Registry source, String type, Service service) {
		this.source = source;
		this.type = type;
		this.service = service;
	}

	public Service getService() {
		return this.service;
	}

	public Registry getSource() {
		return this.source;
	}

	public String getType() {
		return this.type;
	}

}
