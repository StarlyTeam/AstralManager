package kr.starly.manager;

import kr.starly.manager.command.StarlyToolsCommand;
import kr.starly.manager.task.UpdateCheckerTask;
import lombok.Getter;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class AstralManager extends JavaPlugin {

    @Getter private static AstralManager instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        TabExecutor 스탈리도구Executor = new StarlyToolsCommand();
        getCommand("스탈리도구").setExecutor(스탈리도구Executor);
        getCommand("스탈리도구").setTabCompleter(스탈리도구Executor);

        UpdateCheckerTask.run(this);
    }
}