package littleperson101.pokeloader.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import littleperson101.pokeloader.config.PokeloaderConfig;
import net.minecraft.network.chat.Component;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;

public class PokeloaderModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> {
            PokeloaderConfig config = PokeloaderConfig.getInstance();

            return YetAnotherConfigLib.createBuilder()
                    .title(Component.literal("PokeLoader Settings"))
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("General"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Enable Pokéball Screen"))
                                    .description(OptionDescription.of(Component.literal("Toggle the custom Pokeball loading overlay screen on or off.")))
                                    .binding(
                                            true,
                                            () -> config.enableCustomLoadingScreen,
                                            val -> config.enableCustomLoadingScreen = val
                                    )
                                    // This matches the verified YACL documentation standard
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Play Catch Sound"))
                                    .description(OptionDescription.of(Component.literal("Toggle the audio effects when the ball clicks shut.")))
                                    .binding(
                                            true,
                                            () -> config.playCatchSound,
                                            val -> config.playCatchSound = val
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .build())
                    .save(PokeloaderConfig::save)
                    .build()
                    .generateScreen(parentScreen);
        };
    }
}