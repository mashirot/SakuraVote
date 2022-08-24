package ski.mashiro.sakuravote.timer;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ski.mashiro.sakuravote.storage.Data;
import ski.mashiro.sakuravote.storage.VoteInFile;
import ski.mashiro.sakuravote.storage.VoteTask;

import java.text.SimpleDateFormat;

import static ski.mashiro.sakuravote.storage.Data.isInteger;
import static ski.mashiro.sakuravote.storage.Data.verifyReleaseTime;

/**
 * @author MashiroT
 */
public class Timer {

    private Timer() {}

    public static void checkTimeToRun(VoteTask voteTask) {
        long releaseTime = verifyReleaseTime(voteTask);
        if (releaseTime != -1) {
            BukkitTask delay = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage("[SakuraVote] 即将开始投票");
                    Bukkit.broadcastMessage("[SakuraVote] 投票名：" + voteTask.getTaskName());
                    Bukkit.broadcastMessage("[SakuraVote] 投票ID：" + voteTask.getTaskId());
                    Bukkit.broadcastMessage("[SakuraVote] 投票时间：" + voteTask.getEffectTime() + "秒");
                    Bukkit.broadcastMessage("[SakuraVote] 输入/vote [approve/disapprove] " + voteTask.getTaskId() + " 进行支持或反对");
                    voteTask.changeVoteState();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.broadcastMessage("[SakuraVote] 投票结束");
                            voteTask.changeVoteState();
                            Data.calcResult(voteTask);
                            Bukkit.getScheduler().cancelTask(voteTask.getTaskId());
                            voteTask.setTaskId(0);
                            if (voteTask.isReuse()) {
                                isReuse(voteTask);
                            }
                            cancel();
                        }
                    }.runTaskLaterAsynchronously(Data.plugin, voteTask.getEffectTime() * 20L);
                }
            }.runTaskLaterAsynchronously(Data.plugin, releaseTime / 1000 * 20);
            voteTask.setThreadId(delay.getTaskId());
        }
    }

    public static boolean cancelTask(String cancelId) {
        if (isInteger(cancelId)) {
            int id = Integer.parseInt(cancelId);
            for (VoteTask voteTask : Data.VOTE_TASKS) {
                if (voteTask.getTaskId() == id) {
                    if (voteTask.getThreadId() != 0) {
                        Bukkit.getScheduler().cancelTask(voteTask.getThreadId());
                        voteTask.setThreadId(0);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void isReuse(VoteTask voteTask) {
        try {
            Data.VOTE_TASKS.remove(voteTask);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss");
            long longDate = sdf.parse(voteTask.getReleaseTime()).getTime();
            if (longDate - System.currentTimeMillis() < 0) {
                longDate += (86400 * 1000);
                voteTask.setReleaseTime(sdf.format(longDate));
                VoteInFile.modifyReuseTime(voteTask);
                Data.loadVoteTaskFromFile(Data.plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
