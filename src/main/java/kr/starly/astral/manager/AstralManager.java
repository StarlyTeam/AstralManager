package kr.starly.astral.manager;

import kr.starly.astral.manager.task.UpdateCheckTask;
import kr.starly.astral.manager.command.StarlyToolsCommand;
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
        TabExecutor StarlyToolsExecutor = new StarlyToolsCommand();
        getCommand("스탈리도구").setExecutor(StarlyToolsExecutor);
        getCommand("스탈리도구").setTabCompleter(StarlyToolsExecutor);

        UpdateCheckTask.run(this);
    }

    @Override
    public void onDisable() {
        UpdateCheckTask.stop();
    }
}