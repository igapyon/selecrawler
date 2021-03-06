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

import java.io.IOException;

/**
 * Simple sample app for SeleCrawler.
 */
public class App {
	public static void main(final String[] args) throws IOException, InterruptedException {
		new App().process();
	}

	public void process() throws IOException {
		System.err.println("[jp.igapyon.selecrawler] Simple sample half-automated web crawler.");
		final SeleCrawlerSettings settings = new SeleCrawlerSettings();

		// settings.setDebug(true);
		// settings.setProcessIphone(false);

		// full path of chromedriver
		settings.setPathChromeDriver("../../chromedriver");

		// Google Chrome profile
		// /opt/google/chrome/chrome --user-data-dir=/home/user1/chromeprof
		settings.setPathUserDataDir("/home/user1/chromeprof");

		new SeleCrawlerWebContentGetter().process(settings);
		new SeleCrawlerWebContentNormalizer().process(settings);
		new SeleCrawlerWebContentTrimmer().process(settings);
		new SeleCrawlerWebContentAnalyzer().process(settings);
		new SeleCrawlerWebContentNewUrlFinder().process(settings);
	}
}
