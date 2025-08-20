package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import io.github.pandalxb.jlibrehardwaremonitor.model.Sensor;
import cc.polyfrost.oneconfig.config.annotations.Number;
import java.util.List;
import java.util.Optional;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.*;
import static com.github.zacgamingpro1234.titaniumrewrite.SharedResources.*;

public class BatteryLife extends SingleTextHud {
    transient public static volatile double percent = Double.NaN;
    transient public static volatile String percentString = "Loading";
    transient public static volatile boolean charging;
    transient private static volatile List<Sensor> sensorslvl;
    transient private static volatile List<Sensor> sensorspwr;
    transient private static int ignticks;
    transient private static volatile boolean running = false;

    @Number(
            name = "Decimal Accuracy",    // name of the component
            min = 0, max = 6        // min and max values (anything above/below is set to the max/min
    )
    static volatile int num = 2; // default value
    @Color(
            name = "Discharging Color"
    )
    volatile OneColor Dclr = new OneColor(255, 255, 255);
    @Color(
            name = "Full Charge Color"
    )
    volatile OneColor FCclr = new OneColor(0, 255, 0, 255);
    @Color(
            name = "Charging Color"
    )
    volatile OneColor Cclr = new OneColor(0, 155, 0, 255);
    @Color(
            name = "Low Charge Color"
    )
    volatile OneColor LCclr = new OneColor(155, 0, 0, 255);
    @Number(
            name = "Low Charge Amount",    // name of the component
            min = 0f, max = 99.9f,        // min and max values (anything above/below is set to the max/min
            step = 5       // each time the arrow is clicked it will increase/decrease by this amount
    )
    volatile float num2 = 15; // default value

    public static void UpdLife(boolean forced) {
        if ((forced || ignticks > waitick) && !running) {
            try {
                running = true;
                if (!forced) ignticks = 0;
                ThreadManager.execute(() -> {
                    try {
                        sensorslvl = libreHardwareManager.querySensors("Battery", "Level");
                        Optional<Sensor> lvl = sensorslvl.stream()
                                .filter(s -> "Charge Level".equals(s.getName()))
                                .findFirst();
                        lvl.ifPresent(sensor -> percent = sensor.getValue());

                        sensorspwr = libreHardwareManager.querySensors("Battery", "Power");
                        Optional<Sensor> pwr = sensorspwr.stream()
                                .filter(s -> {
                                    String n = s.getName();
                                    return "Charge Rate".equals(n) || "Discharge Rate".equals(n);
                                })
                                .findFirst();
                        pwr.ifPresent(s -> charging = "Charge Rate".equals(s.getName()));

                        percentString = String.format(("%." + num + "f"), percent) + "%";
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

    public BatteryLife() {
        super("Battery Percentage", false);
    }

    @Override
    public String getText(boolean example) {
        if (example) return String.format(("%." + num + "f"), 69.0) + "%";
        UpdLife(false);
        return percentString;
    }

    @Override
    protected void drawLine(String line, float x, float y, float scale) {
            if (!Double.isNaN(percent)) {
                if (percent >= 100) {
                    color = FCclr;
                } else if (charging) {
                    color = Cclr;
                } else if (percent <= num2) {
                    color = LCclr;
                } else {
                    color = Dclr;
                }
            } else {
                color = Dclr;
            }
        TextRenderer.drawScaledString(line, x, y, color.getRGB(), TextRenderer.TextType.toType(textType), scale);
    }
}
