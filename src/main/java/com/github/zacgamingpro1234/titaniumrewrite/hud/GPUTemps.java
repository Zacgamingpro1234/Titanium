package com.github.zacgamingpro1234.titaniumrewrite.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;

public class GPUTemps extends SingleTextHud {
    public GPUTemps() {
        super("GPU Temps", false);
    }

    @Override
    public String getText(boolean example) {
        return "I'm an example HUD";
    }
}
