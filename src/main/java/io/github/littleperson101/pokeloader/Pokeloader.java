package io.github.littleperson101.pokeloader;

import io.github.littleperson101.pokeloader.config.PokeloaderConfig;
import io.github.littleperson101.pokeloader.integration.PokeloaderConfigScreen;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Pokeloader.MODID)
public class Pokeloader {
    public static final String MODID = "pokeloader";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public Pokeloader() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);

        // 3. Attach the registry to the event bus
        SOUND_EVENTS.register(bus);

        ModLoadingContext.get().registerExtensionPoint(
                net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory(PokeloaderConfigScreen::createScreen)
        );
    }

    private void setup(final FMLCommonSetupEvent event) {
        PokeloaderConfig.load();
        PokeloaderConfig config = PokeloaderConfig.getInstance();
        String version = ModList.get()
                .getModContainerById(MODID)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("UNKNOWN");
        LOGGER.info("[Pokeloader]: Pokeloader {} has been initialized!", version);
        LOGGER.info("[Pokeloader]: Current Configuration Loaded:");
        LOGGER.info("  -> Custom Loading Screen Enabled: {}", config.enableCustomLoadingScreen);
        LOGGER.info("  -> Catch Sound Effects Enabled: {}", config.playCatchSound);
        LOGGER.info("  -> Pokeball Orb Color: {}", config.orbColor);
        LOGGER.info("  -> Animation Linger Time (ms): {}", config.lingerTimeMs);
        LOGGER.info("  -> Invert Animation: {}", config.invertColors);
    }
}