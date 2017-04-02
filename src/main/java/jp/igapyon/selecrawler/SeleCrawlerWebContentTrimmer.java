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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jp.igapyon.diary.igapyonv3.util.SimpleDirParser;
import jp.igapyon.selecrawler.util.SimpleHtmlCleanerNormalizerUtil;
import jp.igapyon.selecrawler.util.SimpleHtmlNormalizerUtil;
import jp.igapyon.selecrawler.util.SimpleMyXmlUtil;

public class SeleCrawlerWebContentTrimmer {
	protected SeleCrawlerSettings settings = null;

	public void process(final SeleCrawlerSettings settings) throws IOException {
		this.settings = settings;
		System.err.println("[jp.igapyon.selecrawler] Trim web contents.");

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

		System.err.println("[selecrawler] create/update '*" + SeleCrawlerConstants.EXT_SC_NORMAL_TRIM + "' files.");
		for (File fileMeta : files) {
			if (fileMeta.isDirectory()) {
				continue;
			}

			final File file = new File(fileMeta.getParentFile(), fileMeta.getName().substring(0,
					fileMeta.getName().length() - SeleCrawlerConstants.EXT_SC_URL.length()));

			processFile(file);
		}
	}

	public void processFile(final File file) throws IOException {
		String contents = FileUtils.readFileToString(file, "UTF-8");
		contents = SimpleHtmlNormalizerUtil.normalizeHtml(contents);

		final Document document = SimpleMyXmlUtil.string2Document(contents);

		final Element elementRoot = document.getDocumentElement();

		processElement(elementRoot);

		try {
			// write xml
			final Transformer transformer = TransformerFactory.newInstance().newTransformer();
			final DOMSource source = new DOMSource(elementRoot);
			final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			final StreamResult target = new StreamResult(outStream);
			transformer.transform(source, target);

			outStream.flush();

			final File fileNormalTrim = new File(file.getParentFile(),
					file.getName() + SeleCrawlerConstants.EXT_SC_NORMAL_TRIM);
			FileUtils.writeByteArrayToFile(fileNormalTrim,
					SimpleHtmlCleanerNormalizerUtil.normalizeHtml(outStream.toByteArray()));
		} catch (TransformerConfigurationException ex) {
			throw new IOException(ex);
		} catch (TransformerFactoryConfigurationError ex) {
			throw new IOException(ex);
		} catch (TransformerException ex) {
			throw new IOException(ex);
		}
	}

	public void processElement(final Element element) throws IOException {
		final NodeList nodeList = element.getChildNodes();
		for (int index = nodeList.getLength() - 1; index >= 0; index--) {
			final Node node = nodeList.item(index);
			if (node instanceof Element) {
				final Element lookup = (Element) node;

				if ("script".equals(lookup.getTagName())) {
					// REMOVE script tag.
					element.removeChild(node);
					continue;
				}

				if ("noscript".equals(lookup.getTagName())) {
					// REMOVE noscript tag.
					element.removeChild(node);
					continue;
				}

				if ("iframe".equals(lookup.getTagName())) {
					final NamedNodeMap nnm = lookup.getAttributes();
					for (int indexNnm = 0; indexNnm < nnm.getLength(); indexNnm++) {
						final Attr attr = (Attr) nnm.item(indexNnm);

						// System.out.println(" " + attr.getName() + " [" +
						// attr.getValue() + "]");
						if ("style".equals(attr.getName())) {
							final String value = attr.getValue().replaceAll(" ", "");
							if (value.indexOf("display:none") >= 0) {
								// REMOVE iframe tag which is display:none
								// style..
								element.removeChild(node);
								continue;
							}
						}
					}
				}

				processElement(lookup);
			}
		}
	}
}
