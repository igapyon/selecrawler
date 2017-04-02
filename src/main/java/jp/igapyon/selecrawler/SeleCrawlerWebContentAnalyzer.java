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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import jp.igapyon.diary.igapyonv3.util.SimpleDirParser;
import jp.igapyon.selecrawler.util.SimpleMyXmlUtil;

public class SeleCrawlerWebContentAnalyzer {
	protected SeleCrawlerSettings settings = null;

	public void process(final SeleCrawlerSettings settings) throws IOException {
		this.settings = settings;
		System.err.println("[jp.igapyon.selecrawler] Analyze web contents.");

		final List<File> files = new SimpleDirParser() {
			public boolean isProcessTarget(final File file) {
				if (file.isDirectory()) {
					return true;
				}
				if (file.getName().endsWith(SeleCrawlerConstants.EXT_SC_URL)) {
					return true;
				}
				return false;
			}
		}.listFiles(new File(settings.getPathTargetDir()), true);

		System.err.println("[selecrawler] create/update '*" + SeleCrawlerConstants.EXT_SC_HEAD + "' and '*"
				+ SeleCrawlerConstants.EXT_SC_ANCHOR + "' files.");
		for (File fileMeta : files) {
			if (fileMeta.isDirectory()) {
				continue;
			}
			final List<String> metaUrlList = FileUtils.readLines(fileMeta, "UTF-8");

			final File file = new File(fileMeta.getParentFile(), fileMeta.getName().substring(0,
					fileMeta.getName().length() - SeleCrawlerConstants.EXT_SC_URL.length()));

			processFile(file, metaUrlList.get(1));
		}
	}

	public void processFile(final File file, final String urlString) throws IOException {
		final List<String> anchorList = new ArrayList<String>();
		final List<String> headList = new ArrayList<String>();
		final List<String> scriptSrcList = new ArrayList<String>();

		final String contents = FileUtils.readFileToString(
				new File(file.getParentFile(), file.getName() + SeleCrawlerConstants.EXT_SC_NORMAL), "UTF-8");

		final Document document = SimpleMyXmlUtil.string2Document(contents);
		{
			final String title = SimpleMyXmlUtil.getXPathString(document, "/html/head/title/text()");
			if (title != null && title.trim().length() > 0) {
				headList.add("title: " + title);
			}
		}

		{
			final NodeList nodes = SimpleMyXmlUtil.getXPathNodeList(document, "/html/head/meta");
			for (int index = 0; index < nodes.getLength(); index++) {
				if (nodes.item(index) instanceof Element) {
					final Element eleMeta = (Element) nodes.item(index);
					headList.add("meta:");

					final NamedNodeMap nnm = eleMeta.getAttributes();
					for (int indexNnm = 0; indexNnm < nnm.getLength(); indexNnm++) {
						final Attr attr = (Attr) nnm.item(indexNnm);

						headList.add("  " + attr.getName() + " [" + attr.getValue() + "]");
					}
				}
			}
		}

		{
			final NodeList nodes = SimpleMyXmlUtil.getXPathNodeList(document, "/html/head/link");
			for (int index = 0; index < nodes.getLength(); index++) {
				if (nodes.item(index) instanceof Element) {
					final Element eleMeta = (Element) nodes.item(index);
					headList.add("link:");

					final NamedNodeMap nnm = eleMeta.getAttributes();
					for (int indexNnm = 0; indexNnm < nnm.getLength(); indexNnm++) {
						final Attr attr = (Attr) nnm.item(indexNnm);

						headList.add("  " + attr.getName() + " [" + attr.getValue() + "]");
					}
				}
			}
		}

		{
			// search anchor with href
			final NodeList nodes = SimpleMyXmlUtil.getXPathNodeList(document, "//a");
			for (int index = 0; index < nodes.getLength(); index++) {
				if (nodes.item(index) instanceof Element) {
					final Element eleAnchor = (Element) nodes.item(index);
					String href = eleAnchor.getAttribute("href");
					href = adjustAnchorUrl(href, urlString);
					if (href == null) {
						continue;
					}
					anchorList.add(href);
				}
			}
		}

		{
			// search script
			final NodeList nodes = SimpleMyXmlUtil.getXPathNodeList(document, "//script");
			for (int index = 0; index < nodes.getLength(); index++) {
				if (nodes.item(index) instanceof Element) {
					final Element eleScript = (Element) nodes.item(index);
					String srcString = eleScript.getAttribute("src");
					srcString = adjustAnchorUrl(srcString, urlString);
					if (srcString == null) {
						continue;
					}
					scriptSrcList.add(srcString);
				}
			}
		}

		{
			final File fileMetaAnchor = new File(file.getParentFile(),
					file.getName() + SeleCrawlerConstants.EXT_SC_ANCHOR);
			FileUtils.writeLines(fileMetaAnchor, "UTF-8", anchorList);
		}

		{
			final File fileMetaHead = new File(file.getParentFile(), file.getName() + SeleCrawlerConstants.EXT_SC_HEAD);
			FileUtils.writeLines(fileMetaHead, "UTF-8", headList);
		}

		{
			final File fileScriptSrc = new File(file.getParentFile(),
					file.getName() + SeleCrawlerConstants.EXT_SC_SCRIPTSRC);
			FileUtils.writeLines(fileScriptSrc, "UTF-8", scriptSrcList);
		}
	}

	public static String adjustAnchorUrl(String href, String urlString) throws MalformedURLException {
		if (href.trim().length() == 0) {
			return null;
		}
		if (href.startsWith("#")) {
			return null;
		}

		if (href.startsWith("http") == false) {
			if (href.startsWith("//")) {
				final URL url = new URL(urlString);
				href = url.getProtocol() + ":" + href;
			} else if (href.startsWith("/")) {
				final URL url = new URL(urlString);
				final String hostpart = url.getProtocol() + "://" + url.getHost();
				href = hostpart + href;
			} else if (href.startsWith(".")) {
				if (urlString.endsWith("/")) {
					href = urlString + href;
				} else {
					href = urlString.substring(0, urlString.lastIndexOf('/') + 1) + href;
				}
			} else {
				// これは結構難しい。
				if (urlString.endsWith("/")) {
					// 単に結合。
					href = urlString + href;
				} else {
					href = urlString.substring(0, urlString.lastIndexOf('/') + 1) + href;
				}
			}
		}

		return href;
	}
}
