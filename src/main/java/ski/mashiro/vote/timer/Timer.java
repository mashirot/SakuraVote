package ski.mashiro.vote.timer;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import ski.mashiro.vote.storage.Data;
import ski.mashiro.vote.storage.VoteTask;

import static ski.mashiro.vote.storage.Data.verifyReleaseTime;

/**
 * @author MashiroT
 */
public class Timer {

    private Timer() {}

    public static void checkTimeToRun(VoteTask voteTask) {
        if (verifyReleaseTime(voteTask) != -1) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    int taskId = getTaskId();
                    Bukkit.broadcastMessage("即将开始投票");
                    Bukkit.broadcastMessage("投票名：" + voteTask.getTaskName());
                    Bukkit.broadcastMessage("投票ID：" + voteTask.getTaskId());
                    Bukkit.broadcastMessage("投票时间：" + voteTask.getEffectTime() + "秒");
                    Bukkit.broadcastMessage("输入/vote [approve/disapprove]" + voteTask.getTaskId() + "进行支持或反对");
                    voteTask.changeVoteState();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            voteTask.changeVoteState();
                            Data.calcResult(voteTask);
                            Bukkit.getScheduler().cancelTask(taskId);
                            cancel();
                        }
                    }.runTaskLaterAsynchronously(Data.plugin, voteTask.getEffectTime() * 20L);
                    while (voteTask.isCancel()) {
                        voteTask.setCancel(false);
                        Bukkit.getScheduler().cancelTask(taskId);
                    }
                }
            }.runTaskLaterAsynchronously(Data.plugin, verifyReleaseTime(voteTask) / 1000 * 20);
        }
    }

    public static boolean cancelTask(String cancelId) {
        try {
            int id = Integer.parseInt(cancelId);
            for (VoteTask voteTask : Data.voteTasks) {
                if (voteTask.isStart()) {
                    if (voteTask.getTaskId() == id) {
                        voteTask.setCancel(true);
                        return true;
                    }
                }
            }
        }catch (Exception e) {
            return false;
        }
        return false;
    }
}
