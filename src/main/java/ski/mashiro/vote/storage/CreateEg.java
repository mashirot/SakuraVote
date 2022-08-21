package ski.mashiro.vote.storage;

import org.bukkit.plugin.Plugin;

/**
 * @author FeczIne
 */
public class CreateEg {

    public static void isFolderExist(Plugin plugin){
        String[] fileNameList = plugin.getDataFolder().list();
        if (fileNameList != null) {
            for (String filename : fileNameList) {
                if (!"VoteList".equals(filename)) {
                    createFolder(plugin);
                }
            }
        }
    }

    public static void createFolder(Plugin plugin){
        plugin.saveResource("VoteList/VoteEg1", false);
    }
}
