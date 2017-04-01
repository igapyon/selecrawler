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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import jp.igapyon.selecrawler.util.SimpleChromeWrapper;

public class SeleCrawlerWebContentGetter {
	public static final boolean IS_DEBUG = false;

	public static final String DEFAULT_CHROMEDRIVER_PATH = "../../chromedriver";

	public static final String URLLIST_TXT = "./meta/urllist.txt";
	public static final String URLLIST_EXCLUDE_REGEX_TXT = "./meta/urllist-exclude-regex.txt";

	public static final String TARGET_DIR = "./target/seleniumutil/";

	public void process() throws IOException {
		System.err.println("[jp.igapyon.selecrawler] Fetching web contents using Chrome.");

		// process for each device.
		processDevice("mac");
		processDevice("ios");
	}

	public void processDevice(final String deviceName) throws IOException {
		System.err.println("[selecrawler] Launch Chrome. UA:" + deviceName);
		final SimpleChromeWrapper chrome = new SimpleChromeWrapper(DEFAULT_CHROMEDRIVER_PATH, deviceName);
		chrome.open();

		int getcounter = 0;

		System.err.println("[selecrawler] Load url list file: " + new File(URLLIST_TXT).getCanonicalPath());
		System.err.println("[selecrawler] Target dir: " + new File(TARGET_DIR).getCanonicalPath());

		final List<String> urls = FileUtils.readLines(new File(URLLIST_TXT), "UTF-8");
		for (String urlLookup : urls) {
			if (getcounter >= 10) {
				// refresh chrome instance
				getcounter = 0;
				chrome.close();
				chrome.open();
			}

			final URL url = new URL(urlLookup);
			final String serverhostname = url.getHost();
			String path = url.getPath();
			if (path.length() == 0 || path.equals("/") || path.endsWith("/")) {
				path = path + "/index.html";
			}

			final File outputFile = new File(TARGET_DIR + deviceName + "/" + serverhostname + path);
			if (outputFile.getParentFile().exists() == false) {
				outputFile.getParentFile().mkdirs();
			}

			final File outputMetaFile = new File(
					TARGET_DIR + deviceName + "/" + serverhostname + path + SeleCrawlerConstants.EXT_SC_URL);

			final File outputLogFile = new File(
					TARGET_DIR + deviceName + "/" + serverhostname + path + SeleCrawlerConstants.EXT_SC_LOG);

			if (outputMetaFile.exists()) {
				if (IS_DEBUG)
					System.err.println("[selecrawler] skip(cache): " + urlLookup);
				continue;
			}

			System.err.println("[selecrawler] fetch web: " + urlLookup);

			chrome.getDriver().get(urlLookup);
			final String contents = chrome.getDriver().getPageSource();
			FileUtils.writeStringToFile(outputFile, contents, "UTF-8");

			FileUtils.writeLines(outputLogFile, "UTF-8", chrome.getLogEntries());

			// write meta finally.
			{
				final List<String> metaUrlList = new ArrayList<String>();
				metaUrlList.add(urlLookup);
				metaUrlList.add(chrome.getDriver().getCurrentUrl());
				FileUtils.writeLines(outputMetaFile, "UTF-8", metaUrlList);
			}
			getcounter++;

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		chrome.close();
	}
}