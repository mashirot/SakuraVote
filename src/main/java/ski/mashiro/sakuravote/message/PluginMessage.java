package ski.mashiro.sakuravote.message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ski.mashiro.sakuravote.votetype.ConditionVote;

import static org.bukkit.ChatColor.*;

/**
 * @author MashiroT
 */
public class PluginMessage {

    private PluginMessage() {}

    public static void showHelp(CommandSender commandSender) {
        commandSender.sendMessage(DARK_GREEN + "==============SakuraVote==============");
        commandSender.sendMessage(GREEN + "1. /vote create [投票名] [投票id] [通过后执行的指令] [发布时间] [有效时间]");
        commandSender.sendMessage(GRAY + "说明：指令不需要带 / ， 中间空格使用_代替, 例：time_set_0");
        commandSender.sendMessage(GRAY + "说明：发布时间格式 [yyyy-MM-dd|hh:MM:ss]");
        commandSender.sendMessage(GRAY + "说明：有效时间以[秒]为单位，1分钟就填 60");
        commandSender.sendMessage(GREEN + "2. /vote del [投票id]");
        commandSender.sendMessage(GREEN + "3. /vote approve [投票id]");
        commandSender.sendMessage(GREEN + "4. /vote disapprove [投票id]");
        commandSender.sendMessage(GREEN + "5. /vote list [类型] || 未开始：[going] , 全部：[all]");
        commandSender.sendMessage(GREEN + "6. /vote set [投票id] [类型] [内容]");
        commandSender.sendMessage(GRAY + "类型：投票名[name], 执行指令[command], 发布时间[releasetime]");
        commandSender.sendMessage(GRAY + "类型：有效时间[effecttime], 循环[reuse](内容填写true/false)");
        commandSender.sendMessage(GREEN + "7. /vote cancel [投票id]");
        commandSender.sendMessage(GREEN + "8. /vote condcreate [投票名] [投票id] [通过后执行的指令] [需要玩家数量] [有效时间]");
        commandSender.sendMessage(GREEN + "9. /vote condset [投票id] [类型] [内容]");
        commandSender.sendMessage(GRAY + "类型：投票名[name], 执行指令[command], 需要玩家数量[playernumber]");
        commandSender.sendMessage(GRAY + "类型：有效时间[effecttime], 重启后自动启用[autostart](内容填写true/false)");
        commandSender.sendMessage(GRAY + "说明：只能取消未过期的投票(不删除)，删除投票请使用[2.]");
        commandSender.sendMessage(GREEN + "10. /vote reload");
        commandSender.sendMessage(DARK_GREEN + "======================================");
    }

    public static void startVoteMessage(Object o) {
        ConditionVote voteTask = (ConditionVote) o;
        Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + YELLOW + "即将开始投票");
        Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + YELLOW  + "投票名：" + voteTask.getTaskName());
        Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + YELLOW  + "投票ID：" + voteTask.getTaskId());
        Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + YELLOW  + "投票时间：" + voteTask.getEffectTime() + "秒");
        Bukkit.broadcastMessage(GREEN + "[SakuraVote] " + YELLOW  + "输入/vote [approve/disapprove] " + voteTask.getTaskId() + " 进行支持或反对");
    }

}
