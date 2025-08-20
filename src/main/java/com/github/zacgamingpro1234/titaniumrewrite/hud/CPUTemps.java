package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import io.github.pandalxb.jlibrehardwaremonitor.model.Sensor;
import java.util.List;
import java.util.Optional;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.*;
import static com.github.zacgamingpro1234.titaniumrewrite.SharedResources.*;

public class CPUTemps extends SingleTextHud {
    transient public static volatile double tempCPU = Double.NaN;
    transient private static volatile String cpuTempString = "Loading";
    transient private static volatile List<Sensor> sensors;
    transient private static int ignticks;
    transient private static volatile boolean running = false;

    @Number(
            name = "Decimal Accuracy",    // name of the component
            min = 0, max = 6        // min and max values (anything above/below is set to the max/min
    )
    static volatile int num = 0; // default value
    @Color(
            name = "Default Color"
    )
    volatile OneColor Dclr = new OneColor(255, 255, 255);
    @Color(
            name = "Hot Color"
    )
    volatile OneColor Hclr = new OneColor(255, 0, 0, 255);
    @Number(
            name = "Hot Amount",    // name of the component
            min = 0f, max = 110f,        // min and max values (anything above/below is set to the max/min
            step = 5       // each time the arrow is clicked it will increase/decrease by this amount
    )
    volatile float num2 = 85; // default value

    public static void UpdTempCPU(boolean forced) {
        if ((forced || ignticks > waitick) && !running) {
            try {
                running = true;
                if (!forced) ignticks = 0;
                ThreadManager.execute(() -> {
                    try {
                        sensors = libreHardwareManager.querySensors("CPU", "Temperature");
                        Optional<Sensor> coreMaxSensor = sensors.stream()
                                .filter(s -> "Core Max".equals(s.getName()))
                                .findFirst();
                        coreMaxSensor.ifPresent(sensor -> tempCPU = sensor.getValue());
                        cpuTempString = String.format(("%." + num + "f°C"), tempCPU);
                        tempUpdateLatch.countDown();
                    } catch (Exception e) {
                        LOGGER.warn(e);
                    }
                    running = false;
                });
            } catch (Exception e) {
                LOGGER.warn(e);
            }
        } else {
            ignticks += 1;
        }
    }

    public CPUTemps() {
        super("CPU Temps", false);
    }

    @Override
    public String getText(boolean example) {
        if (example) return String.format(("%." + num + "f°C"), 69.0);
        UpdTempCPU(false);
        return cpuTempString;
    }

    @Override
    protected void drawLine(String line, float x, float y, float scale) {
            if (!Double.isNaN(tempCPU)) {
                color = (tempCPU >= num2) ? Hclr : Dclr;
            } else {
                color = Dclr;
            }
        TextRenderer.drawScaledString(line, x, y, color.getRGB(), TextRenderer.TextType.toType(textType), scale);
    }
}
