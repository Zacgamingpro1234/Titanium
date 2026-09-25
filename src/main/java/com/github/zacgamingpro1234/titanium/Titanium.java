package com.github.zacgamingpro1234.titanium;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import org.polyfrost.oneconfig.api.notifications.v1.Notification;
import org.polyfrost.oneconfig.api.notifications.v1.NotificationsManager;

import java.io.IOException;

public class Titanium implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Titanium");
	private static final TitaniumConfig CONFIG = TitaniumConfig.INSTANCE;

	@Override
	public void init() {
		LOGGER.info("Initializing Titanium!");
		CONFIG.preload();
		if (SystemUtils.IS_OS_WINDOWS) {
			try {
				String prio = TitaniumConfig.CPUprio;
				long pid = ProcessHandle.current().pid();
				ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-NoProfile", "-Command",
					"(Get-Process -Id "+pid+").PriorityClass = '"+prio+"'");
				pb.start().waitFor();
				LOGGER.info("Process Priority {} Applied to {}!", prio, pid);

			} catch (IOException | InterruptedException e) {
				LOGGER.error("Error while initializing Titanium!", e);
			}
		}
	}
}
