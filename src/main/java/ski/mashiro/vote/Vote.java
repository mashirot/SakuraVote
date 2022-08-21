package ski.mashiro.vote;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ski.mashiro.vote.command.Command;
import ski.mashiro.vote.storage.CreateEg;
import ski.mashiro.vote.storage.Data;

/**
 * @author MashiroT
 */
public class Vote extends JavaPlugin {

    @Override
    public void onLoad() {
        getLogger().info("Vote投票插件加载中");
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("vote").setExecutor(new Command());
        this.saveDefaultConfig();
        CreateEg.isFolderExist(this);
        Data.loadVoteTaskFromFile(this);
        Data.plugin = this;
        getLogger().info("Vote投票插件启动成功");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        getLogger().info("Vote投票插件已卸载");
    }
}
