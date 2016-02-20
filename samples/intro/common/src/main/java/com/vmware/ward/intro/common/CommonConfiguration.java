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

package com.vmware.ward.intro.common;

import javax.el.ELContext;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.access.el.SpringBeanELResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.vmware.ward.common.MessageService;
import com.vmware.ward.common.MessageServiceImpl;
import com.vmware.ward.common.OrderService;
import com.vmware.ward.common.OrderServiceImpl;
import com.vmware.ward.core.ServiceBean;

/**
 * @author Rostislav Hristov
 */
@Configuration
public class CommonConfiguration extends WebMvcConfigurationSupport implements
		BeanFactoryAware, ServletContextAware {

	private static final String EL_RESOLVER_KEY = CommonConfiguration.class
			.getName() + ".elResolver";

	private BeanFactory beanFactory;

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(
				"classpath:META-INF/resources/");
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("/WEB-INF/messages");
		messageSource.setCacheSeconds(0);
		return messageSource;
	}

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	@Bean
	public MessageService messageService() {
		return new MessageServiceImpl();
	}

	@Bean
	public ServiceBean messageServiceBean() {
		ServiceBean bean = new ServiceBean();
		bean.setInterfaces(new Class[] { MessageService.class });
		bean.setTarget(messageService());
		return bean;
	}

	@Bean
	public OrderService orderService() {
		return new OrderServiceImpl();
	}

	@Bean
	public ServiceBean orderServiceBean() {
		ServiceBean bean = new ServiceBean();
		bean.setInterfaces(new Class[] { OrderService.class });
		bean.setTarget(orderService());
		return bean;
	}

	@Override
	protected void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
	}

	private class DynamicELResolver extends SpringBeanELResolver {

		private BeanFactory beanFactory;

		@Override
		protected BeanFactory getBeanFactory(ELContext elContext) {
			return this.beanFactory;
		}

		protected void setBeanFactory(BeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
		DynamicELResolver elResolver = (DynamicELResolver) servletContext
				.getAttribute(EL_RESOLVER_KEY);
		if (elResolver == null) {
			elResolver = new DynamicELResolver();
			JspFactory jspFactory = JspFactory.getDefaultFactory();
			JspApplicationContext jspContext = jspFactory
					.getJspApplicationContext(servletContext);
			jspContext.addELResolver(elResolver);
			servletContext.setAttribute(EL_RESOLVER_KEY, elResolver);
		}
		elResolver.setBeanFactory(this.beanFactory);
	}

}