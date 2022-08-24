package ski.mashiro.sakuravote.storage;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author MashiroT
 */
public class VoteTask {

    private String taskName;
    private int taskId;
    private String command;
    private String releaseTime;
    private int effectTime;
    private boolean isStart;
    private boolean isReuse;
    private int threadId = 0;

    public final HashMap<Integer, Player> votes = new HashMap<>();

    public VoteTask() {}

    public VoteTask(String taskName, int taskId, String command, String releaseTime, int effectTime, boolean isReuse) {
        this.taskName = taskName;
        this.taskId = taskId;
        this.command = command;
        this.releaseTime = releaseTime;
        this.effectTime = effectTime;
        this.isReuse = isReuse;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public boolean isReuse() {
        return isReuse;
    }

    public boolean isStart() {
        return isStart;
    }

    public void changeVoteState() {
        isStart = !isStart;
    }

    public int getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(int effectTime) {
        this.effectTime = effectTime;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        VoteTask voteTask = (VoteTask) o;
        return taskId == voteTask.taskId && Objects.equals(taskName, voteTask.taskName) && Objects.equals(command, voteTask.command)
                && Objects.equals(releaseTime, voteTask.releaseTime) && Objects.equals(effectTime, voteTask.effectTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskId, command, releaseTime, effectTime);
    }
}
