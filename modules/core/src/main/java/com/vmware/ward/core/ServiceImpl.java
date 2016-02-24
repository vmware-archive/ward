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
public class ServiceImpl implements Service {

	private ServiceBean factoryBean;

	public ServiceImpl(ServiceBean factoryBean) {
		this.factoryBean = factoryBean;
	}

	@Override
	public Application getApplication() {
		return this.factoryBean.getApplication();
	}

	@Override
	public String getName() {
		return this.factoryBean.getBeanName();
	}

	@Override
	public Class<?>[] getInterfaces() {
		return this.factoryBean.getInterfaces();
	}

	@Override
	public Object getTarget() {
		return this.factoryBean.getTarget();
	}

	@Override
	public int compareTo(Service service) {
		return getName() != null && service.getName() != null ? getName()
				.compareTo(service.getName()) : getName() != null ? -1 : 1;

	}
}
