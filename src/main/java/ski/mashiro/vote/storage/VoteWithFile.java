package ski.mashiro.vote.storage;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * @author MashiroT
 */
public class VoteWithFile {
    private VoteWithFile() {}

    private static final String NAME = "name";
    private static final String COMMAND = "command";
    private static final String RELEASE_TIME = "releasetime";
    private static final String EFFECT_TIME = "effecttime";

    public static boolean createVoteFile(VoteTask voteTask) {
        File voteTaskFile = new File(Data.plugin.getDataFolder().getAbsolutePath() + "/VoteList/" + voteTask.getTaskName() + ".yml");
        YamlConfiguration yamlTaskFile = YamlConfiguration.loadConfiguration(voteTaskFile);
        if (!voteTaskFile.exists()) {
            try {
                if (voteTaskFile.createNewFile()) {
                    /*
                     * #TaskID:
                     * #Name:
                     * #Command:
                     * #releaseTime:
                     * #effectTime:
                     * #reuse:
                     */
                    yamlTaskFile.set("TaskID", voteTask.getTaskId());
                    yamlTaskFile.set("Name", voteTask.getTaskName());
                    yamlTaskFile.set("Command", voteTask.getCommand());
                    yamlTaskFile.set("releaseTime", voteTask.getReleaseTime());
                    yamlTaskFile.set("effectTime", voteTask.getEffectTime());
                    yamlTaskFile.set("reuse", voteTask.isReuse());

                    yamlTaskFile.save(voteTaskFile);
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean deleteVoteFile(int delId) {
        try {
            File[] voteFiles = new File(Data.plugin.getDataFolder() + "/VoteList").listFiles();
            if (voteFiles != null) {
                for (File voteFile : voteFiles) {
                    YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                    if (delId == yamlVoteFile.getInt("TaskID")) {
                        return voteFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean modifyVoteFile(String modifyId, String operate, String newValue) {
        File[] voteFiles = new File(Data.plugin.getDataFolder() + "/VoteList").listFiles();
        try {
            int id = Integer.parseInt(modifyId);
            if (voteFiles != null) {
                for (File voteFile : voteFiles) {
                    YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                    if (id == yamlVoteFile.getInt("TaskID")) {
                        switch (operate.toLowerCase()){
                            case NAME:
                                yamlVoteFile.set("Name", newValue);
                                yamlVoteFile.save(voteFile);
                                return true;
                            case COMMAND:
                                yamlVoteFile.set("Command", newValue);
                                return true;
                            case RELEASE_TIME:
                                long time = Data.transformTime(newValue) - System.currentTimeMillis();
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
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean isFileExist(int id){
        File[] voteFiles = new File(Data.plugin.getDataFolder() + "/VoteList").listFiles();
        if (voteFiles != null) {
            for (File voteFile : voteFiles) {
                YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                if (id == yamlVoteFile.getInt("TaskID")) {
                    return true;
                }
            }
        }
        return false;
    }

}