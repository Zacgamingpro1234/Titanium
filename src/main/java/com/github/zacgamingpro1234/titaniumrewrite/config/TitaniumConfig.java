package com.github.zacgamingpro1234.titaniumrewrite.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import cc.polyfrost.oneconfig.utils.Notifications;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import com.github.zacgamingpro1234.titaniumrewrite.Titaniumod;
import com.github.zacgamingpro1234.titaniumrewrite.hud.*;

public class TitaniumConfig extends Config {
    /// /////////////////////////////////////////GENERAL////////////////////////////////////////
    @Switch(
            name = "Bypass OS" //Since There Is No Category Value It Will Default To General
    )
    public static boolean BypassOS = false;

    @Button(
            name = "Current OS:",    // name beside the button
            text = "Show"        // text on the button itself
    )
    Runnable runnable = () -> {    // using a lambda to create the runnable interface.
        Notifications.INSTANCE.send("Titanium Rewrite", "Your Current OS is " + Titaniumod.osinfo);
    };

    @Dropdown(
            name = "Game CPU Priority On Startup",
            options = {"High", "Above Normal", "Normal/Default", "Below Normal"},
            size = 2
    )
    public static int Prio = 1;        // default option

    @Button(
            name = "Apply Priority Now",    // name beside the button
            size = OptionSize.DUAL,
            text = "Apply"        // text on the button itself
    )
    Runnable runnable1 = Titaniumod::launchPrioritySetter;

    @Info(
            text = "Enabling the Ultimate Powerplan will use more power and drain battery faster for more performance. It will also increase device temperatures.",
            type = InfoType.WARNING, // Types are: INFO, WARNING, ERROR, SUCCESS
            size = 69
    )
    public static boolean ignored6; // Useless

    @Button(
            name = "Create & Enable Ultimate Powerplan",    // name beside the button
            text = "Enable"        // text on the button itself
    )
    Runnable runnable2 = Titaniumod::enableultPwrPln;

    @Button(
            name = "Revert back to your original Powerplan",    // name beside the button
            text = "Revert"        // text on the button itself
    )
    Runnable runnable3 = Titaniumod::revertpwrpln;

    @Dropdown(
            name = "PowerPlan Selector",
            options = {"N/A 1", "N/A 2", "N/A 3", "N/A 4"},
            size = 2
    )
    public static int PPS = 0;        // default option

    @Info(
            text = "This Utility Requires Admin Permissions",
            type = InfoType.WARNING, // Types are: INFO, WARNING, ERROR, SUCCESS
            size = 5
    )
    public static boolean ignored7; // Useless

    @Button(
            name = "Launch The Ultimate Windows Utility by Chris Titus Tech",    // name beside the button
            size = OptionSize.DUAL,
            text = "Launch"        // text on the button itself
    )
    Runnable runnable4 = Titaniumod::CTwinUtil;

    @Switch(
            name = "Warn via Notification if certain CPU temps are reached",
            size = 2
    )
    public static boolean CPUwarn = false;

    @Slider(
            name = "Temperature Limit",
            min = 40f, max = 110f
    )
    public static float templimitCPU = 85f; // default value

    @Switch(
            name = "Warn via Notification if certain GPU temps are reached",
            size = 2
    )
    public static boolean GPUwarn = true;

    @Slider(
            name = "Temperature Limit",
            min = 40f, max = 110f
    )
    public static float templimitGPU = 85f; // default value

    @Switch(
            name = "Warn via Notification if certain RAM Usages are reached",
            size = 2
    )
    public static boolean RAMwarn = true;

    @Slider(
            name = "RAM Left To Warn",
            min = 0f, max = 4096f
    )
    public static float RAMLeftLimit = 690; // default value

    @Switch(
            name = "Warn via Notification if the battery is lower than a certain percent",
            size = 2
    )
    public static boolean Batterywarn = false;

    @Slider(
            name = "Battery Percent Limit",
            min = .01f, max = 100f
    )
    public static float percentMinimum = 15f; // default value

    /// /////////////////////////////////////////HUD////////////////////////////////////////

    @Slider(
            name = "The Amount Of Ticks To Wait Until An Update",
            min = 5f, max = 40,
            category = "HUD"
    )
    public static int waitick = 10; // default value

    @Info(
            text = "To Use CPU Temps You Must Launch The Game As Admin",
            type = InfoType.WARNING, // Types are: INFO, WARNING, ERROR, SUCCESS
            category = "HUD",
            size = 5
    )
    public static boolean ignored9; // Useless

    @HUD(
            name = "CPU Temps",
            category = "HUD"
    )
    public transient CPUTemps hud = new CPUTemps();

    @HUD(
            name = "GPU Temps",
            category = "HUD"
    )
    public transient GPUTemps hud2 = new GPUTemps();

/*  @HUD(
            name = "GPU Vram",
            category = "HUD"
    )
    public transient GPUVram hud3 = new GPUVram();*/

    @HUD(
            name = "RAM Usage",
            category = "HUD"
    )
    public transient RAMUsage hud4 = new RAMUsage();

