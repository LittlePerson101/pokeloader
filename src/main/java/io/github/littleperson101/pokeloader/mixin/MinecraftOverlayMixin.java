package io.github.littleperson101.pokeloader.mixin;

import io.github.littleperson101.pokeloader.client.screen.PokeloaderIntroScreen;
import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftOverlayMixin {

    @Shadow public Overlay overlay;
    @Shadow public Screen screen;
    @Shadow public abstract void setScreen(Screen screen);

    // 1. Catches initial launch: When MC sets TitleScreen at the end of loading, kill overlay instantly
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void interceptScreenWithOverlay(Screen newScreen, CallbackInfo ci) {
        if (this.overlay != null && PokeloaderConfig.getInstance().enableCustomLoadingScreen) {
            if (!(newScreen instanceof PokeloaderIntroScreen)) {
                // Kill overlay immediately so it doesn't do its 1-second fade-out over the title screen
                this.overlay = null;
                // Seamlessly mount our custom intro screen
                this.setScreen(new PokeloaderIntroScreen(newScreen));
                ci.cancel();
            }
        }
    }

    // 2. Catches F3 + T reloads: When overlay clears without changing screen (e.g. in-game reload)
    @Inject(method = "setOverlay", at = @At("HEAD"))
    private void interceptOverlayClear(Overlay newOverlay, CallbackInfo ci) {
        if (newOverlay == null && this.overlay != null && PokeloaderConfig.getInstance().enableCustomLoadingScreen) {
            if (!(this.screen instanceof PokeloaderIntroScreen)) {
                this.setScreen(new PokeloaderIntroScreen(this.screen));
            }
        }
    }
}