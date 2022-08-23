package ski.mashiro.sakuravote.storage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ski.mashiro.sakuravote.arithmetic.Arithmetic;
import ski.mashiro.sakuravote.timer.Timer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author MashiroT
 */
public class Data {

    public static final List<VoteTask> VOTE_TASKS = new ArrayList<>();
    public static Plugin plugin;

    private Data() {}

    public static boolean addVote(String name, String id, String command, String releaseTime, String effectTime) {
        if (isInteger(id) && isInteger(effectTime) && verifyTimePatternCorrect(releaseTime)) {
            if (!VoteInFile.isFileExist(Integer.parseInt(id))) {
                VoteTask task = new VoteTask();
                task.setTaskId(Integer.parseInt(id));
                task.setTaskName(name);
                task.setCommand(replaceCommand(command));
                task.setReleaseTime(releaseTime);
                task.setEffectTime(Integer.parseInt(effectTime));
                if (storeVoteTasks(task)) {
                    VOTE_TASKS.add(task);
                    if (verifyReleaseTime(task) != -1) {
                        Timer.checkTimeToRun(task);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean storeVoteTasks(VoteTask voteTask) {
        return VoteInFile.createVoteFile(voteTask);
    }

    public static boolean delVote(String id) {
        if (isInteger(id)) {
            int delId = Integer.parseInt(id);
            for (VoteTask task : VOTE_TASKS) {
                if (VoteInFile.isFileExist(delId)) {
                    if (delId == task.getTaskId() && !task.isStart()) {
                        if (VoteInFile.deleteVoteFile(delId)) {
                            Timer.cancelTask(delId + "");
                            VOTE_TASKS.iterator().remove();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean approveVote(Player player, String taskId){

        try {
            int id = Integer.parseInt(taskId);
            for (VoteTask task : VOTE_TASKS) {
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
            for (VoteTask task : VOTE_TASKS) {
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
        if (VoteInFile.modifyVoteFile(id, type, newValue)) {
            for (VoteTask inListTask : VOTE_TASKS) {
                if (isInteger(id) && inListTask.getTaskId() == Integer.parseInt(id)) {
                    VOTE_TASKS.iterator().remove();
                    loadVoteTaskFromFile(plugin);
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    public static void loadVoteTaskFromFile(Plugin plugin){
        File[] voteFiles = new File(plugin.getDataFolder() + "/VoteList").listFiles();
        if (voteFiles != null) {
            for (File voteFile : voteFiles) {
                YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                if (voteFile.getName().equals(yamlVoteFile.get("Name") + ".yml")) {
                    if (VOTE_TASKS.size() != 0) {
                        for (int i = 0; i < VOTE_TASKS.size(); i++) {
                            boolean isLoad = VOTE_TASKS.get(i).getTaskId() == yamlVoteFile.getInt("TaskID");
                            if (!isLoad) {
                                addVoteToListFromFile(yamlVoteFile);
                            }
                        }
                    }else {
                        addVoteToListFromFile(yamlVoteFile);
                    }
                }
            }
        }
    }

    public static void addVoteToListFromFile(YamlConfiguration yamlVoteFile) {
        VoteTask newTask = new VoteTask(yamlVoteFile.getString("Name"), yamlVoteFile.getInt("TaskID"), yamlVoteFile.getString("Command"),
                yamlVoteFile.getString("releaseTime"), yamlVoteFile.getInt("effectTime"), yamlVoteFile.getBoolean("reuse"));
        VOTE_TASKS.add(newTask);
        if (verifyReleaseTime(newTask) != -1) {
            Timer.checkTimeToRun(newTask);
        }
    }

    public static void reloadTaskAndConfig() {
        VOTE_TASKS.clear();
        loadVoteTaskFromFile(plugin);
        plugin.reloadConfig();
    }

    public static String replaceCommand(String inputCommand) {
        return inputCommand.replace("_", " ");
    }

    public static void calcResult(VoteTask voteTask) {
        switch (Arithmetic.result(voteTask.votes)) {
            case 1:
                Bukkit.broadcastMessage("[SakuraVote]投票通过");
                Bukkit.broadcastMessage("[SakuraVote]将在5秒后执行任务：" + voteTask.getTaskName());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int time = 5;
                        while (time >= 0) {
                            Bukkit.broadcastMessage(time-- + "");
                            try {
                                Thread.sleep(1000);
                            } catch (Exception ignore) {}
                        }
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), voteTask.getCommand());
                        cancel();
                    }
                }.runTaskAsynchronously(plugin);
                break;
            case 0:
                Bukkit.broadcastMessage("[SakuraVote]投票不通过");
                break;
            case -1:
            default:
                Bukkit.broadcastMessage("[SakuraVote]投票人数小于在线人数的一半，本次结果无效");
                break;
        }
    }

    public static void showResult(List<Player> approvePlayers, List<Player> disApprovePlayers) {
        StringBuilder yesPlayer = new StringBuilder();
        StringBuilder disPlayer = new StringBuilder();
        for (int i = 0; i < approvePlayers.size(); i++) {
            yesPlayer.append(approvePlayers.get(i).getName()).append(approvePlayers.size() - 1 == i ? "" : "，");
        }
        for (int i = 0; i < disApprovePlayers.size(); i++) {
            disPlayer.append(disApprovePlayers.get(i).getName()).append(disApprovePlayers.size() - 1 == i ? "" : "，");
        }
        Bukkit.broadcastMessage("[SakuraVote]赞成玩家：" + yesPlayer);
        Bukkit.broadcastMessage("[SakuraVote]反对玩家：" + disPlayer);
    }

    public static long transformTime(String stringDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss");
        long time;
        try {
            Date date = sdf.parse(stringDate);
            time = date.getTime();
        } catch (Exception e) {
            return -1;
        }
        return time;
    }

    public static long verifyReleaseTime(VoteTask voteTask) {
        long releaseTime = transformTime(voteTask.getReleaseTime()) - System.currentTimeMillis();
        return releaseTime > 0 ? releaseTime : -1;
    }

    public static boolean verifyTimePatternCorrect(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss");
        try {
            sdf.parse(stringDate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInteger(String stringInt) {
        try {
            Integer.parseInt(stringInt);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

}
