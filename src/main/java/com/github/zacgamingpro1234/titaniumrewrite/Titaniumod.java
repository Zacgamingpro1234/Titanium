package com.github.zacgamingpro1234.titaniumrewrite;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.Notifications;
import com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig;
import io.github.pandalxb.jlibrehardwaremonitor.util.OSDetector;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

import static com.github.zacgamingpro1234.titaniumrewrite.SharedResources.*;

import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.PowerplanDefault;

@Mod(modid = Titaniumod.MODID, name = Titaniumod.NAME, version = Titaniumod.VERSION)
public class Titaniumod {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static Boolean Enableable = false;
    public TitaniumConfig config;
    public static final boolean isWindows = OSDetector.isWindows();
    public static final String osinfo = String.format("%s %s", System.getProperty("os.name")
            , System.getProperty("os.arch"));

    /// /////////////////////////////////////////MISC////////////////////////////////////////
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        config = new TitaniumConfig(); //Makes The Config Work In-Game
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ThreadManager.shutdown();
            if (isWindows) { //If We Have Windows, Continue With The Code
                launchVerifiedDown();
            } else if (TitaniumConfig.BypassOS) { //Bypass Enabled? Dw I got u
                launchVerifiedDown(); //Continue With The Code
            }
        })); //Run launchVerifiedDown When The Game Closes
        EventManager.INSTANCE.register(this); //Registers Us To The EventBus
    }

    @Subscribe
    public void onInit(InitializationEvent event) {
        executorrepeat();
        if (isWindows) { //If We Have Windows, Continue With The Code
            launchVerifiedUp();
        } else if (TitaniumConfig.BypassOS) { //Bypass Enabled? Dw I got u
            launchVerifiedUp(); //Continue With The Code
        }
    }

    private void launchVerifiedUp() { //Run On Startup
        launchPrioritySetter();
        try {
            // Run PowerShell command to get active GUID
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-Command",
                    "[regex]::Match((powercfg -getactivescheme), 'GUID: ([\\w-]+)').Groups[1].Value"
            );
            Process process = pb.start();
            // Read the output (the GUID)
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String guid = reader.readLine().trim();
                // Verify it's a valid GUID format
                if (guid.matches("[0-9a-fA-F]{8}-(?:[0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}")) {
                    if (!guid.equals("717AD10b-71F4-4A5E-171F-4A5E71F4A5E1")) {
                        PowerplanDefault = guid;
                        Enableable = true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }

        if (TitaniumConfig.lCMDtUP) {
            launchCMDUP();
        }
        if (TitaniumConfig.lPStUP) {
            launchPSUP();
        }
    }

    private void launchVerifiedDown() { //Run On Shutdown
        if (TitaniumConfig.lPStDOWN) {
            launchPSDown();
        }
        if (TitaniumConfig.lCMDtDOWN) {
            launchCMDDown();
        }
    }

    /// /////////////////////////////////////////LAUNCH CMD STARTUP////////////////////////////////////////

    private void launchCMDUP() {
        try {
            ProcessBuilder processBuilder;
            if (TitaniumConfig.RAACMDUP) {
                processBuilder = new ProcessBuilder("cmd", "/c", "powershell", "-Command",
                        "Start-Process cmd -Verb RunAs");
            } else {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", "cmd.exe", "/K", "cd", "\\");
            }
            processBuilder.start();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /// /////////////////////////////////////////LAUNCH CMD SHUTDOWN////////////////////////////////////////

    private void launchCMDDown() {
        try {
            ProcessBuilder processBuilder;
            if (TitaniumConfig.RAACMDDOWN) {
                processBuilder = new ProcessBuilder("cmd", "/c", "powershell", "-Command",
                        "Start-Process cmd -Verb RunAs");
            } else {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", "cmd.exe", "/K", "cd", "\\");
            }
            processBuilder.start();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /// /////////////////////////////////////////LAUNCH POWERSHELL STARTUP////////////////////////////////////////

    private void launchPSUP() {
        try {
            ProcessBuilder processBuilder;
            if (TitaniumConfig.RAAPSUP) {
                processBuilder = new ProcessBuilder("powershell", "-Command", "Start-Process", "powershell", "-ArgumentList", "\"-NoExit -Command Set-Location -Path", "'C:\\'\"", "-Verb", "RunAs");
            } else {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", "powershell.exe", "-NoExit", "-Command", "Set-Location -Path '" + "C:\\" + "'");
            }
            processBuilder.start();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /// /////////////////////////////////////////LAUNCH POWERSHELL SHUTDOWN////////////////////////////////////////

    private void launchPSDown() {
        try {
            ProcessBuilder processBuilder;
            if (TitaniumConfig.RAAPSDOWN) {
                processBuilder = new ProcessBuilder("powershell", "-Command", "Start-Process", "powershell", "-ArgumentList", "\"-NoExit -Command Set-Location -Path", "'C:\\'\"", "-Verb", "RunAs");
            } else {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", "powershell.exe", "-NoExit", "-Command", "Set-Location -Path '" + "C:\\" + "'");
            }
            processBuilder.start();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /// /////////////////////////////////////////AUTO PRIORITY SETTER////////////////////////////////////////

    public static void launchPrioritySetter() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        String processID = processName.split("@")[0];
        int Priority;
        switch (TitaniumConfig.Prio) {
            case 0:
                Priority = 128;      // High
                break;
            case 1:
                Priority = 32768;    // Above Normal
                break;
            case 2:
                Priority = 32;       // Normal (default)
                break;
            case 3:
                Priority = 16384;    // Below Normal
                break;
            default:
                Priority = 32;       // Fallback to Normal
        }

        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "wmic process where ProcessId=" + processID + " call setpriority " + Priority);
                Process process = processBuilder.start();
                Notifications.INSTANCE.send("Titanium Rewrite", "Please Wait, Priority Applied Soon");
                process.waitFor();
                Notifications.INSTANCE.send("Titanium Rewrite", "Priority " + Priority + " applied to ProcessID " + processID);
            } catch (IOException | InterruptedException e) {
                LOGGER.error(e);
            }
        }).start();
    }

    /// /////////////////////////////////////////CREATE & APPLY ULTIMATE POWERPLAN////////////////////////////////////////

    public static void enableultPwrPln() {
        Enableable = false;
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "powercfg -duplicatescheme e9a42b02-d5df-448d-aa00-03f14749eb61 717AD10b-71F4-4A5E-171F-4A5E71F4A5E1");
                ProcessBuilder processBuilder2 = new ProcessBuilder("cmd", "/c", "powercfg -S 717AD10b-71F4-4A5E-171F-4A5E71F4A5E1");
                Process process = processBuilder.start();
                Notifications.INSTANCE.send("Titanium Rewrite", "Please Wait, Powerplan Applied Soon");
                process.waitFor();
                Process process2 = processBuilder2.start();
                process2.waitFor();
                Notifications.INSTANCE.send("Titanium Rewrite", "The Ulitmate Powerplan Is Applied");
            } catch (IOException | InterruptedException e) {
                LOGGER.error(e);
            }
        }).start();
    }

    public static void revertpwrpln() {
        Enableable = true;
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "powercfg -S " + PowerplanDefault);
                ProcessBuilder processBuilder2 = new ProcessBuilder("cmd", "/c", "powercfg -D 717AD10b-71F4-4A5E-171F-4A5E71F4A5E1");
                Process process = processBuilder.start();
                process.waitFor();
                processBuilder2.start();
                Notifications.INSTANCE.send("Titanium Rewrite", "Powerplan reverted back to original state");
            } catch (IOException | InterruptedException e) {
                LOGGER.error(e);
            }
        }).start();
    }

    /// /////////////////////////////////////////LAUNCH CTwinUtil////////////////////////////////////////

    public static void CTwinUtil() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("powershell", "-Command", "iwr -useb https://christitus.com/win | iex");
            processBuilder.start();
            Notifications.INSTANCE.send("Titanium Rewrite", "Launching The Ultimate Windows Utility by Chris Titus Tech, Please Wait and Accept The UAC");
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

}