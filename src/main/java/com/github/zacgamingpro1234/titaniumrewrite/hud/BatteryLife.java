package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import cc.polyfrost.oneconfig.utils.Notifications;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import oshi.hardware.PowerSource;
import cc.polyfrost.oneconfig.config.annotations.Number;
import java.util.concurrent.TimeUnit;
import static com.github.zacgamingpro1234.titaniumrewrite.SharedResources.*;

public class BatteryLife extends SingleTextHud {
    public static volatile double percent = Double.NaN;
    public static volatile String percentString = "No Battery Detected";
    public static volatile PowerSource batry;
    private static volatile  boolean hi = true;
    public static volatile boolean charging;
    public static volatile boolean check = true;
    @Number(
            name = "Decimal Accuracy",    // name of the component
            min = 0, max = 6,        // min and max values (anything above/below is set to the max/min
            step = 1        // each time the arrow is clicked it will increase/decrease by this amount
    )
    public static int num = 2; // default value
    @Color(
            name = "Discharging Color"
    )
    OneColor Dclr = new OneColor(255, 255, 255);
    @Color(
            name = "Full Charge Color"
    )
    OneColor FCclr = new OneColor(0, 255, 0, 255);
    @Color(
            name = "Charging Color"
    )
    OneColor Cclr = new OneColor(0, 155, 0, 255);
    @Color(
            name = "Low Charge Color"
    )
    OneColor LCclr = new OneColor(155, 0, 0, 255);
    @Number(
            name = "Low Charge Amount",    // name of the component
            min = 0f, max = 99.9f,        // min and max values (anything above/below is set to the max/min
            step = 5       // each time the arrow is clicked it will increase/decrease by this amount
    )
    public static float num2 = 15; // default value

    public static void check(){
        check = true;
    }
    public static void hi(){
        hi = true;
    }

    public static void UpdLife() {
        if (check) {
            try {
                ThreadManager.execute(() -> {
                    try {
                        PowerSource ign = null;
                        for (PowerSource battery : HARDWARE.getPowerSources()) {
                            if ((batry == null) || (ign == null)) {
                                ign = battery;
                                batry = battery;
                            } else {
                                if (hi) {
                                    LOGGER.warn("More Than 1 Battery Detected, Used First One Named As: {}", batry.getName());
                                    hi = false;
                                }
                            }
                        }
                        if (batry == null) {
                            LOGGER.warn("No Batteries Detected");
                            Notifications.INSTANCE.send("Titanium Rewrite","No Batteries Detected," +
                                            " If You Think This Is Wrong Please Re-check In The Config");
                            check = false;
                        }else{
                            percent = (double) (100 * batry.getCurrentCapacity()) / batry.getMaxCapacity();
                            percentString = String.format(("%." + num + "f"), percent) + "%";
                            charging = batry.isCharging();
                            tempUpdateLatch.countDown();
                        }
                    } catch (Exception e) {
                        LOGGER.warn(e);
                    }
                });
            } catch (Exception e) {
                LOGGER.warn(e);
            }
        }
    }

    public BatteryLife() {
        super("Battery Percentage", false);
    }

    @Override
    public String getText(boolean example) {
        if (example) return "69%";
        UpdLife();
        return percentString;
    }

    @Override
    protected void drawLine(String line, float x, float y, float scale) {
        UpdLife();
        ThreadManager.execute(() -> {
            try {
                boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                if ((updated || !Double.isNaN(percent))){
                    if (percent >= 100){
                        color = FCclr;
                    } else if (charging) {
                        color = Cclr;
                    } else if (percent <= num2) {
                        color = LCclr;
                    } else {
                        color = Dclr;
                    }
                }else{
                    color = Dclr;
                }
            } catch (InterruptedException e) {
                LOGGER.warn(e);
            }
        });
        TextRenderer.drawScaledString(line, x, y, color.getRGB(), TextRenderer.TextType.toType(textType), scale);
    }
}
