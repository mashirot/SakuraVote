package ski.mashiro.sakuravote;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ski.mashiro.sakuravote.bstats.Metrics;
import ski.mashiro.sakuravote.command.Command;
import ski.mashiro.sakuravote.listener.Listener;
import ski.mashiro.sakuravote.storage.CreateEg;
import ski.mashiro.sakuravote.storage.Data;
import ski.mashiro.sakuravote.update.Checker;


/**
 * @author MashiroT
 */
public class SakuraVote extends JavaPlugin {

    @Override
    public void onLoad() {
        getLogger().info("SakuraVote投票插件加载中");
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("vote").setExecutor(new Command());
        Bukkit.getPluginCommand("vote").setTabCompleter(new Command());
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        Data.plugin = this;
        this.saveDefaultConfig();
        CreateEg.isFolderExist(this);
        Data.loadVoteTaskFromFile(this);
        Checker.checkUpdate(this);
        new Metrics(this, 16242);
        getLogger().info("SakuraVote投票插件启动成功");
    }

    @Override
    public void onDisable() {
        saveConfig();
        Bukkit.getScheduler().cancelTasks(this);
        getLogger().info("SakuraVote投票插件已卸载");
    }
}
