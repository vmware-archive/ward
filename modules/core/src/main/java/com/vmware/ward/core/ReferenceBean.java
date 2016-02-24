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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.classify.Classifier;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

/**
 * An implementation of a <strong>Ward</strong> service reference.
 * 
 * @author Rostislav Hristov
 * @author Ahsen Jaffer
 */
public class ReferenceBean implements BeanNameAware, FactoryBean<Object>,
		InitializingBean, RegistryAware {

	private Object proxy;
	private String beanName;
	private String serviceBeanName;
	private Class<?>[] interfaces;
	private Registry registry;
	private long timeout = 300000L;

	private RetryTemplate template = new RetryTemplate();
	private TimeoutRetryPolicy retryPolicy = new TimeoutRetryPolicy();
	private NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setServiceBeanName(String serviceBeanName) {
		this.serviceBeanName = serviceBeanName;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setInterfaces(Class<?>[] interfaces) {
		Arrays.sort(interfaces);
		this.interfaces = interfaces;
	}

	@Override
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Object getObject() throws Exception {
		if (this.proxy == null && this.interfaces != null) {
			this.proxy = Proxy.newProxyInstance(Thread.currentThread()
					.getContextClassLoader(), this.interfaces,
					new ProxyInvocationHandler());
		}
		return this.proxy;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Object> getObjectType() {
		if (this.proxy != null) {
			return (Class<Object>) this.proxy.getClass();
		}
		if (this.interfaces != null) {
			return (Class<Object>) Proxy.getProxyClass(Thread.currentThread()
					.getContextClassLoader(), this.interfaces);
		}
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
		policy.setExceptionClassifier(new Classifier<Throwable, RetryPolicy>() {
			@Override
			public RetryPolicy classify(Throwable classifiable) {
				return classifiable instanceof ServiceException ? retryPolicy
						: neverRetryPolicy;
			}
		});

		this.retryPolicy.setTimeout(this.timeout);
		this.template.setRetryPolicy(policy);
	}

	private class ProxyInvocationHandler implements InvocationHandler {

		private Object invokeService(Method method, Object[] args)
				throws Exception {
			Service service = ReferenceBean.this.registry
					.getService(ReferenceBean.this.serviceBeanName != null ? ReferenceBean.this.serviceBeanName
							: ReferenceBean.this.beanName);
			if (service == null) {
				Service[] services = ReferenceBean.this.registry
						.getServices(ReferenceBean.this.interfaces);
				if (services != null) {
					service = services[0];
				}
			}
			if (service == null || service.getTarget() == null) {
				throw new ServiceException(
						"Service [name=\""
								+ ReferenceBean.this.beanName
								+ "\", interfaces=\""
								+ StringUtils
										.arrayToCommaDelimitedString(ReferenceBean.this.interfaces)
								+ "\"] is not available.");
			}
			try {
				return method.invoke(service.getTarget(), args);
			} catch (InvocationTargetException e) {
				throw new Exception(e.getTargetException());
			}
		}

		@Override
		public Object invoke(Object proxy, final Method method,
				final Object[] args) throws Throwable {

			if (ReferenceBean.this.timeout > 0) {
				return ReferenceBean.this.template
						.execute(new RetryCallback<Object>() {
							@Override
							public Object doWithRetry(RetryContext context)
									throws Exception {
								return invokeService(method, args);
							}
						});
			}
			return invokeService(method, args);
		}
	}

}