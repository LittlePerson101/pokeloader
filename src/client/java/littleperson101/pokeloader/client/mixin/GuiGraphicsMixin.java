package littleperson101.pokeloader.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    // UNIVERSAL LOGO SUPPRESSOR: Intercepts EVERY sprite engine draw.
    // If the image path belongs to Mojang's branding, it completely drops the draw operation.
    @Inject(method = "blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", at = @At("HEAD"), cancellable = true)
    private void dropMojangLogoDraws(ResourceLocation resourceLocation, int x, int y, int width, int height, CallbackInfo ci) {
        if (resourceLocation != null && (resourceLocation.getPath().contains("mojang") || resourceLocation.getPath().contains("logo"))) {
            ci.cancel(); // Terminate execution before the graphic paints onto the rendering engine!
        }
    }
}
