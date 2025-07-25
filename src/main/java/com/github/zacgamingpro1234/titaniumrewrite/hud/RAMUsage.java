package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import static com.github.zacgamingpro1234.titaniumrewrite.SharedResources.*;

public class RAMUsage extends SingleTextHud {
    public static final long RAMTotal = memory.getTotal();
    public static volatile int RAMFree;
    public static volatile double RAMTotalfr;
    public static volatile String numstring = "";
    public static volatile String RAMstring = "N/A";
    public static volatile double divisor;
    public static volatile int idk = 0;

    @Dropdown(
            name = "Select Unit",
            options = {"Bytes", "KiloBytes", "MegaBytes", "GigaBytes"}
    )
    public static volatile int num = 0;

    @Color(name = "Default Color")
    OneColor Dclr = new OneColor(255, 255, 255);

    @Color(name = "High Usage Color")
    OneColor Hclr = new OneColor(255, 0, 0, 255);

    @Number(
            name = "High Usage Amount",
            min  = 0f,
            max  = 65536f,
            step = 512
    )
    public static int num2 = (int)((RAMTotal / 1024.0 / 1024.0)*.08);

    @Dropdown(
            name = "Select Styling",
            options = {"Percentage", "Amount Free", "Free/Total", "Total"}
    )
    public static int value = 0;

    @Switch(
            name = "Change From Remaining To Using"
    )
    public static boolean Using = false;

    public RAMUsage() {
        super("RAM", false);
    }

    public static void UpdRAMamt() {
        try {
            ThreadManager.execute(() -> {
                try {
                    divisor = Math.pow(1024.0, num);
                    RAMFree = (int) memory.getAvailable();
                    RAMTotalfr = RAMTotal / divisor;

                    switch (num) {
                        case 0: numstring = "B"; idk = 0; break;
                        case 1: numstring = "KB"; idk = 0; break;
                        case 2: numstring = "MB"; idk = 0; break;
                        default: numstring = "GB"; idk = 1; break;
                    }

                    // choose your display mode
                    switch (value) {
                        case 0:
                            if (Using){
                                RAMstring = (int)(100 - (100.0 * RAMFree / RAMTotal)) + "%";
                            }else{
                                RAMstring = (int)(100.0 * RAMFree / RAMTotal) + "%";
                            }
                            break;
                        case 1:
                            if (Using){
                                RAMstring = String.format(("%." + idk + "f"), RAMTotalfr - (RAMFree/divisor)) + numstring;
                            }else{
                                RAMstring = String.format(("%." + idk + "f"), RAMFree/divisor) + numstring;
                            }
                            break;
                        case 2:
                            if (Using){
                                RAMstring = String.format(("%." + idk + "f"), RAMTotalfr - (RAMFree/divisor)) + "/" +
                                        String.format(("%." + idk + "f"), RAMTotalfr) + numstring;
                            }else{
                                RAMstring = String.format(("%." + idk + "f"), RAMFree/divisor) + "/" +
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
            });
        } catch (Exception e) {
            LOGGER.warn(e);
        }
    }

    @Override
    public String getText(boolean example) {
        if (example) return RAMstring;
        UpdRAMamt();
        return RAMstring;
    }

    @Override
    protected void drawLine(String line, float x, float y, float scale) {
        long thresholdBytes = ((long) num2) * 1024L * 1024L;
        color = (memory.getAvailable() >= thresholdBytes) ? Dclr : Hclr;
        TextRenderer.drawScaledString(line, x, y, color.getRGB(), TextRenderer.TextType.toType(textType), scale);
    }
}
