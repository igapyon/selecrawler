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

import jp.igapyon.diary.igapyonv3.util.SimpleDirParser;
import jp.igapyon.selecrawler.util.SimpleHtmlNormalizerUtil;

public class SeleCrawlerWebContentNormalizer {
	public static boolean IS_DEBUG = false;

	protected SeleCrawlerSettings settings = null;

	public void process(final SeleCrawlerSettings settings) throws IOException {
		this.settings = settings;
		System.err.println("[jp.igapyon.selecrawler] Normalize web contents.");

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

		System.err.println("[selecrawler] create/update '*" + SeleCrawlerConstants.EXT_SC_NORMALIZED + "' files.");
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
		if (IS_DEBUG)
			System.err.println(file.getCanonicalPath());

		String contents = FileUtils.readFileToString(file, "UTF-8");
		contents = SimpleHtmlNormalizerUtil.normalizeHtml(contents);

		final File fileNormalized = new File(file.getParentFile(),
				file.getName() + SeleCrawlerConstants.EXT_SC_NORMALIZED);
		FileUtils.writeStringToFile(fileNormalized, contents, "UTF-8");
	}
}
