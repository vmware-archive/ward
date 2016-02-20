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

package com.vmware.ward.runner;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rostislav Hristov
 */
public class RunnerIT {

	private HttpClient client;

	@Before
	public void before() {
		client = new DefaultHttpClient();
	}

	@After
	public void after() {
		client.getConnectionManager().shutdown();
	}

	@Test
	public void testLibraryVersion2() throws Exception {
		HttpResponse response = client.execute(new HttpGet(
				"http://localhost:9876/ward-tests-parent/"));
		assertEquals("2.0.0.RELEASE",
				EntityUtils.toString(response.getEntity()));
	}

	@Test
	public void testLibraryVersion1() throws Exception {
		HttpResponse response = client.execute(new HttpGet(
				"http://localhost:9876/ward-tests-child/"));
		assertEquals("1.0.0", EntityUtils.toString(response.getEntity()));
	}

}
