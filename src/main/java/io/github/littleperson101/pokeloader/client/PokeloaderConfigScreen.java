/**
 *
 * This is PokeloaderConfigScreen, it replaces the Fabric class PokeloaderModMenu due to Mod Menu's
 * non-existent presence in NeoForge lol.
 * This still uses YACL things tho...
 *
 */

package io.github.littleperson101.pokeloader.client;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.awt.Color;

public class PokeloaderConfigScreen {
    public static Screen create(Screen parent) {
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
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Invert Pokéball Colors"))
                                .description(OptionDescription.of(Component.literal("Inverts the colors of all Pokéball elements.")))
                                .binding(
                                        false,
                                        () -> config.invertColors,
                                        val -> config.invertColors = val
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .build())
                .save(PokeloaderConfig::save)
                .build()
                .generateScreen(parent);
    }
}