package kr.starly.manager.command;

import org.bukkit.command.*;

import java.io.IOException;
import java.util.List;

public interface SubCommandExecutor {

    boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws Exception;

    List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args);
}