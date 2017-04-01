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

public class SeleCrawlerSettings {
	protected boolean isDebug = false;

	protected String pathChromeDriver = "../../chromedriver";

	protected String pathUrllisttTxt = "./meta/urllist.txt";
	protected String pathUrllistExcludeRegexTxt = "./meta/urllist-exclude-regex.txt";

	protected String pathTargetDir = "./target/selecrawler/";

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
}
