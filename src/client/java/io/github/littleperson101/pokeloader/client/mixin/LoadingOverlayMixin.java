package io.github.littleperson101.pokeloader.client.mixin;

import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
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

    @Shadow @Final private Minecraft minecraft;

    @Unique private static boolean isRendering = false;
    @Unique private long startTime = -1;
    @Unique private long fadeStartTime = -1;
    @Unique private boolean soundPlayed = false;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void drawProceduralPokeball(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("animated-mojang-logo")) return;
        if (isRendering) return;

        PokeloaderConfig config = PokeloaderConfig.getInstance();
        if (!config.enableCustomLoadingScreen) return;

        isRendering = true;
        try {
            if (startTime == -1) startTime = System.currentTimeMillis();
            long elapsed = System.currentTimeMillis() - startTime;

            // Logic: progress reaches 1.0 at 1000ms
            double progress = Math.min(1.0, (double) elapsed / 1000.0);

            // Handle Linger Time and Fade
            boolean isGameLoaded = this.minecraft.getDebugOverlay() == null || this.minecraft.gui.screen() != null;
            long totalWaitTime = 1000 + config.lingerTimeMs;

            if (isGameLoaded && elapsed >= totalWaitTime && fadeStartTime == -1) {
                fadeStartTime = System.currentTimeMillis();
            }

            float alpha = 1.0f;
            if (fadeStartTime != -1) {
                long fadeElapsed = System.currentTimeMillis() - fadeStartTime;
                alpha = 1.0f - Math.min(1.0f, (float) fadeElapsed / 1000.0f);
            }

            if (alpha <= 0.0f) {
                isRendering = false;
                return;
            }

            // Sound Logic
            if (config.playCatchSound && progress >= 0.9 && !soundPlayed) {
                Identifier soundId = Identifier.fromNamespaceAndPath("pokeloader", "pokeball_catch");
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(soundId), 1.0F));
                soundPlayed = true;
            }

            // Rendering Logic
            int width = guiGraphics.guiWidth();
            int height = guiGraphics.guiHeight();
            int centerX = width / 2;
            int centerY = height / 2;
            int radius = 50;
            int alphaBits = ((int) (alpha * 255)) << 24;

            // Inversion helper
            java.util.function.IntUnaryOperator applyInvert = c -> config.invertColors ? (~c & 0x00FFFFFF) : (c & 0x00FFFFFF);

            // Background
            guiGraphics.fill(0, 0, width, height, alphaBits | applyInvert.applyAsInt(0x121212));

            // White part
            guiGraphics.fill(centerX - radius, centerY, centerX + radius, centerY + radius, alphaBits | applyInvert.applyAsInt(0xFFFFFF));

            // Red part (moves based on progress)
            int upperOffset = (int) ((1.0 - progress) * 40);
            guiGraphics.fill(centerX - radius, centerY - radius - upperOffset, centerX + radius, centerY - upperOffset, alphaBits | applyInvert.applyAsInt(0xE63946));

            // Trim
            guiGraphics.fill(centerX - radius - 2, centerY - 3, centerX + radius + 2, centerY + 3, alphaBits | applyInvert.applyAsInt(0x2B2B2B));
            guiGraphics.fill(centerX - 14, centerY - 14, centerX + 14, centerY + 14, alphaBits | applyInvert.applyAsInt(0x2B2B2B));

            // Core (Uses config orbColor)
            int coreColor = (progress < 1.0) ? 0x888888 : config.orbColor;
            guiGraphics.fill(centerX - 8, centerY - 8, centerX + 8, centerY + 8, alphaBits | applyInvert.applyAsInt(coreColor));

            ci.cancel();
        } finally {
            isRendering = false;
        }
    }
}