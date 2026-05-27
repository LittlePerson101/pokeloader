package littleperson101.pokeloader.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
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

    @Shadow @Final private Minecraft minecraft;

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
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        double progress = Math.min(1.0, (double) elapsed / ANIMATION_DURATION_MS);

        boolean isGameLoaded = this.minecraft.screen != null;

        if (isGameLoaded && progress >= 1.0 && fadeStartTime == -1) {
            fadeStartTime = System.currentTimeMillis();
        }

        float alpha = 1.0f;
        if (fadeStartTime != -1) {
            long fadeElapsed = System.currentTimeMillis() - fadeStartTime;
            alpha = 1.0f - Math.min(1.0f, (float) fadeElapsed / 1500.0f); // 1.5s fade out

            if (alpha <= 0.0f) {
                return;
            }
        }

        // Native Audio Trigger utilizing your custom ogg asset registry
        if (progress >= 0.9 && !soundPlayed) {
            try {
                ResourceLocation soundRegistryLocation = ResourceLocation.fromNamespaceAndPath("pokeloader", "pokeball_catch");
                SoundEvent pokeballSound = SoundEvent.createVariableRangeEvent(soundRegistryLocation);

                // Directly fires the audio through Minecraft's master sound system channel
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(pokeballSound, 1.0F));
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

        // Custom Canvas Base
        int bgColor = alphaBits | 0x121212;
        guiGraphics.fill(0, 0, width, height, bgColor);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 1. Lower Shell Geometry (White)
        int whiteColor = alphaBits | 0xFFFFFF;
        guiGraphics.fill(centerX - radius, centerY, centerX + radius, centerY + radius, whiteColor);

        // 2. Upper Shell Geometry (Red)
        int redColor = alphaBits | 0xE63946;
        int upperOffset = (int) ((1.0 - progress) * 40);
        guiGraphics.fill(centerX - radius, centerY - radius - upperOffset, centerX + radius, centerY - upperOffset, redColor);

        // 3. Middle Horizontal Trim Bar
        int darkTrim = alphaBits | 0x2B2B2B;
        guiGraphics.fill(centerX - radius - 2, centerY - 3, centerX + radius + 2, centerY + 3, darkTrim);

        // 4. Center Button Bezel Ring
        guiGraphics.fill(centerX - 14, centerY - 14, centerX + 14, centerY + 14, darkTrim);

        // 5. Center Core Light (Swaps Gray -> Yellow on snap)
        int coreColorHex = (progress < 1.0) ? 0x888888 : 0xFFD166;
        int finalCoreColor = alphaBits | coreColorHex;
        guiGraphics.fill(centerX - 8, centerY - 8, centerX + 8, centerY + 8, finalCoreColor);

        RenderSystem.disableBlend();

        // Stop vanilla graphics operations completely
        ci.cancel();
    }
}