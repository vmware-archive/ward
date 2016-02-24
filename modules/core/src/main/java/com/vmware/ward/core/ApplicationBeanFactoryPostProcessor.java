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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author Rostislav Hristov
 */
public class ApplicationBeanFactoryPostProcessor implements
		BeanFactoryPostProcessor, BeanPostProcessor {

	private ApplicationConfig applicationConfig;

	public ApplicationBeanFactoryPostProcessor(
			ApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.addBeanPostProcessor(this);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ApplicationAware) {
			((ApplicationAware) bean).setApplication(this.applicationConfig
					.getApplication());
		}
		if (bean instanceof ApplicationConfigAware) {
			((ApplicationConfigAware) bean)
					.setApplicationConfig(this.applicationConfig);
		}
		if (bean instanceof RegistryAware) {
			((RegistryAware) bean).setRegistry(this.applicationConfig
					.getRegistry());
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return bean;
	}

}
