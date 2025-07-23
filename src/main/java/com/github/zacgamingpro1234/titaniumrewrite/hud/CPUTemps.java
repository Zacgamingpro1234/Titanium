package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import java.util.concurrent.TimeUnit;
import static com.github.zacgamingpro1234.titaniumrewrite.SharedResources.*;

public class CPUTemps extends SingleTextHud {
    public static volatile double tempCPU = Double.NaN;
    private static volatile String cpuTempString = "N/A";
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

    public static void UpdTempCPU() {
        try {
            ThreadManager.execute(() -> {
                try {
                    tempCPU = SENSORS.getCpuTemperature();
                    cpuTempString = String.format(("%." + num + "f°C"), tempCPU);
                    tempUpdateLatch.countDown();
                } catch (Exception e) {
                    LOGGER.warn(e);
                }
            });
    } catch (Exception e) {
        LOGGER.warn(e);
    }
}

    public CPUTemps() {
        super("CPU Temps", false);
    }

    @Override
    public String getText(boolean example) {
        if (example) return "69.9°C";
        UpdTempCPU();
        return cpuTempString;
    }

    @Override
    protected void drawLine(String line, float x, float y, float scale) {
        UpdTempCPU();
        ThreadManager.execute(() -> {
            try {
                boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                if ((updated || !Double.isNaN(tempCPU))){
                    if (tempCPU >= num2){
                        color = Hclr;
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
