package kr.starly.manager.collector;

import kr.starly.manager.AstralManager;
import kr.starly.manager.util.AstralPluginUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class StarlyPluginConfigCollector {

    private StarlyPluginConfigCollector() {}

    public static void collectData(File exportFile) throws IOException {
        if (!exportFile.isDirectory()) return;
        if (!exportFile.exists()) exportFile.mkdirs();

        Set<String> astralPlugins = AstralPluginUtil.fetchPluginList().keySet();
        for (String astralPlugin : astralPlugins) {
            AstralManager plugin = AstralManager.getInstance();
            PluginManager pluginManager = plugin.getServer().getPluginManager();

            Plugin plugin1 = pluginManager.getPlugin(astralPlugin);
            if (!plugin1.getDataFolder().exists()) continue;

            for (File dataFile : FileUtils.listFiles(plugin1.getDataFolder(), null, true)) {
                String[] pathSegments = dataFile.getPath().split("\\\\");

                FileUtils.copyFile(dataFile, new File(exportFile, plugin1.getName() + "-" + String.join("_", Arrays.copyOfRange(pathSegments, 2, pathSegments.length))));
            }
        }
    }
}