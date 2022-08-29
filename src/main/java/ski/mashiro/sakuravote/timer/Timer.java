package ski.mashiro.sakuravote.timer;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ski.mashiro.sakuravote.message.PluginMessage;
import ski.mashiro.sakuravote.storage.Data;
import ski.mashiro.sakuravote.storage.VoteInFile;
import ski.mashiro.sakuravote.votetype.ConditionVote;
import ski.mashiro.sakuravote.votetype.VoteTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.bukkit.ChatColor.*;
import static ski.mashiro.sakuravote.storage.Data.*;

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
                    PluginMessage.startVoteMessage(voteTask);
                    voteTask.changeVoteState();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + YELLOW + "投票结束");
                            voteTask.changeVoteState();
                            Data.calcResult(voteTask);
                            Bukkit.getScheduler().cancelTask(voteTask.getTaskId());
                            voteTask.setThreadId(0);
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

    public static void startCondVote(ConditionVote conditionVote) {
        BukkitTask delay = new BukkitRunnable() {
            @Override
            public void run() {
                PluginMessage.startVoteMessage(conditionVote);
                conditionVote.changeVoteState();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + YELLOW + "投票结束");
                        conditionVote.changeVoteState();
                        calcResult(conditionVote);
                        Bukkit.getScheduler().cancelTask(conditionVote.getTaskId());
                        conditionVote.setThreadId(0);
                        cancel();
                    }
                }.runTaskLaterAsynchronously(plugin, conditionVote.getEffectTime() * 20L);
            }
        }.runTaskAsynchronously(Data.plugin);
        conditionVote.setThreadId(delay.getTaskId());
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
            for (ConditionVote voteTask : CONDITIONAL_VOTE_TASKS) {
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss");
            Date oldDate = sdf.parse(voteTask.getReleaseTime());

            Calendar settingTime = Calendar.getInstance();
            settingTime.setTime(oldDate);
            Calendar newTime = Calendar.getInstance();

            if (settingTime.getTime().getTime() < newTime.getTime().getTime()) {
                newTime.set(Calendar.HOUR_OF_DAY, settingTime.get(Calendar.HOUR_OF_DAY));
                newTime.set(Calendar.MINUTE, settingTime.get(Calendar.MINUTE));
                newTime.set(Calendar.SECOND, settingTime.get(Calendar.SECOND));
                if (newTime.getTime().getTime() < System.currentTimeMillis()) {
                    newTime.set(Calendar.DATE, settingTime.get(Calendar.DATE) + 1);
                }
                voteTask.setReleaseTime(sdf.format(newTime.getTime()));
                VoteInFile.modifyReleaseTime(voteTask);
                Data.VOTE_TASKS.remove(voteTask);
                VOTE_TASKS.add(voteTask);
                checkTimeToRun(voteTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkPlayerNum() {
        for (int i = 0; i < RUNNING_CONDITIONAL_VOTE_TASKS.size(); i++) {
            if (ONLINE_PLAYER_NUM.size() >= RUNNING_CONDITIONAL_VOTE_TASKS.get(i).getStartPlayerNumber()) {
                startCondVote(RUNNING_CONDITIONAL_VOTE_TASKS.get(i));
                RUNNING_CONDITIONAL_VOTE_TASKS.remove(RUNNING_CONDITIONAL_VOTE_TASKS.get(i));
            }
        }
    }

}
