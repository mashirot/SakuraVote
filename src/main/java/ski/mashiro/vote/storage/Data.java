package ski.mashiro.vote.storage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ski.mashiro.vote.arithmetic.Arithmetic;
import ski.mashiro.vote.timer.Timer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author MashiroT
 */
public class Data {

    public static List<VoteTask> voteTasks = new ArrayList<>();
    public static Plugin plugin;

    private Data() {}

    public static boolean addVote(String name, String id, String command, String releaseTime, String effectTime) {
        VoteTask task = new VoteTask();
        if (task.setTaskIdOut(id)) {
            if (!VoteWithFile.isFileExist(Integer.parseInt(id))) {
                task.setTaskName(name);
                task.setCommand(command);
                if (verifyTimePatternCorrect(releaseTime)) {
                    task.setReleaseTime(releaseTime);
                    if (task.setEffectTimeOut(effectTime)) {
                        if (storeVoteTasks(task)) {
                            voteTasks.add(task);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean storeVoteTasks(VoteTask voteTask) {
        return VoteWithFile.createVoteFile(voteTask);
    }

    public static boolean delVote(String id){
        try {
            int delId = Integer.parseInt(id);
            for (VoteTask task : voteTasks) {
                if (VoteWithFile.isFileExist(delId)) {
                    if (delId == task.getTaskId() && !task.isStart()) {
                        if (VoteWithFile.deleteVoteFile(delId)) {
                            Timer.cancelTask(delId + "");
                            voteTasks.remove(task);
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean approveVote(Player player, String taskId){

        try {
            int id = Integer.parseInt(taskId);
            for (VoteTask task : voteTasks) {
                if (id == task.getTaskId()) {
                    if (task.isStart() && !task.votes.containsValue(player)) {
                        task.votes.put(0, player);
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean disApproveVote(Player player, String taskId) {
        try {
            int id = Integer.parseInt(taskId);
            for (VoteTask task : voteTasks) {
                if (id == task.getTaskId()) {
                    if (task.isStart()) {
                        if (task.isStart() && !task.votes.containsValue(player)) {
                            task.votes.put(1, player);
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean modifyVote(String id, String type, String newValue) {
        return VoteWithFile.modifyVoteFile(id ,type, newValue);
    }

    public static void loadVoteTaskFromFile(Plugin plugin){
        File[] voteFiles = new File(plugin.getDataFolder() + "/VoteList").listFiles();
        if (voteFiles != null) {
            for (File voteFile : voteFiles) {
                YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                for (VoteTask inListTask : voteTasks) {
                    boolean isLoad = inListTask.getTaskId() == yamlVoteFile.getInt("TaskID");
                    if (!isLoad) {
                        VoteTask newTask = new VoteTask(yamlVoteFile.getString("Name"), yamlVoteFile.getInt("TaskID"), yamlVoteFile.getString("Command"),
                                yamlVoteFile.getString("releaseTime"), yamlVoteFile.getInt("effectTime"), yamlVoteFile.getBoolean("reuse"));
                        if (verifyReleaseTime(newTask) != -1) {
                            voteTasks.add(newTask);
                            Timer.checkTimeToRun(newTask);
                        }
                    }
                }
            }
        }
    }

    public static void showResult(List<Player> approvePlayers, List<Player> disApprovePlayers) {

        StringBuilder yesPlayer = new StringBuilder();
        StringBuilder disPlayer = new StringBuilder();
        for (int i = 0; i < approvePlayers.size(); i++) {
            yesPlayer.append(approvePlayers.get(i).getName()).append(approvePlayers.size() - 1 == i ? "，" : "");
        }
        for (int i = 0; i < disApprovePlayers.size(); i++) {
            disPlayer.append(disApprovePlayers.get(i).getName()).append(disApprovePlayers.size() - 1 == i ? "，" : "");
        }

        Bukkit.broadcastMessage("赞成玩家：" + yesPlayer);
        Bukkit.broadcastMessage("反对玩家：" + disPlayer);
    }

    public static void calcResult(VoteTask voteTask) {
        switch (Arithmetic.result(voteTask.votes)) {
            case 1:
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), voteTask.getCommand());
                Bukkit.broadcastMessage("投票通过");
                break;
            case 0:
                Bukkit.broadcastMessage("投票不通过");
                break;
            case -1:
            default:
                Bukkit.broadcastMessage("投票人数小于在线人数的一半，本次结果无效");
                break;
        }
    }

    public static boolean modifyTaskReuse(String modifyId) {
        try {
            int id = Integer.parseInt(modifyId);
            for (VoteTask voteTask : voteTasks) {
                if (id == voteTask.getTaskId()) {
                    voteTask.reuse();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long transformTime(String stringDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time;
        try {
            Date date = sdf.parse(stringDate);
            time = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return time;
    }

    public static long verifyReleaseTime(VoteTask voteTask) {
        long releaseTime = transformTime(voteTask.getReleaseTime()) - System.currentTimeMillis();
        return releaseTime > 0 ? -1 : releaseTime;
    }

    public static boolean verifyTimePatternCorrect(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sdf.parse(stringDate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
