package ski.mashiro.vote.storage;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MashiroT
 */
public class Data {

    public static List<VoteTask> voteTasks = new ArrayList<>();
    public static Plugin plugin;

    public static boolean addVote(String name, String id, String command, String releaseTime, String effectTime){
        VoteTask task = new VoteTask();

        if (task.setTaskIdOut(id)) {
            task.setTaskName(name);
            task.setCommand(command);
            if (task.verifyTimePatternCorrect(releaseTime)) {
                task.setReleaseTime(releaseTime);
                if (task.setEffectTimeOut(effectTime)) {
                    if (storeVoteTasks(plugin, task)) {
                        voteTasks.add(task);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean storeVoteTasks(Plugin plugin, VoteTask voteTask){
        return StoreVoteInFile.createVoteFile(plugin, voteTask);
    }

    public static boolean delVote(String id){
        try {
            int delId = Integer.parseInt(id);
            for (VoteTask task : voteTasks) {
                if (delId == task.getTaskId()) {
                    voteTasks.remove(task);
                    return true;
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
                    if (task.isFlag() && !task.votes.containsValue(player)) {
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
    public static boolean disApproveVote(Player player, String taskId){
        try {
            int id = Integer.parseInt(taskId);
            for (VoteTask task : voteTasks) {
                if (id == task.getTaskId()) {
                    if (task.isFlag()) {
                        if (task.isFlag() && !task.votes.containsValue(player)) {
                            task.votes.put(0, player);
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

    public static boolean modifyVote(String id, String type, String newValue){
        return StoreVoteInFile.modifyVoteFile(id ,type, newValue);
    }
}
