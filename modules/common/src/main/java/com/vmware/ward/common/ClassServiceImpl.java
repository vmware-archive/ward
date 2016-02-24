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

package com.vmware.ward.common;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.vmware.ward.core.ServiceException;


/**
 * Basic {@link ClassService} implementation.
 * @author Rostislav Hristov
 */
public class ClassServiceImpl implements ApplicationContextAware,
		ClassService {

	private ApplicationContext applicationContext;
	private List<String> packages;

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> clazz = this.applicationContext.getClassLoader().loadClass(name);
		if (this.packages.contains(clazz.getPackage().getName())) {
			return clazz;
		}
		throw new ServiceException("The class [\"" + name
				+ "\"] is not visible.");
	}

	@Override
	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
