package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
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
    private static volatile double temp = Double.NaN;
    private static volatile String tempstring = "N/A";
    private static volatile OneColor clr;
    @Number(
            name = "Decimal Accuracy",    // name of the component
            min = 0, max = 6,        // min and max values (anything above/below is set to the max/min
            step = 1        // each time the arrow is clicked it will increase/decrease by this amount
    )
    public static int num = 0; // default value
    @Color(
            name = "Default Color"
    )
    OneColor Dclr = new OneColor(255, 255, 255);
    @Color(
            name = "Hot Color"
    )
    OneColor Hclr = new OneColor(255, 0, 0, 255);
    @Number(
            name = "Hot Amount",    // name of the component
            min = 0f, max = 110f,        // min and max values (anything above/below is set to the max/min
            step = 5       // each time the arrow is clicked it will increase/decrease by this amount
    )
    public static float num2 = 85; // default value

    private static void UpdTemp() {
        try {
            ThreadManager.execute(() -> {
                try {
                    List<Sensor> sensors = libreHardwareManager.querySensors("GPU", "Temperature");
                    for (Sensor sensor : sensors) {
                        temp = sensor.getValue();
                        tempstring = String.format(("%." + num + "f°C"), temp);
                    }
                    tempUpdateLatch.countDown();
                } catch (Exception e) {
                    LOGGER.warn(e);
                }
            });
        } catch (Exception e) {
            LOGGER.warn(e);
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
                } catch (InterruptedException e) {
                    LOGGER.warn(e);
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

    @Override
    protected void drawLine(String line, float x, float y, float scale) {
        UpdTemp();
        ThreadManager.execute(() -> {
            try {
                boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                if ((updated || !Double.isNaN(temp))){
                    if (temp >= num2){
                        clr = Hclr;
                    } else {
                        clr = Dclr;
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.warn(e);
            }
        });
        if (!(clr == null)){
            color = clr;
        }else{
            LOGGER.warn("No Colour Found");
        }
        TextRenderer.drawScaledString(line, x, y, color.getRGB(), TextRenderer.TextType.toType(textType), scale);
    }
}
