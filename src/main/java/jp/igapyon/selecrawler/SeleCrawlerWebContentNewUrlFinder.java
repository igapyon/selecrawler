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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import jp.igapyon.diary.igapyonv3.util.SimpleDirParser;

public class SeleCrawlerWebContentNewUrlFinder {
	protected SeleCrawlerSettings settings = null;

	public void process(final SeleCrawlerSettings settings) throws IOException {
		this.settings = settings;
		System.err.println("[jp.igapyon.selecrawler] Find new url from fetched contents.");

		final List<String> urlList = new ArrayList<String>();
		processGatherUrl(urlList);

		Collections.sort(urlList);
		// remove duplicated urls
		for (int index = 1; index < urlList.size(); index++) {
			if (urlList.get(index - 1).equals(urlList.get(index))) {
				urlList.remove(index--);
			}
		}

		// remove if already registered.
		{
			final List<String> registeredList = FileUtils.readLines(new File(settings.getPathUrllisttTxt()), "UTF-8");
			final Map<String, String> registeredMap = new HashMap<String, String>();
			for (String urlRegistered : registeredList) {
				registeredMap.put(urlRegistered, urlRegistered);
			}
			for (int index = 0; index < urlList.size(); index++) {
				if (registeredMap.get(urlList.get(index)) != null) {
					// remove registered url.
					urlList.remove(index--);
				}
			}
		}

		// remove it if it is marked as exclude
		for (int index = 0; index < urlList.size(); index++) {
			final String url = urlList.get(index);
			final List<String> excludeRegexList = FileUtils
					.readLines(new File(settings.getPathUrllistExcludeRegexTxt()), "UTF-8");
			for (String regex : excludeRegexList) {
				final Pattern pat = Pattern.compile(regex);
				final Matcher mat = pat.matcher(url);

				if (mat.find()) {
					// remove registered url.
					urlList.remove(index--);
				}
			}
		}

		final File newurlcandidate = new File(new File(settings.getPathTargetDir()),
				SeleCrawlerConstants.EXT_SC_NEWURLCANDIDATE);
		System.err.println("[selecrawler] create/update new url candidate file: " + newurlcandidate.getCanonicalPath());
		FileUtils.writeLines(newurlcandidate, urlList);
	}

	public void processGatherUrl(final List<String> urlList) throws IOException {
		final List<File> files = new SimpleDirParser() {
			public boolean isProcessTarget(final File file) {
				if (file.isDirectory()) {
					return true;
				}
				if (file.getName().endsWith(SeleCrawlerConstants.EXT_SC_ANCHOR)) {
					return true;
				}
				return false;
			}
		}.listFiles(new File(settings.getPathTargetDir()), true);

		for (File fileAnchor : files) {
			if (fileAnchor.isDirectory()) {
				continue;
			}
			processGatherUrlFile(fileAnchor, urlList);
		}
	}

	public void processGatherUrlFile(final File fileAnchor, final List<String> urlList) throws IOException {
		final List<String> anchorList = FileUtils.readLines(fileAnchor, "UTF-8");
		for (String anchor : anchorList) {
			urlList.add(anchor);
		}
	}
}
