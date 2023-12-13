package kr.starly.astral.manager.command.sub;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.starly.astral.manager.AstralManager;
import kr.starly.astral.manager.command.SubCommandExecutor;
import kr.starly.astral.manager.context.MessageContext;
import kr.starly.astral.manager.util.AstralPluginUtil;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PluginListExecutor implements SubCommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        PluginManager pluginManager = AstralManager.getInstance().getServer().getPluginManager();

        Map<String, JsonElement> pluginDatas;
        try {
            pluginDatas = AstralPluginUtil.fetchInstalledPluginList();
        } catch (IOException ex) {
            ex.printStackTrace();

            sender.sendMessage(MessageContext.PREFIX + "§c플러그인 목록을 불러오는데 실패했습니다.");
            return false;
        }

        List<String> pluginNames = new ArrayList<>();
        for (JsonElement value : pluginDatas.values()) {
            JsonObject pluginData = value.getAsJsonObject();
            String pluginName = pluginData.get("ENName").getAsString();

            pluginNames.add(pluginName);
        }

        Map<String, Plugin> plugins = new HashMap<>();
        pluginNames.forEach(pluginName -> plugins.put(pluginName, pluginManager.getPlugin(pluginName)));

        pluginNames.sort(Comparator.naturalOrder());
        pluginNames.sort(Comparator.comparing(str -> plugins.get((String) str).isEnabled() ? 1 : -1).reversed());

        // 메시지 전송
        TextComponent pluginComponent = new TextComponent(MessageContext.PREFIX + " §e전체 플러그인§f: ");
        for (int index = 0; index < pluginNames.size(); index++) {
            String pluginName = pluginNames.get(index);
            JsonObject pluginData = pluginDatas.get(pluginName).getAsJsonObject();

            Plugin plugin = plugins.get(pluginName);
            String dependencyString = pluginData.get("dependency").getAsJsonArray()
                    .asList()
                    .stream()
                    .map(JsonElement::getAsString)
                    .collect(Collectors.joining(", "));

            TextComponent pluginInfo = new TextComponent((plugins.get(pluginName).isEnabled() ? "§a" : "§c") + pluginName);
            pluginInfo.setHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new TextComponent[] {
                                    new TextComponent("플러그인명 (국문): " + pluginData.get("KRName").getAsString()),
                                    new TextComponent("\n플러그인명 (영문): " + pluginData.get("ENName").getAsString()),
                                    new TextComponent("\n현재버전: " + plugin.getDescription().getVersion()),
                                    new TextComponent("\n최신버전: " + pluginData.get("version").getAsString()),
                                    new TextComponent("\n의존성: " + (dependencyString.isEmpty() ? "없음" : dependencyString))
                            }
                    )
            );

            pluginComponent.addExtra(pluginInfo);

            if (index != pluginNames.size() - 1) {
                pluginComponent.addExtra("§7, ");
            }
        }

        sender.sendMessage(MessageContext.PREFIX);
        sender.spigot().sendMessage(pluginComponent);
        sender.sendMessage(MessageContext.PREFIX);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}