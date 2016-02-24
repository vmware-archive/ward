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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.vmware.ward.core.ServiceBean;
import com.vmware.ward.intro.common.CommonConfiguration;
import com.vmware.ward.intro.common.CommonService;

/**
 * @author Rostislav Hristov
 */
@Configuration
@ComponentScan(basePackageClasses = CommonConfiguration.class)
public class SupportConfiguration {

	@Bean
	public SupportService supportService() {
		return new SupportService();
	}

	@Bean
	public ServiceBean wardSupportService() {
		ServiceBean service = new ServiceBean();
		service.setInterfaces(new Class[] { CommonService.class });
		service.setTarget(supportService());
		return service;
	}

}
