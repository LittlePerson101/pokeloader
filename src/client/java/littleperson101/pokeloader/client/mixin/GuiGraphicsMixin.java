package littleperson101.pokeloader.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Inject(method = "blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", at = @At("HEAD"), cancellable = true)
    private void dropMojangLogoDraws(ResourceLocation resourceLocation, int x, int y, int width, int height, CallbackInfo ci) {
        if (!littleperson101.pokeloader.config.PokeloaderConfig.getInstance().enableCustomLoadingScreen) {
            return;
        }
        if (resourceLocation != null && (resourceLocation.getPath().contains("mojang") || resourceLocation.getPath().contains("logo"))) {
            ci.cancel();
        }
    }
}
