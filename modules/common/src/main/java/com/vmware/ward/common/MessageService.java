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

import java.util.Locale;

import org.springframework.context.NoSuchMessageException;

/**
 * An interface that defines how applications can expose messages.
 * 
 * @author Rostislav Hristov
 */
public interface MessageService {

	/**
	 * Provides a message by key.
	 * 
	 * @param key
	 * @return Localized message
	 */
	String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException;

}
