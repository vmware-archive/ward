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

package com.vmware.ward.library;

import com.vmware.ward.shared.DataService;

/**
 * @author Rostislav Hristov
 */
public class LibraryServiceImpl implements LibraryService {

	private Library library = new Library();

	public LibraryServiceImpl(DataService dataService) {
		dataService.getData();
	}

	public Library getLibrary() {
		return library;
	}

}
