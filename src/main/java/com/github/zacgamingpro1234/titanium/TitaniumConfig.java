package com.github.zacgamingpro1234.titanium;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown;

public final class TitaniumConfig extends Config {
	public static final TitaniumConfig INSTANCE = new TitaniumConfig();

	@Dropdown(title = "CPU Priority", options = {"Realtime", "High", "Above normal", "Normal", "Below normal", "Idle"})
	public static String CPUprio = "Above normal";

	private TitaniumConfig() {
		super("titaniumOrinthe.json", "Titanium", Category.PERFORMANCE);
	}
}
