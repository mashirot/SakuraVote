package ski.mashiro.sakuravote.storage;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ski.mashiro.sakuravote.arithmetic.Arithmetic;
import ski.mashiro.sakuravote.timer.Timer;
import ski.mashiro.sakuravote.votetype.ConditionVote;
import ski.mashiro.sakuravote.votetype.VoteTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.bukkit.ChatColor.*;
import static ski.mashiro.sakuravote.command.Command.*;

/**
 * @author MashiroT
 */
public class Data {

    public static final List<VoteTask> VOTE_TASKS = new ArrayList<>();
    public static final List<ConditionVote> CONDITIONAL_VOTE_TASKS = new ArrayList<>();
    public static final List<ConditionVote> RUNNING_CONDITIONAL_VOTE_TASKS = new ArrayList<>();
    public static final List<Player> ONLINE_PLAYER_NUM = new ArrayList<>();
    public static Plugin plugin;

    private Data() {}

    public static boolean addVote(String name, String id, String command, String releaseTime, String effectTime) {
        if (isInteger(id) && isInteger(effectTime) && verifyTimePatternCorrect(releaseTime)) {
            if (!VoteInFile.isFileExist(Integer.parseInt(id)) && !VoteInFile.isCondFileExist(Integer.parseInt(id))) {
                VoteTask task = new VoteTask();
                task.setTaskId(Integer.parseInt(id));
                task.setTaskName(name);
                task.setCommand(replaceCommand(command));
                task.setReleaseTime(releaseTime);
                task.setEffectTime(Integer.parseInt(effectTime));
                if (VoteInFile.createVoteFile(task)) {
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

    public static boolean addConditionalVote(String name, String id, String command, String playerNum, String effectTime) {
        if (!VoteInFile.isCondFileExist(Integer.parseInt(id)) && !VoteInFile.isFileExist(Integer.parseInt(id))) {
            if (!VoteInFile.isCondFileExist(Integer.parseInt(id))) {
                ConditionVote task = new ConditionVote();
                task.setTaskId(Integer.parseInt(id));
                task.setTaskName(name);
                task.setCommand(replaceCommand(command));
                task.setStartPlayerNumber(Integer.parseInt(playerNum));
                task.setEffectTime(Integer.parseInt(effectTime));
                if (VoteInFile.createConditionalVoteFile(task)) {
                    CONDITIONAL_VOTE_TASKS.add(task);
                    return true;
                }
            }
        }
        return false;
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
            for (int i = 0; i < CONDITIONAL_VOTE_TASKS.size(); i++) {
                if (VoteInFile.isCondFileExist(delId)) {
                    if (delId == CONDITIONAL_VOTE_TASKS.get(i).getTaskId()) {
                        if (CONDITIONAL_VOTE_TASKS.get(i).isStart()) {
                            Timer.cancelTask(CONDITIONAL_VOTE_TASKS.get(i).getTaskId() + "");
                            if (VoteInFile.deleteConditionalVoteFile(delId)) {
                                Timer.cancelTask(delId + "");
                                CONDITIONAL_VOTE_TASKS.remove(CONDITIONAL_VOTE_TASKS.get(i));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean approveVote(Player player, String taskId) {

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
            for (ConditionVote task : CONDITIONAL_VOTE_TASKS) {
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
            for (ConditionVote task : CONDITIONAL_VOTE_TASKS) {
                if (id == task.getTaskId()) {
                    if (task.isStart() && !task.votes.containsValue(player)) {
                        task.votes.put(1, player);
                        return true;
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

    public static boolean modifyConditionalVote(String id, String type, String newValue) {
        if (VoteInFile.modifyConditionalVoteFile(id, type, newValue)) {
            for (int i = 0; i < CONDITIONAL_VOTE_TASKS.size(); i++) {
                if (isInteger(id) && CONDITIONAL_VOTE_TASKS.get(i).getTaskId() == Integer.parseInt(id)) {
                    CONDITIONAL_VOTE_TASKS.remove(CONDITIONAL_VOTE_TASKS.get(i));

                    if (CONDITIONAL_VOTE_TASKS.get(i).isStart()) {
                        Bukkit.getScheduler().cancelTask(CONDITIONAL_VOTE_TASKS.get(i).getThreadId());
                    }
                    loadConditionalSpecifyVoteFromFile(id);
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
                    VoteTask newTask = addVoteToListFromFile(yamlVoteFile);
                    VOTE_TASKS.add(newTask);
                    if (newTask.isReuse()) {
                        Timer.isReuse(newTask);
                    }
                }
            }
        }
        File[] condVoteFiles = new File(plugin.getDataFolder() + "/VoteList/CondVoteList/").listFiles();
        if (condVoteFiles != null) {
            for (File voteFile : condVoteFiles) {
                YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                if (voteFile.getName().equals(yamlVoteFile.get("Name") + ".yml")) {
                    ConditionVote newCondTask = addCondVoteToListFromFile(yamlVoteFile);
                    CONDITIONAL_VOTE_TASKS.add(newCondTask);
                    if (newCondTask.isStartAfterReload()) {
                        RUNNING_CONDITIONAL_VOTE_TASKS.add(newCondTask);
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

    public static ConditionVote addCondVoteToListFromFile(YamlConfiguration yamlVoteFile) {
        ConditionVote newCondTask = new ConditionVote(yamlVoteFile.getString("Name"), yamlVoteFile.getInt("TaskID"), yamlVoteFile.getString("Command"),
                yamlVoteFile.getInt("playerNumber"), yamlVoteFile.getInt("effectTime"), yamlVoteFile.getBoolean("startAfterReload"));
        if (verifyReleaseTime(newCondTask) != -1) {
            Timer.checkTimeToRun(newCondTask);
        }
        return newCondTask;
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

    public static void loadConditionalSpecifyVoteFromFile(String id) {
        YamlConfiguration yamlVoteFile = VoteInFile.findConditionalSpecifyVoteFile(id);
        if (yamlVoteFile != null) {
            ConditionVote newCondTask = new ConditionVote(yamlVoteFile.getString("Name"), yamlVoteFile.getInt("TaskID"), yamlVoteFile.getString("Command"),
                    yamlVoteFile.getInt("playerNumber"), yamlVoteFile.getInt("effectTime"), yamlVoteFile.getBoolean("startAfterReload"));
            CONDITIONAL_VOTE_TASKS.add(newCondTask);
            if (newCondTask.isStartAfterReload()) {
                RUNNING_CONDITIONAL_VOTE_TASKS.add(newCondTask);
            }
        }
    }

    public static void reloadTaskAndConfig() {
        VOTE_TASKS.clear();
        CONDITIONAL_VOTE_TASKS.clear();
        RUNNING_CONDITIONAL_VOTE_TASKS.clear();
        ONLINE_PLAYER_NUM.clear();
        loadVoteTaskFromFile(plugin);
        ONLINE_PLAYER_NUM.addAll(plugin.getServer().getOnlinePlayers());
        plugin.reloadConfig();
    }

    public static boolean showList(String type, CommandSender commandSender) {
        switch (type.toLowerCase()) {
            case LIST_TYPE_GOING:
                if (commandSender.hasPermission(PERMISSION_ADMIN_ALL) || commandSender.hasPermission(PERMISSION_COMMON_LIST_GOING)) {
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
                    commandSender.sendMessage(DARK_GREEN + "======================================");
                    if (RUNNING_CONDITIONAL_VOTE_TASKS.size() != 0) {
                        for (ConditionVote conditionVote : RUNNING_CONDITIONAL_VOTE_TASKS) {
                            commandSender.sendMessage("投票id：" + conditionVote.getTaskId() + "  投票名：" + conditionVote.getTaskName() + "  执行指令：" + conditionVote.getCommand()
                                    + "  需要人数：" + conditionVote.getStartPlayerNumber() + "  投票时长：" + conditionVote.getEffectTime() + "秒" + "  是否自动启用：" + conditionVote.isStartAfterReload());
                        }
                    } else {
                        commandSender.sendMessage(GREEN + "[SakuraVote] " + GRAY + "暂无未开始的人数投票");
                    }
                } else {
                    commandSender.sendMessage(GREEN + "[SakuraVote] " + DARK_AQUA + "权限不足");
                }
                return true;

            case LIST_TYPE_ALL:
                if (commandSender.hasPermission(PERMISSION_ADMIN_ALL) || commandSender.hasPermission(PERMISSION_COMMON_LIST_ALL)) {
                    if (VOTE_TASKS.size() != 0) {
                        commandSender.sendMessage(DARK_GREEN + "==============SakuraVote==============");
                        for (VoteTask voteTask : VOTE_TASKS) {
                            commandSender.sendMessage("投票id：" + voteTask.getTaskId() + "  投票名：" + voteTask.getTaskName() + "  执行指令：" + voteTask.getCommand()
                                    + "  发布时间：" + voteTask.getReleaseTime() + "  投票时长：" + voteTask.getEffectTime() + "秒" + "  是否循环：" + voteTask.isReuse());
                        }
                    } else {
                        commandSender.sendMessage(GREEN + "[SakuraVote] " + GRAY + "暂无投票");
                    }
                    if (CONDITIONAL_VOTE_TASKS.size() != 0) {
                        commandSender.sendMessage(DARK_GREEN + "===============人数投票===============");
                        for (ConditionVote conditionVote : CONDITIONAL_VOTE_TASKS) {
                            commandSender.sendMessage("投票id：" + conditionVote.getTaskId() + "  投票名：" + conditionVote.getTaskName() + "  执行指令：" + conditionVote.getCommand()
                                    + "  需要人数：" + conditionVote.getStartPlayerNumber() + "  投票时长：" + conditionVote.getEffectTime() + "秒" + "  是否自动启用：" + conditionVote.isStartAfterReload());
                        }
                        commandSender.sendMessage(DARK_GREEN + "======================================");
                    } else {
                        commandSender.sendMessage(GREEN + "[SakuraVote] " + GRAY + "暂无未开始的人数投票");
                    }
                } else {
                    commandSender.sendMessage(GREEN + "[SakuraVote] " + DARK_AQUA + "权限不足");
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
                Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + AQUA + "投票通过");
                Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + BLUE + "将在5秒后执行任务：" + voteTask.getTaskName());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int time = 5;
                        while (time > 0) {
                            Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + GRAY + time--);
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
                Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + AQUA + "投票不通过");
                break;
            case -1:
            default:
                Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + AQUA + "投票人数小于在线人数的一半，本次结果无效");
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
        Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + GREEN + "赞成玩家：" + yesPlayer);
        Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + GREEN + "反对玩家：" + disPlayer);
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
