package ski.mashiro.vote.timer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ski.mashiro.vote.storage.Data;
import ski.mashiro.vote.storage.VoteTask;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author MashiroT
 */
public class Timer {

    private static long time;

    private Timer() {
    }

    public static long transformTime(String stringDate){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        try {
            Date date = sdf.parse(stringDate);
            time = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long verifyReleaseTime(VoteTask voteTask){
        long releaseTime = transformTime(voteTask.getReleaseTime()) - System.currentTimeMillis();
        return releaseTime > 0 ? -1 : releaseTime;
    }
    public static void checkTimeToRun(Plugin plugin, VoteTask voteTask){
        if (verifyReleaseTime(voteTask) != -1) {
            new BukkitRunnable(){
                @Override
                public void run() {
                    Bukkit.broadcastMessage("即将开始投票");
                    Bukkit.broadcastMessage("投票名：" + voteTask.getTaskName());
                    Bukkit.broadcastMessage("投票ID：" + voteTask.getTaskId());
                    Bukkit.broadcastMessage("投票时间：" + voteTask.getEffectTime());
                    Bukkit.broadcastMessage("输入/vote [approve/disapprove]" + voteTask.getTaskId() + "进行支持或反对");
                    voteTask.setFlag();
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            Data.calcResult(voteTask);
                            cancel();
                        }
                    }.runTaskLaterAsynchronously(plugin, transformTime(voteTask.getEffectTime()) * 20);
                }
            }.runTaskLaterAsynchronously(plugin, verifyReleaseTime(voteTask) / 1000 * 20);
        }

    }

}
