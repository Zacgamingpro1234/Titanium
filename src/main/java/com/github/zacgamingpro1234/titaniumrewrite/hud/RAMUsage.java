package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import io.github.pandalxb.jlibrehardwaremonitor.model.Sensor;
import java.util.List;
import java.util.Optional;

import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.*;
import static com.github.zacgamingpro1234.titaniumrewrite.SharedResources.*;

public class RAMUsage extends SingleTextHud {
    transient public static double RAMTotal; // RAM Total in GB
    transient public static volatile double MemoryFree;
    transient public static volatile double MemoryUsed;
    transient public static volatile double RAMTotalfr; // RAM Total either GB or MB (changed at runtime)
    transient public static volatile String numstring = "GB";
    transient public static volatile String RAMstring = "N/A";
    transient public static volatile double divisor;
    transient private static int ignticks;
    transient private static volatile boolean running = false;

    @Dropdown(
            name = "Select Unit",
            options = {"GigaBytes", "MegaBytes"}
    )
    static volatile int num = 1;

    @Color(name = "Default Color")
    volatile OneColor Dclr = new OneColor(255, 255, 255);

    @Color(name = "High Usage Color")
    volatile OneColor Hclr = new OneColor(255, 0, 0, 255);

    @Number(
            name = "High Usage Amount (In MB)",
            min = 0f,
            max = 4096f
    )
    volatile float num2;

    @Dropdown(
            name = "Select Styling",
            options = {"Percentage", "Amount Free", "Free/Total", "Total"}
    )
    static volatile int value = 2;

    @Number(
            name = "Decimal Accuracy",    // name of the component
            min = 0, max = 6        // min and max values (anything above/below is set to the max/min
    )
    public static volatile int idk = 0; // default value

    @Switch(
            name = "Change From Remaining To Using"
    )
    static volatile boolean Using = false;

    public RAMUsage() {
        super("RAM", false);
        List<Sensor> Data = libreHardwareManager.querySensors("Memory", "Data");
        Optional<Sensor> MemoryUsedSensor = Data.stream()
                .filter(s -> "Memory Used".equals(s.getName()))
                .findFirst();
        MemoryUsedSensor.ifPresent(sensor -> MemoryUsed = sensor.getValue());
        Optional<Sensor> MemoryLeftSensor = Data.stream()
                .filter(s -> "Memory Available".equals(s.getName()))
                .findFirst();
        MemoryLeftSensor.ifPresent(sensor -> MemoryFree = sensor.getValue());
        RAMTotal = MemoryUsed + MemoryFree;
        num2 = (float) ((RAMTotal * 1024) * .08);
        RAMLeftLimit = (float) ((RAMTotal * 1024) * .08);
    }

    public static void UpdRAMamt(boolean forced) {
        if ((forced || ignticks > waitick) && !running) {
            try {
                running = true;
                if (!forced) ignticks = 0;
                ThreadManager.execute(() -> {
                    try {
                        List<Sensor> Data = libreHardwareManager.querySensors("Memory", "Data");
                        Optional<Sensor> MemoryLeftSensor = Data.stream()
                                .filter(s -> "Memory Available".equals(s.getName()))
                                .findFirst();
                        MemoryLeftSensor.ifPresent(sensor -> MemoryFree = sensor.getValue());
                        divisor = Math.pow(1024, num);
                        RAMTotalfr = RAMTotal * divisor;
                        if (num == 1) {
                            numstring = "MB";
                        } else {
                            numstring = "GB";
                        }

                        // choose your display mode
                        switch (value) {
                            case 0:
                                if (Using) {
                                    RAMstring = String.format(("%." + idk + "f"), (100 - (100.0 * MemoryFree / RAMTotal))) + "%";
                                } else {
                                    RAMstring = String.format(("%." + idk + "f"), (100.0 * MemoryFree / RAMTotal)) + "%";
                                }
                                break;
                            case 1:
                                if (Using) {
                                    RAMstring = String.format(("%." + idk + "f"), RAMTotalfr - (MemoryFree * divisor)) + numstring;
                                } else {
                                    RAMstring = String.format(("%." + idk + "f"), MemoryFree * divisor) + numstring;
                                }
                                break;
                            case 2:
                                if (Using) {
                                    RAMstring = String.format(("%." + idk + "f"), RAMTotalfr - (MemoryFree * divisor)) + "/" +
                                            String.format(("%." + idk + "f"), RAMTotalfr) + numstring;
                                } else {
                                    RAMstring = String.format(("%." + idk + "f"), MemoryFree * divisor) + "/" +
                                            String.format(("%." + idk + "f"), RAMTotalfr) + numstring;
                                }
                                break;
                            default:
                                RAMstring = String.format(("%." + idk + "f"), RAMTotalfr) + numstring;
                        }
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

    @Override
    public String getText(boolean example) {
        if (example) return RAMstring;
        UpdRAMamt(false);
        return RAMstring;
    }

    @Override
    protected void drawLine(String line, float x, float y, float scale) {
        color = ((MemoryFree * 1024) >= num2) ? Dclr : Hclr;
        TextRenderer.drawScaledString(line, x, y, color.getRGB(), TextRenderer.TextType.toType(textType), scale);
    }
}