    @HUD(
            name = "Battery Percent",
            category = "HUD"
    )
    public transient BatteryLife hud5 = new BatteryLife();

    /// /////////////////////////////////////////CMD////////////////////////////////////////

    @Info(
            text = "Advanced Users Only!",
            category = "Debug",
            type = InfoType.ERROR, // Types are: INFO, WARNING, ERROR, SUCCESS
            size = 5
    )
    public static boolean ignored; // Useless

    @Info(
            text = "CMD Options",
            category = "Debug",
            type = InfoType.INFO, // Types are: INFO, WARNING, ERROR, SUCCESS
            size = 5
    )
    public static boolean ignored5; // Useless

//startup

    @Header(
            text = "Startup",
            category = "Debug",
            size = 3
    )
    public static boolean ignored4; // Useless

    @Switch(
            name = "Enable Startup",
            category = "Debug", //Give It A Category
            size = 2
    )
    public static boolean lCMDtUP = false; // default value, launchCMDtoggleStartup

    @Switch( //Run As Admin Switch Option, To Use In Your Code Add TitaniumConfig.RAACMDUP, why RAACMDUP? Because it's the Boolean Value Below
            name = "Run As Administrator On Startup",
            category = "Debug", //Give It A Category
            size = 2
    )
    public static boolean RAACMDUP = false; // default value, Run As Admin CommandPrompt Startup

//shutdown

    @Header(
            text = "Shutdown",
            category = "Debug",
            size = 3
    )
    public static boolean ignored0; // Useless

    @Switch(
            name = "Enable Shutdown",
            category = "Debug", //Give It A Category
            size = 2
    )
    public static boolean lCMDtDOWN = false; // default value, launchCMDtoggleShutdown

    @Switch( //Run As Admin Switch Option
            name = "Run As Administrator On Shutdown",
            category = "Debug", //Give It A Category
            size = 2
    )
    public static boolean RAACMDDOWN = false; // default value, Run As Admin CommandPrompt Startup

    /// /////////////////////////////////////////POWERSHELL////////////////////////////////////////

    @Info(
            text = "PS Options",
            category = "Debug",
            type = InfoType.INFO, // Types are: INFO, WARNING, ERROR, SUCCESS
            size = 5
    )
    public static boolean ignored1; // Useless

//startup

    @Header(
            text = "Startup",
            category = "Debug",
            size = 3
    )
    public static boolean ignored2; // Useless

    @Switch(
            name = "Enable Startup",
            category = "Debug", //Give It A Category
            size = 2
    )
    public static boolean lPStUP = false; // default value, launchPStoggleStartup

    @Switch( //Run As Admin Switch Option
            name = "Run As Administrator On Startup",
            category = "Debug", //Give It A Category
            size = 2
    )
    public static boolean RAAPSUP = false; // default value, Run As Admin Powershell Startup

//shutdown

    @Header(
            text = "Shutdown",
            category = "Debug",
            size = 3
    )
    public static boolean ignored3; // Useless

    @Switch( //Run As Admin Switch Option
            name = "Enable Shutdown",
            category = "Debug", //Give It A Category
            size = 2
    )
    public static boolean lPStDOWN = false; // default value, launchPStoggleShutdown

    @Switch( //Run As Admin Switch Option
            name = "Run As Administrator On Shutdown",
            category = "Debug", //Give It A Category
            size = 2
    )
    public static boolean RAAPSDOWN = false; // default value, Run As Admin Powershell Shutdown

    /// /////////////////////////////////////////DEBUG MISC////////////////////////////////////////

    @Text(
            name = "Powerplan To Revert To",
            category = "Debug", //Give It A Category
            secure = false, multiline = false
    )
    public static String PowerplanDefault = "717AD10b-71F4-4A5E-171F-4A5E71F4A5E1";

    /// /////////////////////////////////////////MISC////////////////////////////////////////

    public TitaniumConfig() {
        // Available mod types: PVP, HUD, UTIL_QOL, HYPIXEL, SKYBLOCK
        super(new Mod("Titanium Rewrite", ModType.UTIL_QOL, "/Assets/logo.png"), "TitaniumConfig.json");
        initialize();
        //cmd dependencies for os
        addDependency("lCMDtUP", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS)); // disable RAACMD if BypassOS is off, making it dependant
        addDependency("RAACMDUP", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("lCMDtDOWN", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("RAACMDDOWN", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        //ps dependencies for os
        addDependency("lPStUP", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("RAAPSUP", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("lPStDOWN", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("RAAPSDOWN", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("runnable1", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("runnable2", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("runnable3", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("runnable4", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("runnable5", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));
        addDependency("Prio", "Enable BypassOS Is Off", () -> (Titaniumod.isWindows || BypassOS));

        //Powerplan selector dependency for os
        addDependency("PPS", "Unreleased", () -> (false));
        addDependency("PowerplanDefault", "Unchangeable", () -> (false));
        addDependency("runnable2", "Is Already Enabled", () -> (Titaniumod.Enableable));
        addDependency("runnable3", "Is Already Enabled", () -> (!Titaniumod.Enableable));
    }

}