package ski.mashiro.sakuravote.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ski.mashiro.sakuravote.storage.Data;
import ski.mashiro.sakuravote.timer.Timer;

/**
 * @author FeczIne
 */
public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {
        Data.ONLINE_PLAYER_NUM.add(e.getPlayer());
        Timer.checkPlayerNum();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Data.ONLINE_PLAYER_NUM.remove(e.getPlayer());
    }

}
