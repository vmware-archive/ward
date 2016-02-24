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

import java.util.Arrays;
import java.util.Comparator;

import org.springframework.core.Ordered;

import com.vmware.ward.core.Application;
import com.vmware.ward.core.Service;

/**
 * An interface that defines how applications can be ordered.
 * 
 * @author Rostislav Hristov
 */
public interface OrderService {

	/**
	 * Provides the order value.
	 * 
	 * @return the order value
	 */
	int getOrder();

	/**
	 * Sets the order value.
	 * 
	 * @param the
	 *            order value
	 */
	void setOrder(int order);

	public static final Comparator<Application> COMPARATOR = new Comparator<Application>() {

		public int compare(Application application1,
				Application application2) {
			int order1 = getOrder(application1);
			int order2 = getOrder(application2);
			return (order1 < order2) ? -1 : (order1 > order2) ? 1 : 0;
		}

		protected int getOrder(Application application) {
			Service[] services = application.getServices();
			for (Service service : services) {
				if (Arrays.equals(service.getInterfaces(),
						new Class[] { OrderService.class })) {
					return ((OrderService) service.getTarget())
							.getOrder();
				}
			}
			return Ordered.LOWEST_PRECEDENCE;
		}
	};
}
