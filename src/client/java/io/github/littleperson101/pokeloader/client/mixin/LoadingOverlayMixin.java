package io.github.littleperson101.pokeloader.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private static final long ANIMATION_DURATION_MS = 1000;
    @Unique
    private long startTime = -1;
    @Unique
    private long fadeStartTime = -1;
    @Unique
    private boolean soundPlayed = false;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void drawProceduralPokeball(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!PokeloaderConfig.getInstance().enableCustomLoadingScreen) {
            return;
        }

        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        // 1. Calculate timing
        long elapsed = System.currentTimeMillis() - startTime;
        long totalWaitTime = ANIMATION_DURATION_MS + PokeloaderConfig.getInstance().lingerTimeMs;

        // progress stays at 1.0 during the linger phase
        double progress = Math.min(1.0, (double) elapsed / ANIMATION_DURATION_MS);

        // 2. Logic: Handle fade start after animation + linger
        boolean isGameLoaded = this.minecraft.screen != null;
        if (isGameLoaded && elapsed >= totalWaitTime && fadeStartTime == -1) {
            fadeStartTime = System.currentTimeMillis();
        }

        // 3. Calculate alpha for fading
        float alpha = 1.0f;
        if (fadeStartTime != -1) {
            long fadeElapsed = System.currentTimeMillis() - fadeStartTime;
            alpha = 1.0f - Math.min(1.0f, (float) fadeElapsed / 2000.0f);
            if (alpha <= 0.0f) {
                return;
            }
        }

        // 4. Handle Sound
        if (PokeloaderConfig.getInstance().playCatchSound && progress >= 0.9 && !soundPlayed) {
            try {
                ResourceLocation soundRegistryLocation = ResourceLocation.fromNamespaceAndPath("pokeloader", "pokeball_catch");
                SoundEvent pokeballSound = SoundEvent.createVariableRangeEvent(soundRegistryLocation);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(pokeballSound, 1.0F));
            } catch (Exception e) {
                e.printStackTrace();
            }
            soundPlayed = true;
        }

        // 5. Drawing
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = 50;
        int alphaBits = ((int) (alpha * 255)) << 24;

        // Background and base shapes
        guiGraphics.fill(0, 0, width, height, alphaBits | 0x121212);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.fill(centerX - radius, centerY, centerX + radius, centerY + radius, alphaBits | 0xFFFFFF); // White

        int upperOffset = (int) ((1.0 - progress) * 40);
        guiGraphics.fill(centerX - radius, centerY - radius - upperOffset, centerX + radius, centerY - upperOffset, alphaBits | 0xE63946); // Red

        guiGraphics.fill(centerX - radius - 2, centerY - 3, centerX + radius + 2, centerY + 3, alphaBits | 0x2B2B2B); // Trim
        guiGraphics.fill(centerX - 14, centerY - 14, centerX + 14, centerY + 14, alphaBits | 0x2B2B2B); // Trim

        // Core (uses Config orbColor)
        int coreColorHex = (progress < 1.0) ? 0x888888 : PokeloaderConfig.getInstance().orbColor;
        guiGraphics.fill(centerX - 8, centerY - 8, centerX + 8, centerY + 8, alphaBits | coreColorHex);

        RenderSystem.disableBlend();
        ci.cancel();
    }
}