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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * An implementation of a <strong>Ward</strong> reference list.
 * 
 * @author Rostislav Hristov
 */
@SuppressWarnings("serial")
public class ListBean<T> extends ArrayList<T> implements DisposableBean,
		InitializingBean, RegistryAware {

	private Registry registry;
	private Class<?>[] interfaces;
	private ServiceListener listener;
	private List<Object> proxies = new ArrayList<Object>();
	Map<Service, Object> mapping = new HashMap<Service, Object>();

	@Override
	public void destroy() throws Exception {
		this.registry.removeServiceListener(listener);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.listener = new ListServiceListener();
		this.registry.addServiceListener(this.listener);
		Service[] services = this.registry.getServices(this.interfaces);
		if (services != null) {
			for (Service service : services) {
				Object reference = createProxy(service);
				this.mapping.put(service, reference);
				this.proxies.add(reference);
			}
		}
	}

	@Override
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Override
	public int size() {
		return this.proxies.size();
	}

	@Override
	public boolean isEmpty() {
		return this.proxies.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.proxies.contains(o);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Iterator iterator() {
		return new ServiceIterator();
	}

	@Override
	public Object[] toArray() {
		return this.proxies.toArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray(Object[] a) {
		return this.proxies.toArray(a);
	}

	@Override
	public boolean add(Object e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean containsAll(Collection c) {
		return this.proxies.containsAll(c);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean addAll(int index, Collection c) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		return (T) this.proxies.get(index);
	}

	@Override
	public T set(int index, Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		return this.proxies.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.proxies.lastIndexOf(o);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ListIterator listIterator() {
		return listIterator(0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ListIterator listIterator(int index) {
		return new ServiceListIterator(index);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List subList(int fromIndex, int toIndex) {
		return this.proxies.subList(fromIndex, toIndex);
	}

	public Class<?>[] getInterfaces() {
		return this.interfaces;
	}

	public void setInterfaces(Class<?>[] interfaces) {
		Arrays.sort(interfaces);
		this.interfaces = interfaces;
	}

	private Object createProxy(Service service) {
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), this.interfaces,
				new ProxyInvocationHandler(service));
	}

	private class ProxyInvocationHandler implements InvocationHandler {

		private Service service;

		public ProxyInvocationHandler(Service service) {
			this.service = service;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			try {
				return method.invoke(this.service.getTarget(), args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

	}

	private class ListServiceListener implements ServiceListener {

		@Override
		public void onServiceEvent(ServiceEvent event) {
			Service service = event.getService();
			if (!Arrays.equals(getInterfaces(), service.getInterfaces())) {
				return;
			}
			if (ServiceEvent.ADD_SERVICE.equals(event.getType())) {
				Object proxy = createProxy(service);
				ListBean.this.mapping.put(service, proxy);
				ListBean.this.proxies.add(proxy);
			} else if (ServiceEvent.REMOVE_SERVICE.equals(event.getType())) {
				ListBean.this.proxies.remove(ListBean.this.mapping
						.remove(service));
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private class ServiceIterator implements Iterator {

		private Iterator iterator;

		public ServiceIterator() {
			this.iterator = ListBean.this.proxies.iterator();
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public Object next() {
			return this.iterator.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	@SuppressWarnings({ "rawtypes" })
	private class ServiceListIterator implements ListIterator {

		private ListIterator iterator;

		public ServiceListIterator(int index) {
			this.iterator = ListBean.this.proxies.listIterator(index);
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public Object next() {
			return this.iterator.next();
		}

		@Override
		public boolean hasPrevious() {
			return this.iterator.hasNext();
		}

		@Override
		public Object previous() {
			return this.iterator.hasPrevious();
		}

		@Override
		public int nextIndex() {
			return this.iterator.nextIndex();
		}

		@Override
		public int previousIndex() {
			return this.iterator.previousIndex();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(Object e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(Object e) {
			throw new UnsupportedOperationException();
		}

	}

}