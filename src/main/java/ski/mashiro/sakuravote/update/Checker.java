package ski.mashiro.sakuravote.update;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author FeczIne
 */
public class Checker {

    private Checker() {}
    private static URL updateUrl = null;
    private static final String UPDATE_SWITCH = "check-update";

    public static void checkUpdate(Plugin plugin) {

        if (!plugin.getConfig().getBoolean(UPDATE_SWITCH)) {
            return;
        }

        Bukkit.getConsoleSender().sendMessage("[SakuraVote]检查更新中...");
        try {
            updateUrl = new URL("https://update.check.mashiro.ski/SakuraVoteVersion.txt");
        }catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("[SakuraVote]无法连接到更新服务器");
        }
        new BukkitRunnable() {
            String latestVersion;
            @Override
            public void run() {
                try (
                    BufferedReader br = new BufferedReader(new InputStreamReader(updateUrl.openStream(), StandardCharsets.UTF_8))
                ) {
                    latestVersion = br.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (plugin.getDescription().getVersion().equals(latestVersion)) {
                    Bukkit.getConsoleSender().sendMessage("[SakuraVote]当前为最新版本，感谢您的使用");
                }else {
                    Bukkit.getConsoleSender().sendMessage("[SakuraVote]当前有更新可用，请前往MCBBS发布贴下载");
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
