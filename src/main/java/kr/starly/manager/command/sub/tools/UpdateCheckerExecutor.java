package kr.starly.manager.command.sub.tools;

import com.google.gson.JsonElement;
import kr.starly.manager.AstralManager;
import kr.starly.manager.command.SubCommandExecutor;
import kr.starly.manager.util.AstralPluginUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static kr.starly.manager.context.MessageContext.PREFIX;

public class UpdateCheckerExecutor implements SubCommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(PREFIX + "§c실행타입을 지정해주세요.");
            return false;
        } else if (args.length != 1) {
            sender.sendMessage(PREFIX + "§c명령어가 올바르지 않습니다.");
            return false;
        }

        AstralManager plugin = AstralManager.getInstance();

        switch (args[0]) {
            case "활성화": {
                plugin.getConfig().set("enableVersionChecker", true);
                plugin.saveConfig();

                sender.sendMessage(PREFIX + "§a버전체커를 §6활성화 §a했습니다.");
                return true;
            }

            case "비활성화": {
                plugin.getConfig().set("enableVersionChecker", false);
                plugin.saveConfig();

                sender.sendMessage(PREFIX + "§a버전체커를 §6비활성화 §a했습니다.");
                return true;
            }

            default: {
                sender.sendMessage(PREFIX + "§c알 수 없는 실행 타입입니다.");
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("활성화", "비활성화", "즉시");
        } else {
            return Collections.emptyList();
        }
    }
}