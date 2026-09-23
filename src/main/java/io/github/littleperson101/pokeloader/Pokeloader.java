package io.github.littleperson101.pokeloader;

import io.github.littleperson101.pokeloader.client.PokeloaderConfigScreen;
import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Pokeloader.MOD_ID)
public class Pokeloader {
    public static final String MOD_ID = "pokeloader";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public Pokeloader(IEventBus modEventBus, ModContainer modContainer) {
        PokeloaderConfig.load();
        PokeloaderConfig config = PokeloaderConfig.getInstance();

        String version = modContainer.getModInfo().getVersion().toString();

        LOGGER.info("[Pokeloader]: Pokeloader {} has been initialized!", version);
        LOGGER.info("[Pokeloader]: Current Configuration Loaded:");
        LOGGER.info("  -> Custom Loading Screen Enabled: {}", config.enableCustomLoadingScreen);
        LOGGER.info("  -> Catch Sound Effects Enabled: {}", config.playCatchSound);
        LOGGER.info("  -> Pokeball Orb Color: {}", config.orbColor);
        LOGGER.info("  -> Animation Linger Time (ms): {}", config.lingerTimeMs);
        LOGGER.info("  -> Invert Animation: {}", config.invertColors);

        if (FMLLoader.getDist().isClient()) {
            modContainer.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (container, parent) -> PokeloaderConfigScreen.create(parent)
            );
        }
    }
}