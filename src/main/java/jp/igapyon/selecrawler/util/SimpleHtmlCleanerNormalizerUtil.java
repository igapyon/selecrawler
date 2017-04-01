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

package jp.igapyon.selecrawler.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

class SimpleHtmlCleanerNormalizerUtil {
	public static String normalizeHtml(final String source) throws IOException {
		final CleanerProperties props = new CleanerProperties();
		props.setOmitDoctypeDeclaration(true);
		props.setKeepWhitespaceAndCommentsInHead(true);
		props.setOmitComments(false);

		final TagNode tagNode = new HtmlCleaner(props).clean(new StringReader(source));

		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		new PrettyXmlSerializer(props).writeToStream(tagNode, outStream, "UTF-8");
		outStream.flush();

		return new String(outStream.toByteArray(), "UTF-8");
	}
}
