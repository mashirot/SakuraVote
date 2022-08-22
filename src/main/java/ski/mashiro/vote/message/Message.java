package ski.mashiro.vote.message;

import org.bukkit.command.CommandSender;

import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GREEN;

/**
 * @author MashiroT
 */
public class Message {

    private Message() {}

    public static void showHelp(CommandSender commandSender) {
        commandSender.sendMessage(DARK_GREEN + "==============Vote==============");
        commandSender.sendMessage(GREEN + "1. /vote create [投票名] [投票id] [通过后执行的指令] [发布时间] [有效时间]");
        commandSender.sendMessage(GREEN + "说明：指令不需要带 /，发布时间格式 [yyyy-MM-dd hh:MM:ss]，有效时间以[秒]为单位，1分钟就填 60");
        commandSender.sendMessage(GREEN + "2. /vote del [投票id]");
        commandSender.sendMessage(GREEN + "3. /vote approve [投票id]");
        commandSender.sendMessage(GREEN + "4. /vote disapprove [投票id]");
        commandSender.sendMessage(GREEN + "5. /vote list || 展示所有投票");
        commandSender.sendMessage(GREEN + "6. /vote set [投票id] [类型] [内容]");
        commandSender.sendMessage(GREEN + "说明：类型：投票名[name], 执行指令[command], 发布时间[releasetime], 有效时间[effecttime]");
        commandSender.sendMessage(GREEN + "7. /vote reuse [投票id]");
        commandSender.sendMessage(GREEN + "说明：会在第二天相同时间再次发起投票，如需取消再次输入即可");
        commandSender.sendMessage(GREEN + "8. /vote cancel [投票id]");
        commandSender.sendMessage(GREEN + "说明：只能取消进行中的投票(不删除)，删除投票请使用[2.]");
        commandSender.sendMessage(DARK_GREEN + "================================");
    }

    public static void showCreateErrMessage(CommandSender commandSender) {
        commandSender.sendMessage("投票创建失败，输入格式有误");
        commandSender.sendMessage("/vote create [投票名] [投票id] [通过后执行的指令] [发布时间] [有效时间]");
    }

    public static void showDelErrMessage(CommandSender commandSender) {
        commandSender.sendMessage("删除失败，id输入有误或任务不存在");
    }

}
