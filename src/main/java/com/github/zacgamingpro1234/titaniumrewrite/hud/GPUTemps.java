package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.asset.Icon;
import cc.polyfrost.oneconfig.utils.Notifications;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import io.github.pandalxb.jlibrehardwaremonitor.config.ComputerConfig;
import io.github.pandalxb.jlibrehardwaremonitor.manager.LibreHardwareManager;
import io.github.pandalxb.jlibrehardwaremonitor.model.Sensor;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.GPUwarn;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.templimitGPU;

public class GPUTemps extends SingleTextHud {
    private static final LibreHardwareManager libreHardwareManager = LibreHardwareManager.createInstance(ComputerConfig.getInstance().setGpuEnabled(true));
    List<Sensor> sensors = libreHardwareManager.querySensors("GPU", "Temperature");
    private volatile String gpuTempString;
    public static final Icon FLAME_ICON = new Icon("/Assets/flame.svg");
    double temp = 1f;

    public GPUTemps() {
        super("GPU Temps", false);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
        executor.scheduleAtFixedRate(() -> {
            if(GPUwarn){
                List<Sensor> sensors = libreHardwareManager.querySensors("GPU", "Temperature");
                for(Sensor sensor : sensors) {
                    double sens = sensor.getValue();
                    if (sens > 0) {
                        temp = sens;
                    }
                }
                if (temp >= templimitGPU){
                    Notifications.INSTANCE.send("Titanium Rewrite",
                            "Your GPU temps have reached " + String.format("%.0f", temp) +
                                    "°C, beware of thermal throttling", FLAME_ICON, 10000);
                }
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    @Override
    public String getText(boolean example) {
        if (example) return "69.9°C";
        ThreadManager.execute(() -> {
            List<Sensor> sensors = libreHardwareManager.querySensors("GPU", "Temperature");
            for(Sensor sensor : sensors) {
                double sens = sensor.getValue();
                if (sens > 0) {
                    temp = sens;
                }
            }
            gpuTempString = String.format("%.1f°C", temp);
        });
        return gpuTempString;
    }
}
