package kr.starly.astral.manager.collector;

import kr.starly.astral.manager.AstralManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class LuckPermsSettingCollector {

    private LuckPermsSettingCollector() {}

    public static void collectData(File exportFile) throws IOException {
        if (!exportFile.getName().endsWith(".txt")) return;
        if (!exportFile.exists()) exportFile.createNewFile();

        try {
            LuckPerms.class.getName();
        } catch (NoClassDefFoundError ignored) {
            return;
        }

        AstralManager plugin = AstralManager.getInstance();
        ServicesManager servicesManager = plugin.getServer().getServicesManager();
        RegisteredServiceProvider<LuckPerms> lpProvider = servicesManager.getRegistration(LuckPerms.class);

        try (
                FileWriter fw = new FileWriter(exportFile);
                BufferedWriter bw = new BufferedWriter(fw)
        ) {
            LuckPerms luckPerms = lpProvider.getProvider();
            Set<Group> groups = luckPerms.getGroupManager().getLoadedGroups();

            for (Group group : groups) {
                bw.write("[" + group.getName() + "]"); bw.newLine();
                for (Node node : group.getNodes()) {
                    bw.write(node.getKey() + ": " + node.getValue());
                }

                bw.newLine();
            }
        }
    }
}