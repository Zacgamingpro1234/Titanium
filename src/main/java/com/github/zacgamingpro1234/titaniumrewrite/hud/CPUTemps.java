package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.asset.Icon;
import cc.polyfrost.oneconfig.utils.Notifications;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.CPUwarn;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.templimitCPU;

public class CPUTemps extends SingleTextHud {
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final HardwareAbstractionLayer HARDWARE = SYSTEM_INFO.getHardware();
    private static final Sensors SENSORS = HARDWARE.getSensors();
    private static volatile String cpuTempString;
    private static final CountDownLatch tempUpdateLatch = new CountDownLatch(1);
    public static final Icon FLAME_ICON = new Icon("/Assets/flame.svg");
    private static final Logger LOGGER = LogManager.getLogger("titaniumrewrite");
    private static double temp = Double.NaN;

    private static void UpdTemp() {
        try {
            ThreadManager.execute(() -> {
                temp = SENSORS.getCpuTemperature();
                cpuTempString = String.format("%.1f°C", temp);
            tempUpdateLatch.countDown();
        });
    } catch (Exception e) {
        LOGGER.info(e);
    }
}

    public CPUTemps() {
        super("CPU Temps", false);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
        executor.scheduleAtFixedRate(() -> {
            if(CPUwarn) {
                UpdTemp();
                try {
                    boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                    if ((updated || !Double.isNaN(temp)) && temp >= templimitCPU) {
                        Notifications.INSTANCE.send("Titanium Rewrite",
                                "Your CPU temps have reached " + String.format("%.0f", temp) +
                                        "°C, beware of thermal throttling", FLAME_ICON, 10000);
                    }
                } catch (InterruptedException e) {
                    LOGGER.info(e);
                }
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    @Override
    public String getText(boolean example) {
        if (example) return "69.9°C";
        UpdTemp();
        return cpuTempString;
    }
}
