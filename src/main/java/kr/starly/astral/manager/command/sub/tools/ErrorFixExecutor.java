package kr.starly.astral.manager.command.sub.tools;

import com.google.gson.JsonElement;
import kr.starly.astral.manager.context.MessageContext;
import kr.starly.astral.manager.AstralManager;
import kr.starly.astral.manager.command.SubCommandExecutor;
import kr.starly.astral.manager.util.AstralPluginUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ErrorFixExecutor implements SubCommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws IOException {
        if (args.length == 0) {
            sender.sendMessage(MessageContext.PREFIX + "§c오류가 발생한 플러그인을 입력해주세요.");
            return false;
        } else if (args.length != 1) {
            sender.sendMessage(MessageContext.PREFIX + "§c명령어가 올바르지 않습니다.");
            return false;
        }

        AstralManager plugin = AstralManager.getInstance();
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        String targetName = args[0];
        Plugin target = pluginManager.getPlugin(targetName);
        Map<String, JsonElement> targetData = AstralPluginUtil.fetchPlugin(targetName);

        // Step1: 정보 출력
        sender.sendMessage(MessageContext.PREFIX + "§7플러그인 활성: " + target.isEnabled());

        // Step2: 코어 확인
        Plugin corePlugin = pluginManager.getPlugin("StarlyCore");
        if (corePlugin == null || !corePlugin.isEnabled()) {
            findProblem(sender, "코어 플러그인이 감지되지 않았습니다. [코어가 정상적으로 동작중인지 점검해주세요.]");
            return true;
        }

        // Step3: 의존성 확인
        List<String> softDependencies = target.getDescription().getSoftDepend();
        List<String> dependencies = targetData.get("dependency").getAsJsonArray()
                .asList()
                .stream()
                .map(JsonElement::getAsString)
                .filter(name -> !softDependencies.contains(name))
                .collect(Collectors.toList());

        List<String> uninstalledDependencies = new ArrayList<>();
        for (String dependencyName : dependencies) {
            Plugin dependencyPlugin = pluginManager.getPlugin(dependencyName);
            if (dependencyPlugin != null && dependencyPlugin.isEnabled()) continue;

            uninstalledDependencies.add(dependencyName);
        }

        if (!uninstalledDependencies.isEmpty()) {
            findProblem(sender, "의존성 플러그인이 감지되지 않았습니다. [" + String.join(", ", uninstalledDependencies) + " 플러그인을 설치해주세요.]");
            return true;
        }

        // Step4: 추가 의존성 확인
        if (dependencies.contains("Vault")) {
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                findProblem(sender, "Vault 플러그인에 등록된 Economy가 감지되지 않았습니다. [EssentialsX와 같은 Economy 플러그인을 설치해주세요.]");
                return true;
            }
        }

        sender.sendMessage(MessageContext.PREFIX + "§eℹ §c오류를 찾지 못했습니다.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        Map<String, JsonElement> pluginDatas;
        try {
            pluginDatas = AstralPluginUtil.fetchInstalledPluginList();
        } catch (IOException ex) {
            ex.printStackTrace();

            return Collections.emptyList();
        }

        return new ArrayList<>(pluginDatas.keySet());
    }

    private void findProblem(CommandSender sender, String problem) {
        sender.sendMessage(MessageContext.PREFIX);
        sender.sendMessage(MessageContext.PREFIX + "§eℹ §f문제를 찾았습니다.");
        sender.sendMessage(MessageContext.PREFIX + "§eℹ §7" + problem);
        sender.sendMessage(MessageContext.PREFIX);
    }
}