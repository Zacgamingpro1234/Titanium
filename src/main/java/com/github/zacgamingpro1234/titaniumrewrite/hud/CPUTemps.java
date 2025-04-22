package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.asset.Icon;
import cc.polyfrost.oneconfig.utils.Notifications;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.CPUwarn;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.templimit;

public class CPUTemps extends SingleTextHud {
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final HardwareAbstractionLayer HARDWARE = SYSTEM_INFO.getHardware();
    private static final Sensors SENSORS = HARDWARE.getSensors();
    private volatile String cpuTempString;
    public static final Icon FLAME_ICON = new Icon("/Assets/flame.svg");

    public CPUTemps() {
        super("CPU Temps", false);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if(CPUwarn){
                        float temp = (float) SENSORS.getCpuTemperature();
                        if (temp >= templimit){
                            Notifications.INSTANCE.send("Titanium Rewrite",
                                    "Your CPU temps have reached " + String.format("%.0f", temp) +
                                            "°C, beware of thermal throttling", FLAME_ICON, 10000);
                        }
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    @Override
    public String getText(boolean example) {
        if (example) return "69.9°C";
        new Thread(() -> {
            float temp = (float) SENSORS.getCpuTemperature();
            cpuTempString = String.format("%.1f°C", temp);
        }).start();
        return cpuTempString;
    }
}
