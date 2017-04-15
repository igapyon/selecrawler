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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SimpleChromeWrapper {
	public static final int WAIT_TIMEOUT_SECONDS = 60;

	protected WebDriver driver = null;
	protected String deviceName = null;
	protected String userDataDir = null;

	public SimpleChromeWrapper(final String driverPath, final String deviceName, final String userDataDir) {
		this.deviceName = deviceName;
		this.userDataDir = userDataDir;

		System.setProperty("webdriver.chrome.driver", driverPath);
		{
			final File fileDriver = new File(driverPath);
			if (fileDriver.exists() == false) {
				System.err.println("WARN: chrome driver file not found: " + driverPath);
			} else if (fileDriver.isFile() == false) {
				System.err.println("WARN: chrome driver file is not a file: " + driverPath);
			} else if (fileDriver.canExecute() == false) {
				System.err.println("WARN: chrome driver file seems not an executable file: " + driverPath);
			}
		}
	}

	public WebDriver getDriver() {
		if (driver == null) {
			open();
		}
		return driver;
	}

	public void open() {
		final DesiredCapabilities capabilities = getDesiredCapabilities();

		driver = new ChromeDriver(capabilities);

		driver.manage().timeouts().implicitlyWait(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
	}

	public void close() {
		driver.close();
	}

	protected DesiredCapabilities getDesiredCapabilities() {
		final DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		{
			{
				final ChromeOptions chromeOptions = new ChromeOptions();

				{
					final HashMap<String, Object> prefsMap = new HashMap<String, Object>();
					prefsMap.put("profile.default_content_settings.popups", 0);
					// prefsMap.put("download.default_directory", ???);
					chromeOptions.setExperimentalOption("prefs", prefsMap);
				}

				if ("ios".equals(deviceName)) {
					final Map<String, String> mobileEmulationMap = new HashMap<String, String>();
					mobileEmulationMap.put("deviceName", "Apple iPhone 6");
					chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulationMap);
				}

				if (userDataDir != null && userDataDir.trim().length() > 0) {
					chromeOptions.addArguments("user-data-dir=" + userDataDir);
				}

				capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
			}
		}

		{
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		}

		{
			final LoggingPreferences preferences = new LoggingPreferences();
			preferences.enable(LogType.BROWSER, Level.ALL);
			capabilities.setCapability(CapabilityType.LOGGING_PREFS, preferences);
		}
		return capabilities;
	}

	public List<String> getLogEntries() {
		final List<String> logEntriesList = new ArrayList<String>();
		final LogEntries logEntries = getDriver().manage().logs().get(LogType.BROWSER);
		for (LogEntry entry : logEntries) {
			logEntriesList.add("[" + entry.getLevel() + "] " + entry.getMessage());
		}
		return logEntriesList;
	}
}
