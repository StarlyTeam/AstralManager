package kr.starly.manager.task;

import com.google.gson.JsonElement;
import kr.starly.manager.AstralManager;
import kr.starly.manager.util.AstralPluginUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UpdateCheckerTask implements Runnable {

    private static BukkitTask task;

    public static BukkitTask run(Plugin plugin) {
        return task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new UpdateCheckerTask(), 0L, 144000L /* 6시간 */);
    }

    public static void stop() {
        task.cancel();
        task = null;
    }

    @Override
    public void run() {
        AstralManager plugin = AstralManager.getInstance();
        Logger logger = plugin.getLogger();
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        if (pluginManager.getPlugin("StarlyCore") == null) {
            logger.warning("코어 플러그인이 설치되지 않아, 버전체크를 실행할 수 없습니다.");
            return;
        }


        logger.info("최신버전 불러오기중...");

        try {
            Map<String, JsonElement> pluginDatas = AstralPluginUtil.fetchInstalledPluginList();

            List<String> outDatedPlugins = new ArrayList<>();
            pluginDatas.forEach((pluginName, pluginData) -> {
                Plugin plugin1 = pluginManager.getPlugin(pluginName);

                String installedVersion = plugin1.getDescription().getVersion();
                String latestVersion = pluginData.getAsJsonObject().get("version").getAsString();
                if (!installedVersion.equals(latestVersion)) {
                    outDatedPlugins.add(pluginName);
                }
            });

            logger.warning("다음 플러그인이 업데이트 되지 않았습니다: " + outDatedPlugins);
        } catch (IOException ex) {
            ex.printStackTrace();

            logger.severe("버전체크를 실행하는 도중, 오류가 발생했습니다.");
        }
    }
}