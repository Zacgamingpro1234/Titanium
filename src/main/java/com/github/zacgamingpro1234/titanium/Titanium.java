package com.github.zacgamingpro1234.titanium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.ornithemc.osl.entrypoints.api.ModInitializer;

public class Titanium implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Titanium");
	private static final TitaniumConfig CONFIG = TitaniumConfig.INSTANCE;

	@Override
	public void init() {
		LOGGER.info("Initializing Titanium!");
		CONFIG.preload();
	}
}
