package kr.starly.astral.manager.command.sub;

import kr.starly.astral.manager.command.SubCommandExecutor;
import kr.starly.astral.manager.context.MessageContext;
import kr.starly.astral.manager.AstralManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UpdateCheckerExecutor implements SubCommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageContext.PREFIX + "§c실행타입을 지정해주세요.");
            return false;
        } else if (args.length != 1) {
            sender.sendMessage(MessageContext.PREFIX + "§c명령어가 올바르지 않습니다.");
            return false;
        }

        AstralManager plugin = AstralManager.getInstance();

        switch (args[0]) {
            case "활성화": {
                plugin.getConfig().set("enableVersionChecker", true);
                plugin.saveConfig();

                sender.sendMessage(MessageContext.PREFIX + "§a버전체커를 §6활성화 §a했습니다.");
                return true;
            }

            case "비활성화": {
                plugin.getConfig().set("enableVersionChecker", false);
                plugin.saveConfig();

                sender.sendMessage(MessageContext.PREFIX + "§a버전체커를 §6비활성화 §a했습니다.");
                return true;
            }

            default: {
                sender.sendMessage(MessageContext.PREFIX + "§c알 수 없는 실행 타입입니다.");
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