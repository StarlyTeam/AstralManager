package kr.starly.manager.command.sub.tools;

import kr.starly.manager.AstralManager;
import kr.starly.manager.collector.LuckPermsSettingCollector;
import kr.starly.manager.collector.PluginDataCollector;
import kr.starly.manager.collector.ServerInfoCollector;
import kr.starly.manager.collector.StarlyPluginConfigCollector;
import kr.starly.manager.command.SubCommandExecutor;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static kr.starly.manager.context.MessageContext.PREFIX;

public class ReportExecutor implements SubCommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(PREFIX + "§c명령어가 올바르지 않습니다.");
            return false;
        }

        AstralManager plugin = AstralManager.getInstance();

        Thread thread = new Thread(() -> {
            File reportFolder = new File(plugin.getDataFolder(), "tmp/" + System.currentTimeMillis());
            if (!reportFolder.exists()) reportFolder.mkdirs();

            try {
                File serverInfo = new File(reportFolder, "serverInfo.txt");

                ServerInfoCollector.collectData(serverInfo);
            } catch (IOException ex) {
                ex.printStackTrace();

                sender.sendMessage(PREFIX + "§c서버 정보를 수집하는 도중 오류가 발생했습니다.");
            }

            try {
                File serverProperties = new File(plugin.getDataFolder(), "../../server.properties");
                FileUtils.copyFile(serverProperties, new File(reportFolder, "server.properties"));
            } catch (IOException ex) {
                ex.printStackTrace();

                sender.sendMessage(PREFIX + "§c서버 설정을 수집하는 도중 오류가 발생했습니다.");
            }

            try {
                File latestLog = new File(plugin.getDataFolder(), "../../logs/latest.log");
                FileUtils.copyFile(latestLog, new File(reportFolder, "latest.log"));
            } catch (IOException ex) {
                ex.printStackTrace();

                sender.sendMessage(PREFIX + "§c서버 로그를 수집하는 도중 오류가 발생했습니다.");
            }

            try {
                File pluginInfo = new File(reportFolder, "pluginInfo.txt");

                PluginDataCollector.collectData(pluginInfo);
            } catch (IOException ex) {
                ex.printStackTrace();

                sender.sendMessage(PREFIX + "§c플러그인 정보를 수집하는 도중 오류가 발생했습니다.");
            }

            try {
                File luckPermsInfo = new File(reportFolder, "luckPermsInfo.txt");

                LuckPermsSettingCollector.collectData(luckPermsInfo);
            } catch (IOException ex) {
                ex.printStackTrace();

                sender.sendMessage(PREFIX + "§cLuckPerms 정보를 수집하는 도중 오류가 발생했습니다.");
            }

            try {
                StarlyPluginConfigCollector.collectData(reportFolder);
            } catch (IOException ex) {
                ex.printStackTrace();

                sender.sendMessage(PREFIX + "§c스탈리 플러그인 콘피그 정보를 수집하는 도중 오류가 발생했습니다.");
            }

            File resultFile;
            try {
                resultFile = new File(plugin.getDataFolder(), System.currentTimeMillis() + ".stdmp");
                try (
                        FileOutputStream fos = new FileOutputStream(resultFile);
                        ZipOutputStream zipOut = new ZipOutputStream(fos)
                ) {
                    for (File file : reportFolder.listFiles()) {
                        try (FileInputStream fis = new FileInputStream(file)) {
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zipOut.putNextEntry(zipEntry);

                            byte[] bytes = new byte[1024];
                            int length;
                            while ((length = fis.read(bytes)) >= 0) {
                                zipOut.write(bytes, 0, length);
                            }
                        }
                    }
                }

                FileUtils.deleteDirectory(reportFolder);
            } catch (IOException ex) {
                ex.printStackTrace();

                sender.sendMessage(PREFIX + "§c보고서를 마무리하는 도중 오류가 발생했습니다.");
                return;
            }

            sender.sendMessage(PREFIX);
            sender.sendMessage(PREFIX + "§e❕ §a보고서 생성이 완료되었습니다.");
            sender.sendMessage(PREFIX + "§e❕ §a경로§f: " + resultFile.getPath());
            sender.sendMessage(PREFIX + "§e❕ §c주의: 해당 보고서에는 다음과 같은 내용이 포함됩니다.");
            sender.sendMessage(PREFIX + "§e❕ §c[§e서버 정보§7, §e서버 속성§7, §e최신 로그§7, §e플러그인 목록/정보§7, §eLuckPerms 설정§7, §e스탈리 플러그인의 DataFolder§c]");
            sender.sendMessage(PREFIX + "§e❕ §c해당 보고서 파일은 중요한 정보를 포함하고 있습니다.");
            sender.sendMessage(PREFIX + "§e❕ §c파일 제공을 요청 받으셨더라도, 제공하지 않으실 수 있습니다.");
            sender.sendMessage(PREFIX);
        });

        thread.setName("STAsyncThread-" + System.currentTimeMillis());
        thread.setDaemon(true);
        thread.setPriority(5);
        thread.setUncaughtExceptionHandler((th, ex) -> {
            ex.printStackTrace();
        });
        thread.start();

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}