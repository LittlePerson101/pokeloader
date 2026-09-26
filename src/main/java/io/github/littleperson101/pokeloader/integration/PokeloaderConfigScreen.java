package io.github.littleperson101.pokeloader.integration;

import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PokeloaderConfigScreen {

    public static Screen createScreen(Minecraft client, Screen parentScreen) {
        PokeloaderConfig config = PokeloaderConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setTitle(Component.literal("Pokeloader Settings"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Pokéball Screen"), config.enableCustomLoadingScreen)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.enableCustomLoadingScreen = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Play Catch Sound"), config.playCatchSound)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.playCatchSound = val)
                .build());

        general.addEntry(entryBuilder.startIntSlider(Component.literal("Linger Time (ms)"), config.lingerTimeMs, 0, 5000)
                .setDefaultValue(500)
                .setSaveConsumer(val -> config.lingerTimeMs = val)
                .build());

        general.addEntry(entryBuilder.startColorField(Component.literal("Orb Color"), config.orbColor)
                .setDefaultValue(0xFFD166)
                .setSaveConsumer(val -> config.orbColor = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Invert Pokéball Colors"), config.invertColors)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.invertColors = val)
                .build());

        builder.setSavingRunnable(PokeloaderConfig::save);

        return builder.build();
    }
}