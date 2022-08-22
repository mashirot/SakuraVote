package ski.mashiro.vote.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * @author FeczIne
 */
public class CreateEg {
    private CreateEg() {}

    public static void isFolderExist(Plugin plugin) {
        File[] fileList = new File(plugin.getDataFolder() + "").listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (!file.isDirectory() || !"VoteList".equals(file.getName())) {
                    createFolder(plugin);
                }
            }
        }
    }

    public static void createFolder(Plugin plugin) {
        File folder = new File(plugin.getDataFolder() + "/VoteList");
        File eg = new File(folder, "VoteEg.yml");
        try {
            if (folder.mkdir() && eg.createNewFile()) {
                YamlConfiguration yamlEg = YamlConfiguration.loadConfiguration(eg);
                yamlEg.set("TaskID", 6657);
                yamlEg.set("Name", "投票示例");
                yamlEg.set("Command", "stop");
                yamlEg.set("releaseTime", "2042-01-01 00:00:00");
                yamlEg.set("effectTime", 600);
                yamlEg.set("reuse", false);
                yamlEg.save(eg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
