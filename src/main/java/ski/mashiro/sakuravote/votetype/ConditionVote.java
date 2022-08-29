package ski.mashiro.sakuravote.votetype;

/**
 * @author FeczIne
 */
public class ConditionVote extends VoteTask {

    private int startPlayerNumber;
    private boolean startAfterReload = true;

    public ConditionVote() {}

    public ConditionVote(String taskName, int taskId, String command, int startPlayerNumber, int effectTime, boolean startAfterReload) {
        super(taskName, taskId, command, effectTime);
        this.startPlayerNumber = startPlayerNumber;
        this.startAfterReload = startAfterReload;
    }

    public int getStartPlayerNumber() {
        return startPlayerNumber;
    }

    public void setStartPlayerNumber(int startPlayerNumber) {
        this.startPlayerNumber = startPlayerNumber;
    }

    public boolean isStartAfterReload() {
        return startAfterReload;
    }
}
