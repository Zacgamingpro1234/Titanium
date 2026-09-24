package com.github.zacgamingpro1234.titanium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.zacgamingpro1234.titanium.Titanium;

import net.minecraft.client.gui.screen.menu.TitleScreen;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

	@Inject(method = "init", at = @At("TAIL"))
	private void exampleMod$onInit(CallbackInfo ci) {
		Titanium.LOGGER.info("This line is printed by an example mod mixin!");
	}
}
