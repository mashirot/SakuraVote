package ski.mashiro.vote.storage;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MashiroT
 */
public class Data {

    public static List<VoteTask> voteTasks = new ArrayList<>();

    public static boolean addVote(String name, String id, String command, String releaseTime, String effectTime){
        VoteTask task = new VoteTask();

        if (task.setTaskIdOut(id)) {
            task.setTaskName(name);
            task.setCommand(command);
            if (task.verifyTimePatternCorrect(releaseTime) && task.verifyTimePatternCorrect(effectTime)) {
                task.setReleaseTime(releaseTime);
                task.setEffectTime(effectTime);
            }else {
                return false;
            }
            voteTasks.add(task);
            return true;
        }
        return false;
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
}
