package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
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

public class GPUTemps extends SingleTextHud {
    transient public static volatile double tempGPU = Double.NaN;
    transient private static volatile String tempstring = "Loading";
    transient private static volatile List<Sensor> sensors;
    transient private static int ignticks;
    transient private static volatile String hardtype = "Gpu";
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
    @Dropdown(
            name = "Selected GPU",        // name of the component
            options = {"Intel GPU", "AMD GPU", "Nvidia GPU", "Random GPU (Fallback)"},
            size = 5
    )
    public static volatile int selgpu = 3;        // default option (here "Another Option")

    public static void UpdTempGPU(boolean forced) {
        if ((forced || ignticks > waitick) && !running) {
            try {
                running = true;
                if (!forced) ignticks = 0;
                ThreadManager.execute(() -> {
                    try {
                        switch (selgpu) {
                            case 0:
                                hardtype = "GpuIntel";
                            case 1:
                                hardtype = "GpuAmd";
                            case 2:
                                hardtype = "GpuNvidia";
                            default:
                                hardtype = "GPU";
                        }
                        sensors = libreHardwareManager.querySensors(hardtype, "Temperature");
                        Optional<Sensor> coreSensor = sensors.stream()
                                .filter(s -> "GPU Core".equals(s.getName()))
                                .findFirst();
                        coreSensor.ifPresent(sensor -> tempGPU = sensor.getValue());
                        tempstring = String.format(("%." + num + "f°C"), tempGPU);
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

    public GPUTemps() {
        super("GPU Temps", false);
    }

    @Override
    public String getText(boolean example) {
        if (example) return String.format(("%." + num + "f°C"), 69.0);
        UpdTempGPU(false);
        return tempstring;
    }

    @Override
    protected void drawLine(String line, float x, float y, float scale) {
            if (!Double.isNaN(tempGPU)) {
                color = (tempGPU >= num2) ? Hclr : Dclr;
            } else {
                color = Dclr;
            }
        TextRenderer.drawScaledString(line, x, y, color.getRGB(), TextRenderer.TextType.toType(textType), scale);
    }
}
