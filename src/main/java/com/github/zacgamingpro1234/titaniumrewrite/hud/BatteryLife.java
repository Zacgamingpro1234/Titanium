package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import cc.polyfrost.oneconfig.renderer.asset.Icon;
import cc.polyfrost.oneconfig.utils.Notifications;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;
import cc.polyfrost.oneconfig.config.annotations.Number;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.Batterywarn;
import static com.github.zacgamingpro1234.titaniumrewrite.config.TitaniumConfig.percentMinimum;

public class BatteryLife extends SingleTextHud {
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final HardwareAbstractionLayer HARDWARE = SYSTEM_INFO.getHardware();
    private static final CountDownLatch tempUpdateLatch = new CountDownLatch(1);
    public static final Icon BATTERY_ICON = new Icon("/Assets/battery-warning.svg");
    private static final Logger LOGGER = LogManager.getLogger("titaniumrewrite");
    private static volatile double percent = Double.NaN;
    private static volatile String percentString = "N/A";
    public static volatile PowerSource batry;
    private static boolean hi = true;
    private static volatile OneColor clr;
    public static volatile boolean charging;
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

    private static void UpdLife() {
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
                    percent = (double) (100 * batry.getCurrentCapacity()) / batry.getMaxCapacity();
                    percentString = String.format(("%." + num + "f"), percent) + "%";
                    charging = batry.isCharging();
                    tempUpdateLatch.countDown();
                } catch (Exception e) {
                    LOGGER.warn(e);
                }
            });
        } catch (Exception e) {
            LOGGER.warn(e);
        }
    }

    public BatteryLife() {
        super("Battery Percentage", false);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
        executor.scheduleAtFixedRate(() -> {
            if (Batterywarn) {
                hi = true;
                UpdLife();
                try {
                    boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                    if ((updated || !Double.isNaN(percent)) && percent >= percentMinimum && batry.isDischarging()) {
                        Notifications.INSTANCE.send("Titanium Rewrite",
                                "Your Battery percentage has reached " + percentString +
                                        "°C, please plug it in", BATTERY_ICON, 10000);
                    }
                } catch (InterruptedException e) {
                    LOGGER.warn(e);
                }
            }
        }, 0, 60, TimeUnit.SECONDS);
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
                        clr = FCclr;
                    } else if (charging) {
                        clr = Cclr;
                    } else if (percent <= num2) {
                        clr = LCclr;
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
