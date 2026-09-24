package io.github.littleperson101.pokeloader.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class PokeloaderIntroScreen extends Screen {
    private static final long ANIMATION_DURATION_MS = 1000;
    private long startTime = -1;
    private boolean soundPlayed = false;
    private final Screen parentScreen;

    public PokeloaderIntroScreen(Screen parentScreen) {
        super(Component.literal("Pokeloader Intro"));
        this.parentScreen = parentScreen;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        long elapsed = System.currentTimeMillis() - startTime;

        // Safety check: If user config value is small (e.g. 2 or 3), assume seconds and convert to ms
        long configuredLinger = PokeloaderConfig.getInstance().lingerTimeMs;
        long lingerMs = (configuredLinger < 100) ? (configuredLinger * 1000L) : configuredLinger;

        long totalWaitTime = ANIMATION_DURATION_MS + lingerMs;

        // Progress caps at 1.0 at 1000ms, holding the assembled Pokéball state during lingerMs
        double progress = Math.min(1.0, (double) elapsed / ANIMATION_DURATION_MS);

        // Smooth 150ms fade-in
        float fadeIn = Math.min(1.0f, elapsed / 150.0f);
        int alphaBits = ((int) (fadeIn * 255)) << 24;

        // Sound trigger
        if (PokeloaderConfig.getInstance().playCatchSound && progress >= 0.9 && !soundPlayed) {
            try {
                ResourceLocation soundRegistryLocation = ResourceLocation.fromNamespaceAndPath("pokeloader", "pokeball_catch");
                SoundEvent pokeballSound = SoundEvent.createVariableRangeEvent(soundRegistryLocation);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(pokeballSound, 1.0F));
            } catch (Exception e) {
                e.printStackTrace();
            }
            soundPlayed = true;
        }

        // Screen completion transition after animation (1000ms) + linger time
        if (elapsed >= totalWaitTime) {
            Minecraft.getInstance().setScreen(this.parentScreen);
            return;
        }

        int width = this.width;
        int height = this.height;
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = 50;

        boolean invert = PokeloaderConfig.getInstance().invertColors;

        java.util.function.IntUnaryOperator applyInvert = c -> {
            int rgb = invert ? (~c & 0x00FFFFFF) : (c & 0x00FFFFFF);
            return alphaBits | rgb;
        };

        // Dark background fill
        guiGraphics.fill(0, 0, width, height, applyInvert.applyAsInt(0x121212));

        RenderSystem.enableBlend();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, 0);

        // White bottom half
        guiGraphics.fill(-radius, 0, radius, radius, applyInvert.applyAsInt(0xFFFFFF));

        // Red top half (animated assembly, locks at 0 offset once progress hits 1.0)
        int upperOffset = (int) ((1.0 - progress) * 40);
        guiGraphics.fill(-radius, -radius - upperOffset, radius, -upperOffset, applyInvert.applyAsInt(0xE63946));

        // Trim bars
        guiGraphics.fill(-radius - 2, -3, radius + 2, 3, applyInvert.applyAsInt(0x2B2B2B));
        guiGraphics.fill(-14, -14, 14, 14, applyInvert.applyAsInt(0x2B2B2B));

        // Core button
        int coreColor = (progress < 1.0) ? 0x888888 : PokeloaderConfig.getInstance().orbColor;
        guiGraphics.fill(-8, -8, 8, 8, applyInvert.applyAsInt(coreColor));

        guiGraphics.pose().popPose();
        RenderSystem.disableBlend();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}