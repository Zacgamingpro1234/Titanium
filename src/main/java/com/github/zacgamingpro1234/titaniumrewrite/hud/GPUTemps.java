package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.asset.Icon;
import cc.polyfrost.oneconfig.utils.Notifications;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import io.github.pandalxb.jlibrehardwaremonitor.config.ComputerConfig;
import io.github.pandalxb.jlibrehardwaremonitor.manager.LibreHardwareManager;
import io.github.pandalxb.jlibrehardwaremonitor.model.Sensor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.GPUwarn;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.templimitGPU;

public class GPUTemps extends SingleTextHud {
    private static final LibreHardwareManager libreHardwareManager = LibreHardwareManager.createInstance(ComputerConfig.getInstance().setGpuEnabled(true));
    public static final Icon FLAME_ICON = new Icon("/Assets/flame.svg");
    private static final Logger LOGGER = LogManager.getLogger("titaniumrewrite");
    private static final CountDownLatch tempUpdateLatch = new CountDownLatch(1);
    static double temp = Double.NaN;
    static String tempstring = "N/A";

    private static void UpdTemp() {
        try {
            ThreadManager.execute(() -> {
                List<Sensor> sensors = libreHardwareManager.querySensors("GPU", "Temperature");
                for (Sensor sensor : sensors) {
                    temp = sensor.getValue();
                    tempstring = String.format("%.1f°C", temp);
                }
                tempUpdateLatch.countDown();
            });
        } catch (Exception e) {
            LOGGER.info(e);
        }
    }

    public GPUTemps() {
        super("GPU Temps", false);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
        executor.scheduleAtFixedRate(() -> {
            if (GPUwarn) {
                UpdTemp();
                try {
                    boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                    if ((updated || !Double.isNaN(temp)) && temp >= templimitGPU) {
                        Notifications.INSTANCE.send("Titanium Rewrite",
                                "Your GPU temps have reached " + String.format("%.0f", temp) +
                                        "°C, beware of thermal throttling", FLAME_ICON, 10000);
                    }
                } catch (Exception e) {
                    LOGGER.info(e);
                }
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    @Override
    public String getText(boolean example) {
        if (example) return "69.9°C";
        UpdTemp();
        return tempstring;
    }
}
