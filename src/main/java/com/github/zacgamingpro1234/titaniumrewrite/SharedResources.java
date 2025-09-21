package com.github.zacgamingpro1234.titaniumrewrite;

import cc.polyfrost.oneconfig.renderer.asset.Icon;
import cc.polyfrost.oneconfig.utils.Notifications;
import io.github.pandalxb.jlibrehardwaremonitor.config.ComputerConfig;
import io.github.pandalxb.jlibrehardwaremonitor.manager.LibreHardwareManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.*;
import static com.github.zacgamingpro1234.titaniumrewrite.hud.BatteryLife.*;
import static com.github.zacgamingpro1234.titaniumrewrite.hud.CPUTemps.*;
import static com.github.zacgamingpro1234.titaniumrewrite.hud.GPUTemps.*;
import static com.github.zacgamingpro1234.titaniumrewrite.hud.RAMUsage.*;
import static com.github.zacgamingpro1234.titaniumrewrite.Titaniumod.*;

public class SharedResources {
    public static final LibreHardwareManager libreHardwareManager = LibreHardwareManager.createInstance(ComputerConfig.getInstance()
            .setGpuEnabled(true)
            .setCpuEnabled(true)
            .setBatteryEnabled(true)
            .setMemoryEnabled(true)
    );
    public static final Icon FLAME_ICON = new Icon("/Assets/flame.svg");
    public static final Logger LOGGER = LogManager.getLogger("titaniumrewrite");
    public static final Icon BATTERY_ICON = new Icon("/Assets/battery-warning.svg");
    public static final Icon RAM_ICON = new Icon("/Assets/memory-stick.svg");
    public static final String MainGPU = GL11.glGetString(GL11.GL_RENDERER);
    public static final String MainVendor = GL11.glGetString(GL11.GL_VENDOR);
    public volatile static AtomicInteger amt = new AtomicInteger(0);
    public static CountDownLatch tempUpdateLatch;

    public static void executorrepeat() {
        if (selgpu == 3){
            String lower = (MainGPU + " " + MainVendor).toLowerCase();
            if (lower.contains("nvidia")) {
                selgpu = 2;
            } else if (lower.contains("amd") || lower.contains("ati")) {
                selgpu = 1;
            } else if (lower.contains("intel")) {
                selgpu = 0;
            }
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            if (setog) {
                revertpwrpln();
            }
        }));
        executor.scheduleAtFixedRate(() -> {
            amt.set(0);
            if (CPUwarn) amt.incrementAndGet();
            if (GPUwarn) amt.incrementAndGet();
            if (Batterywarn) amt.incrementAndGet();
            if (RAMwarn) amt.incrementAndGet();
            tempUpdateLatch = new CountDownLatch(amt.get());

            if (CPUwarn) {
                UpdTempCPU(true);
                ThreadManager.execute(() -> {
                    try {
                        boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                        if ((updated || !Double.isNaN(tempCPU)) && tempCPU >= templimitCPU) {
                            Notifications.INSTANCE.send("Titanium Rewrite",
                                    "Your CPU temps have reached " + String.format("%.0f", tempCPU) +
                                            "°C, beware of thermal throttling", FLAME_ICON, 10000);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn(e);
                    }
                });
            }

            if (GPUwarn) {
                UpdTempGPU(true);
                ThreadManager.execute(() -> {
                    try {
                        boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                        if ((updated || !Double.isNaN(tempGPU)) && tempGPU >= templimitGPU) {
                            Notifications.INSTANCE.send("Titanium Rewrite",
                                    "Your GPU temps have reached " + String.format("%.0f", tempGPU) +
                                            "°C, beware of thermal throttling", FLAME_ICON, 10000);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn(e);
                    }
                });
            }

            if (Batterywarn) {
                UpdLife(true);
                ThreadManager.execute(() -> {
                    try {
                        boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                        if ((updated || !Double.isNaN(percent)) && percent <= percentMinimum && !charging) {
                            Notifications.INSTANCE.send("Titanium Rewrite",
                                    "Your Battery percentage has reached " + percentString +
                                            "°C, please plug it in", BATTERY_ICON, 10000);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn(e);
                    }
                });
            }

            if (RAMwarn) {
                UpdRAMamt(true);
                ThreadManager.execute(() -> {
                    try {
                        boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                        if (updated && RAMLeftLimit >= (MemoryFree*1024)) {
                            Notifications.INSTANCE.send("Titanium Rewrite",
                                    "You Have " + String.format(("%." + idk + "f"), MemoryFree * divisor) + numstring +
                                            " Of RAM Left, please close background apps", RAM_ICON, 10000);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn(e);
                    }

                });
            }
        }, 0, 60, TimeUnit.SECONDS);
    }
}