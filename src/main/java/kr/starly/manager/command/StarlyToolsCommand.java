package kr.starly.manager.command;

import kr.starly.manager.AstralManager;
import kr.starly.manager.command.sub.tools.ListExecutor;
import kr.starly.manager.command.sub.tools.ReportExecutor;
import kr.starly.manager.command.sub.tools.ErrorFixExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.*;

import static kr.starly.manager.context.MessageContext.PREFIX;

public class StarlyToolsCommand implements TabExecutor {

    private static Map<String, SubCommandExecutor> executors = new HashMap<>();

    static {
        executors.put("목록", new ListExecutor());
        executors.put("보고서", new ReportExecutor());
        executors.put("문제해결", new ErrorFixExecutor());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(PREFIX + "§dv" + AstralManager.getInstance().getDescription().getVersion());
            return true;
        }

        SubCommandExecutor executor = executors.get(args[0]);
        if (executor == null) {
            sender.sendMessage(PREFIX + "§c알 수 없는 명령어입니다.");
            return false;
        } else if (!sender.isOp()) {
            sender.sendMessage(PREFIX + "§c해당 명령어를 사용하실 수 없습니다.");
            return false;
        } else {
            try {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                return executor.onCommand(sender, cmd, label, newArgs);
            } catch (Exception ex) {
                ex.printStackTrace();

                sender.sendMessage(PREFIX + "§c명령어를 실행하는 도중 오류가 발생했습니다.");
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(executors.keySet());
        } else {
            SubCommandExecutor executor = executors.get(args[0]);
            if (executor == null) return Collections.emptyList();

            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            completions.addAll(executor.onTabComplete(sender, cmd, label, newArgs));
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}