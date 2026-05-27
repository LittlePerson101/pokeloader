package littleperson101.pokeloader.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PokeloaderConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "pokeloader.json");

    // ==========================================
    // CONFIGURATION OPTIONS
    // ==========================================
    public boolean enableCustomLoadingScreen = true;
    public boolean playCatchSound = true;

    // ==========================================
    // BACKEND LOGIC
    // ==========================================
    private static PokeloaderConfig instance;

    public static PokeloaderConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                instance = GSON.fromJson(reader, PokeloaderConfig.class);
            } catch (Exception e) {
                e.printStackTrace();
                instance = new PokeloaderConfig();
            }
        } else {
            instance = new PokeloaderConfig();
            save();
        }
    }

    public static void save() {
        if (instance == null) instance = new PokeloaderConfig();
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(instance, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}