package io.github.littleperson101.pokeloader;

import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pokeloader implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pokeloader");

    @Override
    public void onInitialize() {
        PokeloaderConfig.load();
        PokeloaderConfig config = PokeloaderConfig.getInstance();
        String version = FabricLoader.getInstance()
                .getModContainer("pokeloader")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("UNKNOWN");
        LOGGER.info("[Pokeloader]: Pokeloader {} has been initialized!", version);
        LOGGER.info("[Pokeloader]: Current Configuration Loaded:");
        LOGGER.info("  -> Custom Loading Screen Enabled: {}", config.enableCustomLoadingScreen);
        LOGGER.info("  -> Catch Sound Effects Enabled: {}", config.playCatchSound);
    }
}