package ski.mashiro.vote.timer;

import org.bukkit.scheduler.BukkitRunnable;
import ski.mashiro.vote.storage.VoteTask;

/**
 * @author MashiroT
 */
public class Timer extends BukkitRunnable {

    private final VoteTask voteTask;

    public Timer(VoteTask voteTask) {
        this.voteTask = voteTask;
    }

    @Override
    public void run() {


    }
}
