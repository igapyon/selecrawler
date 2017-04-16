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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import jp.igapyon.selecrawler.util.SimpleChromeWrapper;

public class SeleCrawlerWebContentGetter {
	protected SeleCrawlerSettings settings = null;

	public void process(final SeleCrawlerSettings settings) throws IOException {
		this.settings = settings;
		System.err.println("[jp.igapyon.selecrawler] Fetching web contents using Chrome.");

		// process for each device.
		if (settings.isProcessMac()) {
			processDevice("mac");
		}
		if (settings.isProcessIphone()) {
			processDevice("iphone");
		}
	}

	public void processDevice(final String deviceName) throws IOException {
		System.err.println("[selecrawler] Launch Chrome. UA:" + deviceName);
		final SimpleChromeWrapper chrome = new SimpleChromeWrapper(settings.getPathChromeDriver(), deviceName,
				settings.getPathUserDataDir());
		chrome.open();

		int getcounter = 0;

		System.err.println(
				"[selecrawler] Load url list file: " + new File(settings.getPathUrllisttTxt()).getCanonicalPath());
		System.err.println("[selecrawler] Target dir: " + new File(settings.getPathTargetDir()).getCanonicalPath());

		final List<String> urls = FileUtils.readLines(new File(settings.getPathUrllisttTxt()), "UTF-8");
		for (String urlLookup : urls) {
			if (getcounter >= 10) {
				// refresh chrome instance
				getcounter = 0;
				chrome.close();
				chrome.open();
			}

			final File outputFile = getFileHtml(deviceName, urlLookup);
			if (outputFile.getParentFile().exists() == false) {
				outputFile.getParentFile().mkdirs();
			}

			final File outputMetaFile = new File(outputFile.getParentFile(),
					outputFile.getName() + SeleCrawlerConstants.EXT_SC_URL);

			final File outputLogFile = new File(outputFile.getParentFile(),
					outputFile.getName() + SeleCrawlerConstants.EXT_SC_LOG);

			if (outputMetaFile.exists()) {
				if (settings.isDebug()) {
					System.err.println("[selecrawler] skip(cache): " + urlLookup);
				}
				continue;
			}

			System.err.println("[selecrawler] fetch web: " + urlLookup);

			chrome.getDriver().get(urlLookup);

			{
				// check wait settings.
				final String urlActual = chrome.getDriver().getCurrentUrl();
				for (String regex : settings.getUrllistWaitRegex()) {
					final Pattern pat = Pattern.compile(regex);
					final Matcher mat = pat.matcher(urlActual);
					if (mat.find()) {
						try {
							System.out.println("waiting browser operation");
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}

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

	public File getFileHtml(final String deviceName, final String urlLookup) throws IOException {
		final URL url = new URL(urlLookup);
		final String serverhostname = url.getHost();
		String path = url.getPath();
		if (path.length() == 0 || path.equals("/") || path.endsWith("/")) {
			path = path + "/index.html";
		}

		return new File(settings.getPathTargetDir() + deviceName + "/" + serverhostname + path);
	}
}