package io.github.littleperson101.pokeloader.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.loading.ForgeLoadingOverlay;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeLoadingOverlay.class)
public class ForgeLoadingOverlayMixin {

    @Unique
    private static final long ANIMATION_DURATION_MS = 1000;
    @Unique
    private long startTime = -1;
    @Unique
    private long fadeStartTime = -1;
    @Unique
    private boolean soundPlayed = false;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hijackForgeRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!PokeloaderConfig.getInstance().enableCustomLoadingScreen) {
            return;
        }

        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        Minecraft minecraft = Minecraft.getInstance();
        // Retrieve the private reload instance from the vanilla superclass
        ReloadInstance reload = ((LoadingOverlayAccessor) this).getReload();

        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed >= 900 && !reload.isDone()) {
            startTime = System.currentTimeMillis() - 900;
            elapsed = 900;
        }

        long totalWaitTime = ANIMATION_DURATION_MS + PokeloaderConfig.getInstance().lingerTimeMs;
        double progress = Math.min(1.0, (double) elapsed / ANIMATION_DURATION_MS);

        if (reload.isDone() && elapsed >= totalWaitTime && fadeStartTime == -1) {
            fadeStartTime = System.currentTimeMillis();
        }

        float alpha = 1.0f;
        if (fadeStartTime != -1) {
            long fadeElapsed = System.currentTimeMillis() - fadeStartTime;
            alpha = 1.0f - Math.min(1.0f, (float) fadeElapsed / 2000.0f);
            if (alpha <= 0.0f) {
                return;
            }
        }

        if (PokeloaderConfig.getInstance().playCatchSound && progress >= 0.9 && reload.isDone() && !soundPlayed) {
            try {
                net.minecraft.sounds.SoundEvent pokeballSound = io.github.littleperson101.pokeloader.Pokeloader.POKEBALL_CATCH.get();
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(pokeballSound, 1.0F));
            } catch (Exception e) {
                e.printStackTrace();
            }
            soundPlayed = true;
        }

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = 50;
        int alphaBits = ((int) (alpha * 255)) << 24;

        boolean invert = PokeloaderConfig.getInstance().invertColors;
        java.util.function.IntUnaryOperator applyInvert = c -> invert ? (~c & 0x00FFFFFF) : (c & 0x00FFFFFF);

        guiGraphics.fill(0, 0, width, height, alphaBits | applyInvert.applyAsInt(0x121212));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.fill(centerX - radius, centerY, centerX + radius, centerY + radius, alphaBits | applyInvert.applyAsInt(0xFFFFFF));

        int upperOffset = (int) ((1.0 - progress) * 40);
        guiGraphics.fill(centerX - radius, centerY - radius - upperOffset, centerX + radius, centerY - upperOffset, alphaBits | applyInvert.applyAsInt(0xE63946));

        guiGraphics.fill(centerX - radius - 2, centerY - 3, centerX + radius + 2, centerY + 3, alphaBits | applyInvert.applyAsInt(0x2B2B2B));
        guiGraphics.fill(centerX - 14, centerY - 14, centerX + 14, centerY + 14, alphaBits | applyInvert.applyAsInt(0x2B2B2B));

        int coreColor = (progress < 1.0) ? 0x888888 : PokeloaderConfig.getInstance().orbColor;
        guiGraphics.fill(centerX - 8, centerY - 8, centerX + 8, centerY + 8, alphaBits | applyInvert.applyAsInt(coreColor));

        RenderSystem.disableBlend();

        // Cancel the remainder of the method so Forge's anvil and text are never drawn
        ci.cancel();
    }
}