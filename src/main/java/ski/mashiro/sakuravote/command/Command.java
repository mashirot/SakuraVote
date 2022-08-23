package ski.mashiro.sakuravote.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ski.mashiro.sakuravote.message.PluginMessage;
import ski.mashiro.sakuravote.storage.Data;
import ski.mashiro.sakuravote.storage.VoteTask;
import ski.mashiro.sakuravote.timer.Timer;

/**
 * @author MashiroT
 */
public class Command implements CommandExecutor {

    private static final String CREATE = "create";
    private static final String DEL = "del";
    private static final String DELETE = "delete";
    private static final String APPROVE = "approve";
    private static final String DISAPPROVE = "disapprove";
    private static final String LIST = "list";
    private static final String SET = "set";
    private static final String CANCEL = "cancel";
    private static final String RELOAD = "reload";
    private static final int TASK_NAME = 1;
    private static final int TASK_ID = 2;
    private static final int TASK_COMMAND = 3;
    private static final int TASK_RELEASE_TIME = 4;
    private static final int TASK_EFFECT_TIME = 5;
    private static final int TASK_CREATE_CORRECT_LENGTH = 6;
    private static final int TASK_DELETE_CORRECT_LENGTH = 2;
    private static final int TASK_APPROVE_CORRECT_LENGTH = 2;
    private static final int TASK_DISAPPROVE_CORRECT_LENGTH = 2;
    private static final int TASK_MODIFY_ID = 1;
    private static final int TASK_MODIFY_TYPE = 2;
    private static final int TASK_MODIFY_VALUE = 3;
    private static final int TASK_MODIFY_CORRECT_LENGTH = 4;
    private static final int TASK_CANCEL_ID = 1;
    private static final int TASK_CANCEL_CORRECT_LENGTH = 2;
    private static final int RELOAD_CORRECT_LENGTH = 1;

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {

        if (strings.length == 0) {
            PluginMessage.showHelp(commandSender);
            return true;
        }
        String cmd = strings[0];
        switch (cmd.toLowerCase()){
            case CREATE:
                try {
                    if (strings[TASK_NAME] != null && strings[TASK_ID]!= null && strings[TASK_COMMAND] != null
                            && strings[TASK_RELEASE_TIME] != null && strings[TASK_EFFECT_TIME] != null && strings.length == TASK_CREATE_CORRECT_LENGTH) {
                        if (Data.addVote(strings[TASK_NAME], strings[TASK_ID], strings[TASK_COMMAND], strings[TASK_RELEASE_TIME], strings[TASK_EFFECT_TIME])) {
                            commandSender.sendMessage("[SakuraVote]投票创建成功，id：" + strings[TASK_ID]);
                        }else {
                            commandSender.sendMessage("[SakuraVote]投票创建失败，可能原因：[投票id]为数字，[投票id]重复");
                        }
                    }
                }catch (Exception e){
                    PluginMessage.showCreateErrMessage(commandSender);
                }
                break;
            case DEL:
            case DELETE:
                if (strings[1] != null && strings.length == TASK_DELETE_CORRECT_LENGTH) {
                    if (Data.delVote(strings[1])) {
                        commandSender.sendMessage("[SakuraVote]投票删除成功");
                    }else {
                        PluginMessage.showDelErrMessage(commandSender);
                    }
                }
                break;

            case APPROVE:
                if (strings[1] != null && strings.length == TASK_APPROVE_CORRECT_LENGTH) {
                    if (commandSender instanceof Player) {
                        if (Data.approveVote((Player) commandSender, strings[1])){
                            commandSender.sendMessage("[SakuraVote]投票成功");
                        }else {
                            commandSender.sendMessage("[SakuraVote]投票失败，id输入有误或任务不存在");
                        }
                    }else {
                        commandSender.sendMessage("[SakuraVote]必须以玩家身份执行");
                    }
                }
                break;

            case DISAPPROVE:
                if (strings[1] != null && strings.length == TASK_DISAPPROVE_CORRECT_LENGTH) {
                    if (commandSender instanceof Player) {
                        if (Data.disApproveVote((Player) commandSender, strings[1])){
                            commandSender.sendMessage("[SakuraVote]投票成功");
                        }else {
                            commandSender.sendMessage("[SakuraVote]投票失败，id输入有误或任务不存在");
                        }
                    }else {
                        commandSender.sendMessage("[SakuraVote]必须以玩家身份执行");
                    }
                }
                break;

            case LIST:
                if (strings.length == 1) {
                    if (Data.VOTE_TASKS.size() != 0) {
                        commandSender.sendMessage("==============SakuraVote==============");
                        for (VoteTask voteTask : Data.VOTE_TASKS) {
                            commandSender.sendMessage("投票id：" + voteTask.getTaskId() + "  投票名：" + voteTask.getTaskName() + "  执行指令：" + voteTask.getCommand()
                                    + "  发布时间：" + voteTask.getReleaseTime() + "  投票时长：" + voteTask.getEffectTime() + "秒" + "  是否循环：" + voteTask.isReuse());
                        }
                        commandSender.sendMessage("======================================");
                    }else {
                        commandSender.sendMessage("[SakuraVote]暂无投票");
                    }
                }else {
                    commandSender.sendMessage("[SakuraVote]输入有误，请输入/vote 查看使用说明");
                }
                break;

            case SET:
                if (strings[TASK_MODIFY_ID] != null && strings[TASK_MODIFY_TYPE] != null &&
                        strings[TASK_MODIFY_VALUE] != null && strings.length == TASK_MODIFY_CORRECT_LENGTH) {
                    if (Data.modifyVote(strings[TASK_MODIFY_ID], strings[TASK_MODIFY_TYPE], strings[TASK_MODIFY_VALUE])) {
                        commandSender.sendMessage("[SakuraVote]修改成功");
                    }else {
                        commandSender.sendMessage("[SakuraVote]修改失败，请重试");
                    }
                }else {
                    commandSender.sendMessage("[SakuraVote]输入有误，请输入/vote 查看使用说明");
                }
                break;

            case CANCEL:
                if (strings[TASK_CANCEL_ID] != null && strings.length == TASK_CANCEL_CORRECT_LENGTH) {
                    if (Timer.cancelTask(strings[TASK_CANCEL_ID])) {
                        commandSender.sendMessage("[SakuraVote]取消投票成功");
                    }else {
                        commandSender.sendMessage("[SakuraVote]取消失败，不存在该投票或投票未开始");
                    }
                }else {
                    commandSender.sendMessage("[SakuraVote]输入有误，请输入/vote 查看使用说明");
                }
                break;

            case RELOAD:
                if (strings.length == RELOAD_CORRECT_LENGTH) {
                    Data.reloadTaskAndConfig();
                    commandSender.sendMessage("[SakuraVote]重载成功");
                }
                break;
            default:
                commandSender.sendMessage("[SakuraVote]输入有误，请输入/vote 查看使用说明");
                break;
        }
        return true;
    }
}
