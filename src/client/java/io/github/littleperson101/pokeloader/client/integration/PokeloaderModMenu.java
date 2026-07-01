package io.github.littleperson101.pokeloader.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import net.minecraft.network.chat.Component;

import java.awt.Color;

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
                                    .description(OptionDescription.of(Component.literal("Toggle the custom Pokeball loading overlay.")))
                                    .binding(true, () -> config.enableCustomLoadingScreen, val -> config.enableCustomLoadingScreen = val)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Play Catch Sound"))
                                    .description(OptionDescription.of(Component.literal("Plays the Pokeball catch sound effect.")))
                                    .binding(true, () -> config.playCatchSound, val -> config.playCatchSound = val)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.literal("Linger Time (ms)"))
                                    .description(OptionDescription.of(Component.literal("How long the animation stays visible after finishing.")))
                                    .binding(500, () -> config.lingerTimeMs, val -> config.lingerTimeMs = val)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 5000).step(100))
                                    .build())
                            .option(Option.<Color>createBuilder()
                                    .name(Component.literal("Orb Color"))
                                    .description(OptionDescription.of(Component.literal("Changes the color of the Pokeball's center orb.")))
                                    .binding(new Color(0xFFD166), () -> new Color(config.orbColor), val -> config.orbColor = val.getRGB())
                                    .controller(ColorControllerBuilder::create)
                                    .build())
                            .build())
                    .save(PokeloaderConfig::save)
                    .build()
                    .generateScreen(parentScreen);
        };
    }
}