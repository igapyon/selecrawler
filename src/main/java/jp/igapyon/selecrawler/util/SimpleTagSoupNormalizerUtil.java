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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

class SimpleTagSoupNormalizerUtil {
	public static String normalizeHtml(final String source) throws IOException {
		try {
			final XMLReader parser = new Parser();

			final HTMLSchema schema = new HTMLSchema();
			parser.setProperty(Parser.schemaProperty, schema);

			final StringWriter output = new StringWriter();

			final XMLWriter serializer = new XMLWriter(output);
			parser.setContentHandler(serializer);
			parser.setFeature(Parser.namespacesFeature, false);

			final InputSource input = new InputSource();
			input.setCharacterStream(new StringReader(source));

			serializer.setOutputProperty(XMLWriter.METHOD, "xhtml");
			serializer.setOutputProperty(XMLWriter.OMIT_XML_DECLARATION, "yes");
			parser.setFeature(Parser.defaultAttributesFeature, false);
			serializer.setOutputProperty(XMLWriter.ENCODING, "UTF-8");
			parser.setFeature("http://www.ccil.org/~cowan/tagsoup/features/ignore-bogons", false);

			parser.parse(input);

			return output.toString();
		} catch (SAXException ex) {
			throw new IOException(ex);
		}
	}
}
