package ski.mashiro.sakuravote.storage;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ski.mashiro.sakuravote.arithmetic.Arithmetic;
import ski.mashiro.sakuravote.command.Command;
import ski.mashiro.sakuravote.timer.Timer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.bukkit.ChatColor.*;

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

            for (int i = 0; i < VOTE_TASKS.size(); i++) {
                if (VoteInFile.isFileExist(delId)) {
                    if (delId == VOTE_TASKS.get(i).getTaskId() && !VOTE_TASKS.get(i).isStart()) {
                        if (VoteInFile.deleteVoteFile(delId)) {
                            Timer.cancelTask(delId + "");
                            VOTE_TASKS.remove(VOTE_TASKS.get(i));
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
            for (int i = 0; i < VOTE_TASKS.size(); i++) {
                if (isInteger(id) && VOTE_TASKS.get(i).getTaskId() == Integer.parseInt(id)) {
                    VOTE_TASKS.remove(VOTE_TASKS.get(i));
                    loadSpecifyVoteFromFile(id);
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    public static void loadVoteTaskFromFile(Plugin plugin) {
        File[] voteFiles = new File(plugin.getDataFolder() + "/VoteList").listFiles();
        if (voteFiles != null) {
            for (File voteFile : voteFiles) {
                YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                if (voteFile.getName().equals(yamlVoteFile.get("Name") + ".yml")) {
                    if (VOTE_TASKS.size() != 0) {
                        for (int i = 0; i < VOTE_TASKS.size(); i++) {
                            boolean isLoad = VOTE_TASKS.get(i).getTaskId() == yamlVoteFile.getInt("TaskID");
                            if (!isLoad) {
                                VoteTask newTask = addVoteToListFromFile(yamlVoteFile);
                                VOTE_TASKS.add(newTask);
                                if (newTask.isReuse()) {
                                    Timer.isReuse(newTask);
                                }
                            }
                        }
                    }else {
                        VoteTask newTask = addVoteToListFromFile(yamlVoteFile);
                        VOTE_TASKS.add(newTask);
                        if (newTask.isReuse()) {
                            Timer.isReuse(newTask);
                        }
                    }
                }
            }
        }
    }

    public static VoteTask addVoteToListFromFile(YamlConfiguration yamlVoteFile) {
        VoteTask newTask = new VoteTask(yamlVoteFile.getString("Name"), yamlVoteFile.getInt("TaskID"), yamlVoteFile.getString("Command"),
                yamlVoteFile.getString("releaseTime"), yamlVoteFile.getInt("effectTime"), yamlVoteFile.getBoolean("reuse"));
        if (verifyReleaseTime(newTask) != -1) {
            Timer.checkTimeToRun(newTask);
        }
        return newTask;
    }

    public static void loadSpecifyVoteFromFile(String id) {
        YamlConfiguration yamlVoteFile = VoteInFile.findSpecifyVoteFile(id);
        if (yamlVoteFile != null) {
            VoteTask newTask = new VoteTask(yamlVoteFile.getString("Name"), yamlVoteFile.getInt("TaskID"), yamlVoteFile.getString("Command"),
                    yamlVoteFile.getString("releaseTime"), yamlVoteFile.getInt("effectTime"), yamlVoteFile.getBoolean("reuse"));
            VOTE_TASKS.add(newTask);
            if (verifyReleaseTime(newTask) != -1) {
                Timer.checkTimeToRun(newTask);
            }
            if (newTask.isReuse()) {
                Timer.isReuse(newTask);
            }
        }
    }

    public static void reloadTaskAndConfig() {
        VOTE_TASKS.clear();
        loadVoteTaskFromFile(plugin);
        plugin.reloadConfig();
        plugin.getConfig();
    }

    public static boolean showList(String type, CommandSender commandSender) {
        switch (type.toLowerCase()) {
            case Command.LIST_TYPE_GOING:
                if (VOTE_TASKS.size() != 0) {
                    boolean hasStartTask = false;
                    for (VoteTask voteTask : VOTE_TASKS) {
                        if (!voteTask.isStart() && voteTask.getThreadId() != 0) {
                            hasStartTask = true;
                            commandSender.sendMessage("投票id：" + voteTask.getTaskId() + "  投票名：" + voteTask.getTaskName() + "  执行指令：" + voteTask.getCommand()
                                    + "  发布时间：" + voteTask.getReleaseTime() + "  投票时长：" + voteTask.getEffectTime() + "秒" + "  是否循环：" + voteTask.isReuse());
                        }
                    }
                    if (!hasStartTask) {
                        commandSender.sendMessage(GREEN + "[SakuraVote] " + GRAY + "暂无未开始的投票");
                    }
                } else {
                    commandSender.sendMessage(GREEN + "[SakuraVote] " + GRAY + "暂无未开始的投票");
                }
                return true;
            case Command.LIST_TYPE_ALL:
                if (VOTE_TASKS.size() != 0) {
                    commandSender.sendMessage(DARK_GREEN + "==============SakuraVote==============");
                    for (VoteTask voteTask : VOTE_TASKS) {
                        commandSender.sendMessage("投票id：" + voteTask.getTaskId() + "  投票名：" + voteTask.getTaskName() + "  执行指令：" + voteTask.getCommand()
                                + "  发布时间：" + voteTask.getReleaseTime() + "  投票时长：" + voteTask.getEffectTime() + "秒" + "  是否循环：" + voteTask.isReuse());
                    }
                    commandSender.sendMessage(DARK_GREEN + "======================================");
                } else {
                    commandSender.sendMessage(GREEN + "[SakuraVote] " + GRAY + "暂无投票");
                }
                return true;
            default:
                return false;
        }
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
