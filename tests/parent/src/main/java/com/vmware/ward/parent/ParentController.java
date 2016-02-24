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

package com.vmware.ward.parent;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vmware.ward.core.ListBean;
import com.vmware.ward.library.LibraryService;

/**
 * @author Rostislav Hristov
 */
@Controller
public class ParentController {

	@Autowired
	private ListBean<LibraryService> libraryServices;

	@RequestMapping("/*")
	public void printLibraryVersion(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		res.getOutputStream().print(
				libraryServices.get(0).getLibrary().getVersion().toString());
		res.getOutputStream().flush();
		res.getOutputStream().close();
	}

}
