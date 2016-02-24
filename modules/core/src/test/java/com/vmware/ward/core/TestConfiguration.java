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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rostislav Hristov
 */
@Configuration
public class TestConfiguration {

	@Bean
	public ServiceBean testServiceA() {
		ServiceBean service = new ServiceBean();
		service.setInterfaces(new Class[] { TestService.class });
		service.setTargetBeanName("wardTestServiceA");
		return service;
	}

	@Bean
	public TestServiceA wardTestServiceA() {
		return new TestServiceA();
	}

	@Bean
	public ServiceBean testServiceB() {
		ServiceBean service = new ServiceBean();
		service.setInterfaces(new Class[] { TestService.class });
		service.setTarget(new TestServiceB());
		return service;
	}

	@Bean
	public ReferenceBean wardReferenceA() {
		ReferenceBean reference = new ReferenceBean();
		reference.setInterfaces(new Class[] { TestService.class });
		reference.setServiceBeanName("wardTestServiceA");
		return reference;
	}

	@Bean
	public ListBean<TestService> wardList() {
		ListBean<TestService> list = new ListBean<TestService>();
		list.setInterfaces(new Class[] { TestService.class });
		return list;
	}

}
