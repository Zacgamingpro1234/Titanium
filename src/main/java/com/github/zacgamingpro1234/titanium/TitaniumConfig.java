package com.github.zacgamingpro1234.titanium;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown;
import org.polyfrost.oneconfig.api.config.v1.annotations.Info;

public final class TitaniumConfig extends Config {
	public static final TitaniumConfig INSTANCE = new TitaniumConfig();

	@Dropdown(title = "CPU Priority", options = {"Realtime", "High", "AboveNormal", "Normal", "BelowNormal", "Idle"})
	public static String CPUprio = "Above normal";

	@Info(title = "Realtime can only be applied when Minecraft is launched as Admin")
	private static boolean ign;

	private TitaniumConfig() {
		super("titaniumOrinthe.json", "Titanium", Category.PERFORMANCE);
	}
}
