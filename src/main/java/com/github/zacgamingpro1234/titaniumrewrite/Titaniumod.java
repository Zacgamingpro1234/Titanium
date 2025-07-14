package com.github.zacgamingpro1234.titaniumrewrite;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.Notifications;
import com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.lang.management.ManagementFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.PowerplanDefault;

@Mod(modid = Titaniumod.MODID, name = Titaniumod.NAME, version = Titaniumod.VERSION)
public class Titaniumod {
    public static final String MODID = "titaniumrewrite";
    public static final String NAME = "Titanium Rewrite";
    public static final String VERSION = "V0.3";
    public static Boolean Enableable = false;
    public TitaniumConfig config;
    public static boolean isWindows;
    public static OperatingSystem os;
    private static final Logger LOGGER = LogManager.getLogger("titaniumrewrite");
    SystemInfo si = new SystemInfo();

    /// /////////////////////////////////////////MISC////////////////////////////////////////
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        //Checks If We Are Using Windows using Oshi OS Family
        os = si.getOperatingSystem();
        LOGGER.info(os.toString());
        String ostype = os.getFamily();
        if (Objects.equals(ostype, "Windows")) {
            isWindows = true;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isWindows) { //If We Have Windows, Continue With The Code
                launchVerifiedDown();
            } else if (TitaniumConfig.BypassOS) { //Bypass Enabled? Dw I got u
                launchVerifiedDown(); //Continue With The Code
            }
        })); //Run launchVerifiedDown When The Game Closes
        BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true));
        writer.write("Is Windows: " + os);
        writer.newLine();
        writer.close();
        EventManager.INSTANCE.register(this); //Registers Us To The EventBus
    }

    @Subscribe
    public void onInit(InitializationEvent event) {
        config = new TitaniumConfig(); //Makes The Config Work In-Game
        if (isWindows) { //If We Have Windows, Continue With The Code
            launchVerifiedUp();
        } else if (TitaniumConfig.BypassOS) { //Bypass Enabled? Dw I got u
            launchVerifiedUp(); //Continue With The Code
        }
    }

    private void launchVerifiedUp() { //Run On Startup
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true))) {
            writer.write("Launch CMD-UP: " + TitaniumConfig.lCMDtUP);
            writer.newLine();
            writer.write("Launch PS-UP: " + TitaniumConfig.lPStUP);
            writer.newLine();
            launchPrioritySetter();
        } catch (IOException e) {
            LOGGER.error(e);
        }
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
                    if (!guid.equals("717AD10b-71F4-4A5E-171F-4A5E71F4A5E1")){
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true))) {
            writer.write("Verified Shutdown: true");
            writer.newLine();
            writer.write("Launch CMD-DOWN: " + TitaniumConfig.lCMDtDOWN);
            writer.newLine();
            writer.write("Launch PS-DOWN: " + TitaniumConfig.lPStDOWN);
            writer.newLine();
        } catch (IOException e) {
            LOGGER.error(e);
        }

        if (TitaniumConfig.lPStDOWN) {
            launchPSDown();
        }
        if (TitaniumConfig.lCMDtDOWN) {
            launchCMDDown();
        }
    }

    /// /////////////////////////////////////////LAUNCH CMD STARTUP////////////////////////////////////////

    private void launchCMDUP() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true))) {
            writer.write("Run As Admin CMD Startup: " + TitaniumConfig.RAACMDUP);
            writer.newLine();

            ProcessBuilder processBuilder;
            if (TitaniumConfig.RAACMDUP) {
                processBuilder = new ProcessBuilder("cmd", "/c", "powershell", "-Command",
                        "Start-Process cmd -Verb RunAs");
            } else {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", "cmd.exe", "/K", "cd", "\\");
            }

            Process process = processBuilder.start();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /// /////////////////////////////////////////LAUNCH CMD SHUTDOWN////////////////////////////////////////

    private void launchCMDDown() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true))) {
            writer.write("Run As Admin CMD Shutdown: " + TitaniumConfig.RAACMDDOWN);
            writer.newLine();

            ProcessBuilder processBuilder;
            if (TitaniumConfig.RAACMDDOWN) {
                processBuilder = new ProcessBuilder("cmd", "/c", "powershell", "-Command",
                        "Start-Process cmd -Verb RunAs");
            } else {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", "cmd.exe", "/K", "cd", "\\");
            }

            Process process = processBuilder.start();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /// /////////////////////////////////////////LAUNCH POWERSHELL STARTUP////////////////////////////////////////

    private void launchPSUP() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true))) {
            writer.write("Run As Admin PowerShell Startup: " + TitaniumConfig.RAAPSUP);
            writer.newLine();
            ProcessBuilder processBuilder;

            if (TitaniumConfig.RAAPSUP) {
                processBuilder = new ProcessBuilder("powershell", "-Command", "Start-Process", "powershell", "-ArgumentList", "\"-NoExit -Command Set-Location -Path", "'C:\\'\"", "-Verb", "RunAs");
            } else {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", "powershell.exe", "-NoExit", "-Command", "Set-Location -Path '" + "C:\\" + "'");
            }

            Process process = processBuilder.start();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /// /////////////////////////////////////////LAUNCH POWERSHELL SHUTDOWN////////////////////////////////////////

    private void launchPSDown() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true))) {
            writer.write("Run As Admin PowerShell Shutdown: " + TitaniumConfig.RAAPSDOWN);
            writer.newLine();
            ProcessBuilder processBuilder;

            if (TitaniumConfig.RAAPSDOWN) {
                processBuilder = new ProcessBuilder("powershell", "-Command", "Start-Process", "powershell", "-ArgumentList", "\"-NoExit -Command Set-Location -Path", "'C:\\'\"", "-Verb", "RunAs");
            } else {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", "powershell.exe", "-NoExit", "-Command", "Set-Location -Path '" + "C:\\" + "'");
            }

            Process process = processBuilder.start();
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true))) {
            writer.write("ProcessID: " + processID + ", Setting priority to: " + Priority);
            writer.newLine();
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "wmic process where ProcessId=" + processID + " call setpriority " + Priority);
            Process process = processBuilder.start();
            Notifications.INSTANCE.send("Titanium Rewrite", "Priority "+Priority+" applied to ProcessID "+processID);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /// /////////////////////////////////////////CREATE & APPLY ULTIMATE POWERPLAN////////////////////////////////////////

    public static void enableultPwrPln() {
        Enableable = false;
        new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true))) {
                writer.write("Powerplan Creation");
                writer.newLine();
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "powercfg -duplicatescheme e9a42b02-d5df-448d-aa00-03f14749eb61 717AD10b-71F4-4A5E-171F-4A5E71F4A5E1");
                ProcessBuilder processBuilder2 = new ProcessBuilder("cmd", "/c", "powercfg -S 717AD10b-71F4-4A5E-171F-4A5E71F4A5E1");
                Process process = processBuilder.start();
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
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("mod_config.log", true))) {
                writer.write(PowerplanDefault);
                writer.newLine();
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "powercfg -S " + PowerplanDefault);
                ProcessBuilder processBuilder2 = new ProcessBuilder("cmd", "/c", "powercfg -D 717AD10b-71F4-4A5E-171F-4A5E71F4A5E1");
                Process process = processBuilder.start();
                process.waitFor();
                Process process2 = processBuilder2.start();
                Notifications.INSTANCE.send("Titanium Rewrite", "Powerplan reverted back to original state");
            } catch (IOException | InterruptedException e) {
                LOGGER.error(e);
            }
        }).start();
    }

    /// /////////////////////////////////////////LAUNCH CTwinUtil////////////////////////////////////////

    public static void CTwinUtil(){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("powershell", "-Command", "iwr -useb https://christitus.com/win | iex");
            Process process = processBuilder.start();
            Notifications.INSTANCE.send("Titanium Rewrite", "Launching The Ultimate Windows Utility by Chris Titus Tech, Please Wait and Accept The UAC");
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /// /////////////////////////////////////////LAUNCH ADMIN APP////////////////////////////////////////

    private static void LaunchIt(String fulldir){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("powershell", "-WindowStyle", "Hidden", "-Command", "Start-Process", "-FilePath", fulldir, "-WindowStyle", "Hidden");
            Process process = processBuilder.start();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static boolean unzip(String zipFile, String destFolder) throws IOException {
        try (InputStream resourceStream = Titaniumod.class.getResourceAsStream(zipFile)) {
            if (resourceStream == null) {
                LOGGER.error("Could not find resource: {}", zipFile);
                return false;
            }
            try (ZipInputStream zis = new ZipInputStream((resourceStream))) {
                ZipEntry entry;
                byte[] buffer = new byte[1024];
                while ((entry = zis.getNextEntry()) != null) {
                    File newFile = new File(destFolder + File.separator + entry.getName());
                    if (entry.isDirectory()) {
                        if (!newFile.mkdirs()) {
                            LOGGER.error("Failed to create directory: {}", newFile.getAbsolutePath());
                            return false;
                        }
                    } else {
                        File parentDir = new File(newFile.getParent());
                        if (!parentDir.exists() && !parentDir.mkdirs()) {
                            LOGGER.error("Failed to create parent directory: {}", parentDir.getAbsolutePath());
                            return false;
                        }

                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
                            int length;
                            while ((length = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void LaunchAsAdmin() {
        new Thread(() -> {
            String mcDir = Minecraft.getMinecraft().mcDataDir.toString();
            String dir = mcDir + File.separator + "OHM";
            String fulldir = dir + File.separator + "OpenHardwareMonitor.exe";
            if (!Files.exists(Paths.get(fulldir))) {
                try {
                    String zipFileName = "/third-party/OpenHardwareMonitor.zip";
                    if (unzip(zipFileName, dir)) {
                        LaunchIt(fulldir);
                    }else{
                        LOGGER.error("Failed To Launch OHM Due To Extraction Error");
                    }
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            } else {
                LaunchIt(fulldir);
            }
        }).start();
    }
}