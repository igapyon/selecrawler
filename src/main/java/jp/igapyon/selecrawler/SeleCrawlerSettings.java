/*
 *  selecrawler
 *  Copyright (C) 2017  Toshiki Iga
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 *  Copyright 2017 Toshiki Iga
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jp.igapyon.selecrawler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class SeleCrawlerSettings {
	protected boolean isDebug = false;

	private boolean isProcessMac = true;
	private boolean isProcessIphone = true;

	protected String pathChromeDriver = "./chromedriver";

	protected String pathUrllisttTxt = "./meta/urllist.txt";
	protected String pathUrllistExcludeRegexTxt = "./meta/urllist-exclude-regex.txt";
	protected String pathUrllistWaitRegexTxt = "./meta/urllist-wait-regex.txt";

	/**
	 * Chrome user-data-dir
	 * 
	 * ex: /home/user1/mychromedir
	 * 
	 * prepare settings: <code> 
	 * /opt/google/chrome/chrome --user-data-dir=mychromedir
	 * </code>
	 */
	protected String pathUserDataDir = null;

	protected String pathTargetDir = "./target/selecrawler/";

	private List<String> waitRegexList = null;

	public List<String> getUrllistWaitRegex() throws IOException {
		if (waitRegexList == null) {
			waitRegexList = FileUtils.readLines(new File(getPathUrllistWaitRegexTxt()), "UTF-8");
		}
		return waitRegexList;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public String getPathChromeDriver() {
		return pathChromeDriver;
	}

	public void setPathChromeDriver(String pathChromeDriver) {
		this.pathChromeDriver = pathChromeDriver;
	}

	public String getPathUrllisttTxt() {
		return pathUrllisttTxt;
	}

	public void setPathUrllisttTxt(String pathUrllisttTxt) {
		this.pathUrllisttTxt = pathUrllisttTxt;
	}

	public String getPathUrllistExcludeRegexTxt() {
		return pathUrllistExcludeRegexTxt;
	}

	public void setPathUrllistExcludeRegexTxt(String pathUrllistExcludeRegexTxt) {
		this.pathUrllistExcludeRegexTxt = pathUrllistExcludeRegexTxt;
	}

	public String getPathTargetDir() {
		return pathTargetDir;
	}

	public void setPathTargetDir(String pathTargetDir) {
		this.pathTargetDir = pathTargetDir;
	}

	public String getPathUrllistWaitRegexTxt() {
		return pathUrllistWaitRegexTxt;
	}

	public void setPathUrllistWaitRegexTxt(String pathUrllistWaitRegexTxt) {
		this.pathUrllistWaitRegexTxt = pathUrllistWaitRegexTxt;
	}

	public String getPathUserDataDir() {
		return pathUserDataDir;
	}

	/**
	 * Chrome user-data-dir
	 * 
	 * prepare settings: <code> 
	 * /opt/google/chrome/chrome --user-data-dir=/home/user1/chromeprof
	 * </code>
	 * 
	 * @param pathUserDataDir
	 *            ex: /home/user1/chromeprof
	 */
	public void setPathUserDataDir(String pathUserDataDir) {
		this.pathUserDataDir = pathUserDataDir;
	}

	public boolean isProcessMac() {
		return isProcessMac;
	}

	public void setProcessMac(boolean isProcessMac) {
		this.isProcessMac = isProcessMac;
	}

	public boolean isProcessIphone() {
		return isProcessIphone;
	}

	public void setProcessIphone(boolean isProcessIphone) {
		this.isProcessIphone = isProcessIphone;
	}
}
