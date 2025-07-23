package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
import io.github.pandalxb.jlibrehardwaremonitor.config.ComputerConfig;
import io.github.pandalxb.jlibrehardwaremonitor.manager.LibreHardwareManager;
import io.github.pandalxb.jlibrehardwaremonitor.model.Sensor;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.github.zacgamingpro1234.titaniumrewrite.SharedResources.*;

public class GPUTemps extends SingleTextHud {
    private static final LibreHardwareManager libreHardwareManager = LibreHardwareManager.createInstance(ComputerConfig.getInstance().setGpuEnabled(true));
    public static volatile double tempGPU = Double.NaN;
    private static volatile String tempstring = "N/A";
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

    public static void UpdTempGPU() {
        try {
            ThreadManager.execute(() -> {
                try {
                    List<Sensor> sensors = libreHardwareManager.querySensors("GPU", "Temperature");
                    for (Sensor sensor : sensors) {
                        tempGPU = sensor.getValue();
                        tempstring = String.format(("%." + num + "f°C"), tempGPU);
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
    }

    @Override
    public String getText(boolean example) {
        if (example) return String.format(("%." + num + "f°C"), 69.0);
        UpdTempGPU();
        return tempstring;
    }

    @Override
    protected void drawLine(String line, float x, float y, float scale) {
        UpdTempGPU();
        ThreadManager.execute(() -> {
            try {
                boolean updated = tempUpdateLatch.await(5, TimeUnit.SECONDS);
                if ((updated || !Double.isNaN(tempGPU))){
                    if (tempGPU >= num2){
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
