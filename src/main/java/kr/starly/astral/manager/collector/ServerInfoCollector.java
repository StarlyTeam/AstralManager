package kr.starly.astral.manager.collector;

import kr.starly.astral.manager.AstralManager;
import org.bukkit.Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServerInfoCollector {

    private ServerInfoCollector() {}

    public static void collectData(File exportFile) throws IOException {
        if (!exportFile.getName().endsWith(".txt")) return;
        if (!exportFile.exists()) exportFile.createNewFile();

        try (
                FileWriter fw = new FileWriter(exportFile);
                BufferedWriter bw = new BufferedWriter(fw)
        ) {
            AstralManager plugin = AstralManager.getInstance();
            Server server = plugin.getServer();

            bw.write("Server Name: " + server.getName()); bw.newLine();
            bw.write("Server Version: " + server.getVersion()); bw.newLine();
            bw.write("Server Bukkit Version: " + server.getBukkitVersion());
        }
    }
}