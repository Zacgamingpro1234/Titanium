//package com.github.zacgamingpro1234.titaniumrewrite.hud;
//
//import cc.polyfrost.oneconfig.config.annotations.Color;
//import cc.polyfrost.oneconfig.config.annotations.Number;
//import cc.polyfrost.oneconfig.config.core.OneColor;
//import cc.polyfrost.oneconfig.hud.SingleTextHud;
//import cc.polyfrost.oneconfig.renderer.TextRenderer;
//import com.github.zacgamingpro1234.titaniumrewrite.ThreadManager;
//import io.github.pandalxb.jlibrehardwaremonitor.model.Hardware;
//import io.github.pandalxb.jlibrehardwaremonitor.model.Sensor;
//import oshi.hardware.GraphicsCard;
//
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static com.github.zacgamingpro1234.titaniumrewrite.SharedResources.*;
//
//public class GPUVram extends SingleTextHud {
//    public static volatile double VramAmt = Double.NaN;
//    private static volatile String VramAmtString = "N/A";
//    private static CountDownLatch PrivLatch = new CountDownLatch(1);
//    @Number(
//            name = "Decimal Accuracy",    // name of the component
//            min = 0, max = 6,        // min and max values (anything above/below is set to the max/min
//            step = 1        // each time the arrow is clicked it will increase/decrease by this amount
//    )
//    public static int num = 2; // default value
//    @Color(
//            name = "Default Color"
//    )
//    OneColor Dclr = new OneColor(255, 255, 255);
//    @Color(
//            name = "High Usage Color"
//    )
//    OneColor Hclr = new OneColor(255, 0, 0, 255);
//    @Number(
//            name = "High Usage Amount",    // name of the component
//            min = 0f, max = 110f,        // min and max values (anything above/below is set to the max/min
//            step = 5       // each time the arrow is clicked it will increase/decrease by this amount
//    )
//    public static float num2 = 95; // default value
//
//    public static void UpdVram() {
//        try {
//            PrivLatch = new CountDownLatch(1);
//            ThreadManager.execute(() -> {
//                try {
//                    for (GraphicsCard gpu : SYSTEM_INFO.getHardware().getGraphicsCards()) {
//                        LOGGER.info("GPU name: {}", MainGPU);
//                        LOGGER.info("GPU: {}", gpu.getName());
//                        LOGGER.info("Driver Version: {}", gpu.getVersionInfo());
//                        LOGGER.info("VRAM Amount: {}MB", gpu.getVRam()/1024/1024);
//                    }
//                    List<Sensor> sensors = libreHardwareManager.querySensors("GPU", "SmallData");
//                    List<Hardware> specs = libreHardwareManager.getComputer().getHardware();
//                    LOGGER.info("THEM SPECS: {}", specs);
//                    for (Sensor sensor : sensors) {
//                        LOGGER.info("sensor name: {}", sensor.getName());
//                        LOGGER.info("sensor val: {}", sensor.getValue());
//                    }
//                    tempUpdateLatch.countDown();
//                    PrivLatch.countDown();
//                } catch (Exception e) {
//                    LOGGER.warn(e);
//                }
//            });
//    } catch (Exception e) {
//        LOGGER.warn(e);
//    }
//}
//
//    public GPUVram() {
//        super("GPU Vram", false);
//        LOGGER.info("GPU name2: {}", MainGPU);
//    }
//
//    @Override
//    public String getText(boolean example) {
//        if (example) return String.format(("%." + num + "f"), 69.0) + "%";
//        UpdVram();
//        return VramAmtString;
//    }
//
//    @Override
//    protected void drawLine(String line, float x, float y, float scale) {
//        UpdVram();
//        ThreadManager.execute(() -> {
//            try {
//                boolean updated = PrivLatch.await(5, TimeUnit.SECONDS);
//                if ((updated || !Double.isNaN(VramAmt))){
//                    if (VramAmt >= num2){
//                        color = Hclr;
//                    } else {
//                        color = Dclr;
//                    }
//                }else{
//                    color = Dclr;
//                }
//            } catch (InterruptedException e) {
//                LOGGER.warn(e);
//            }
//        });
//        TextRenderer.drawScaledString(line, x, y, color.getRGB(), TextRenderer.TextType.toType(textType), scale);
//    }
//}
