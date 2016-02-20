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

/**
 * @author Rostislav Hristov
 */
public class Version {

	private int major = 0;
	private int minor = 0;
	private int revision = 0;

	public Version(double version) {
		this(String.valueOf(version));
	}

	public Version(String version) {

		String[] parts = version.split("\\.");

		if (parts.length == 0) {
			return;
		}
		this.major = Integer.parseInt(parts[0]);
		if (this.major < 0) {
			this.major = 0;
		}

		if (parts.length == 1) {
			return;
		}
		this.minor = Integer.parseInt(parts[1]);
		if (this.minor < 0) {
			this.minor = 0;
		}

		if (parts.length == 2) {
			return;
		}
		this.revision = Integer.parseInt(parts[2]);
		if (this.revision < 0) {
			this.revision = 0;
		}
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public String toString() {
		return String.format("%s.%s.%s", major, minor, revision);
	}

}