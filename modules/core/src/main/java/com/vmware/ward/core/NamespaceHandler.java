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

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@link NamespaceHandler} for the Ward configuration namespace.
 * @author Rostislav Hristov
 */
public class NamespaceHandler extends NamespaceHandlerSupport {

	private static final String BEAN_NAME = "bean-name";
	private static final String SERVICE_BEAN_NAME = "serviceBeanName";
	private static final String ORDER = "order";
	private static final String RANKING = "ranking";
	private static final String REF = "ref";
	private static final String TARGET_BEAN_NAME = "targetBeanName";
	private static final String TIMEOUT = "timeout";
	private static final String INTERFACE = "interface";
	private static final String INTERFACES = "interfaces";

	@Override
	public void init() {
		registerBeanDefinitionParser("service",
				new ServiceBeanDefinitionParser());
		registerBeanDefinitionParser("reference",
				new ReferenceBeanDefinitionParser());
		registerBeanDefinitionParser("list",
				new ListBeanDefinitionParser());
	}

	private class BeanDefinitionParser extends
			AbstractSingleBeanDefinitionParser {

		@Override
		protected boolean shouldGenerateIdAsFallback() {
			return true;
		}

		@Override
		protected void doParse(Element element, ParserContext parserContext,
				BeanDefinitionBuilder builder) {

			builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

			Set<Object> interfaces = new HashSet<Object>();
			NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node instanceof Element) {
					if (INTERFACES.equals(node.getLocalName())) {
						NodeList subnodes = node.getChildNodes();
						for (int j = 0; j < subnodes.getLength(); j++) {
							String content = subnodes.item(j).getTextContent();
							if (StringUtils.hasText(content)) {
								interfaces.add(StringUtils
										.trimWhitespace(content));
							}
						}
					}
				}
			}

			if (element.hasAttribute(INTERFACE)) {
				if (interfaces.size() != 0) {
					parserContext
							.getReaderContext()
							.error("Either 'interface' attribute or <intefaces> sub-element has to be specified.",
									element);
				} else {
					interfaces.add(element.getAttribute(INTERFACE));
				}
			}

			builder.addPropertyValue(INTERFACES, interfaces);
		}

	}

	private class ServiceBeanDefinitionParser extends
			BeanDefinitionParser {

		@Override
		protected Class<?> getBeanClass(Element element) {
			return ServiceBean.class;
		}

		@Override
		protected void doParse(Element element, ParserContext parserContext,
				BeanDefinitionBuilder builder) {

			super.doParse(element, parserContext, builder);

			if (element.hasAttribute(REF)) {
				builder.addPropertyValue(TARGET_BEAN_NAME,
						new RuntimeBeanReference(element.getAttribute(REF))
								.getBeanName());
			}

			if (element.hasAttribute(RANKING)) {
				builder.addPropertyValue(ORDER,
						Integer.valueOf(element.getAttribute(RANKING)));
			}
		}

	}

	private class ReferenceBeanDefinitionParser extends
			BeanDefinitionParser {

		@Override
		protected Class<?> getBeanClass(Element element) {
			return ReferenceBean.class;
		}

		@Override
		protected void doParse(Element element, ParserContext parserContext,
				BeanDefinitionBuilder builder) {

			super.doParse(element, parserContext, builder);

			if (element.hasAttribute(BEAN_NAME)) {
				builder.addPropertyValue(SERVICE_BEAN_NAME,
						new RuntimeBeanReference(element.getAttribute(BEAN_NAME))
								.getBeanName());
			}
			if (element.hasAttribute(TIMEOUT)) {
				builder.addPropertyValue(TIMEOUT,
						new RuntimeBeanReference(element.getAttribute(TIMEOUT))
								.getBeanName());
			}
		}
	}

	private class ListBeanDefinitionParser extends
			BeanDefinitionParser {

		@Override
		protected Class<?> getBeanClass(Element element) {
			return ListBean.class;
		}

	}
}