package kr.starly.astral.manager.collector;

import kr.starly.astral.manager.AstralManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PluginDataCollector {

    private PluginDataCollector() {}

    public static void collectData(File exportFile) throws IOException {
        if (!exportFile.getName().endsWith(".txt")) return;
        if (!exportFile.exists()) exportFile.createNewFile();

        try (
                FileWriter fw = new FileWriter(exportFile);
                BufferedWriter bw = new BufferedWriter(fw)
        ) {
            AstralManager plugin = AstralManager.getInstance();
            PluginManager pluginManager = plugin.getServer().getPluginManager();

            for (Plugin plugin1 : pluginManager.getPlugins()) {
                bw.write("[" + plugin1.getName() + "]"); bw.newLine();
                bw.write("Version: " + plugin1.getDescription().getVersion()); bw.newLine();
                bw.write("Dependency: " + plugin1.getDescription().getDepend().toString()); bw.newLine();
                bw.write("Soft Dependency: " + plugin1.getDescription().getSoftDepend().toString()); bw.newLine();
                bw.write("Authors: " + plugin1.getDescription().getAuthors().toString()); bw.newLine();

                bw.newLine();
            }
        }
    }
}