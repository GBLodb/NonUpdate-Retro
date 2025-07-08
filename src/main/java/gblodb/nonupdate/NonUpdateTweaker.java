package gblodb.nonupdate;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.outlands.nonupdate.NonUpdateConfig;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class NonUpdateTweaker implements ITweaker {
    public static File configFile = null;
    public static NonUpdateConfig config = null;
    public static final Logger logger = LogManager.getLogger("NonUpdate Retro");

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        configFile = new File(new File(gameDir, "config"), "nonupdate-retro.json");
        if (configFile != null) {
            logger.info("Using config file {}", configFile.getPath());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (configFile.exists()) {
                try {
                    config = gson.fromJson(new FileReader(configFile), NonUpdateConfig.class);
                } catch (FileNotFoundException e) {
                    logger.error("Failed to read config json", e);
                }
            } else {
                try {
                    Files.touch(configFile);
                    config = new NonUpdateConfig(new String[]{}, false);
                    FileWriter writer = new FileWriter(configFile);
                    gson.toJson(config, writer);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    logger.error("Failed to create json config", e);
                }
            }
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.addClassLoaderExclusion("top.outlands.nonupdate.NonUpdateConfig");
        classLoader.addTransformerExclusion("top.outlands.nonupdate.NonUpdateConfig");
        classLoader.registerTransformer("top.outlands.nonupdate.URLTransformer");
    }

    @Override
    public String getLaunchTarget() {
        return "com.gtnewhorizons.retrofuturabootstrap.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    public static URLConnection openConnection(URL url) throws IOException {
        if (config.debugMode) {
            StackTraceElement[] array = Thread.currentThread().getStackTrace();
            logger.info("Connection to {}, called by {} using openConnection", url.toString(), array[array.length - 2].getClassName());
        }
        throw new IOException("Connection blocked by NonUpdate");
    }

    public static InputStream openStream(URL url) throws IOException {
        if (config.debugMode) {
            StackTraceElement[] array = Thread.currentThread().getStackTrace();
            logger.info("Connection to {}, called by {} using openStream", url.toString(), array[array.length - 2].getClassName());
        }
        throw new IOException("Connection blocked by NonUpdate");
    }
}
