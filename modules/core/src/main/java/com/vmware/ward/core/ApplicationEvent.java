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
public class ApplicationEvent {

	public static final String ADD_APPLICATION = "addApplication";
	public static final String REMOVE_APPLICATION = "removeApplication";

	private Registry source;
	private String type;
	private Application application;

	public ApplicationEvent(Registry source, String type,
			Application application) {
		this.source = source;
		this.type = type;
		this.application = application;
	}

	public Registry getSource() {
		return this.source;
	}

	public String getType() {
		return this.type;
	}

	public Application getApplication() {
		return this.application;
	}

}
