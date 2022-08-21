package ski.mashiro.vote.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import ski.mashiro.vote.timer.Timer;

import java.io.File;

/**
 * @author MashiroT
 */
public class StoreVoteInFile {

    private static final String NAME = "name";
    private static final String COMMAND = "command";
    private static final String RELEASE_TIME = "releasetime";
    private static final String EFFECT_TIME = "effecttime";

    public static boolean createVoteFile(Plugin plugin, VoteTask voteTask) {

        File voteTaskFile = new File(plugin.getDataFolder() + "/VoteList/" + voteTask.getTaskName() + ".yml");

        YamlConfiguration yamlTaskFile = YamlConfiguration.loadConfiguration(voteTaskFile);

        if (!voteTaskFile.exists()) {

            try {
                if (voteTaskFile.createNewFile()) {

                    /*
                     * #TaskID:
                     * #Name:
                     * #Command:
                     * #  - aaa
                     * #  - bbb
                     * #  - ccc
                     * #releaseTime:
                     * #effectTime:
                     */
                    yamlTaskFile.set("TaskID", voteTask.getTaskId());
                    yamlTaskFile.set("Name", voteTask.getTaskName());
                    yamlTaskFile.set("Command", voteTask.getCommand());
                    yamlTaskFile.set("releaseTime", voteTask.getReleaseTime());
                    yamlTaskFile.set("effectTime", voteTask.getEffectTime());

                    yamlTaskFile.save(voteTaskFile);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean modifyVoteFile(String id, String operate, String newValue){

        File[] voteFileNames = new File(Data.plugin.getDataFolder() + "/VoteList").listFiles();
        if (voteFileNames != null) {
            for (File voteFileName : voteFileNames) {
                if (id.equals(voteFileName.getName())) {
                    YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFileName);
                    try {
                        switch (operate.toLowerCase()){
                            case NAME:
                                yamlVoteFile.set("Name", newValue);
                                yamlVoteFile.save(voteFileName);
                                return true;
                            case COMMAND:
                                yamlVoteFile.set("Command", newValue);
                                return true;
                            case RELEASE_TIME:
                                long time = Timer.transformTime(newValue) - System.currentTimeMillis();
                                if (time > 0) {
                                    yamlVoteFile.set("releaseTime", newValue);
                                    return true;
                                }
                                return false;
                            case EFFECT_TIME:
                                yamlVoteFile.set("effectTime", Integer.parseInt(newValue));
                                return true;
                            default:
                                return false;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
        }
        return false;
    }
}