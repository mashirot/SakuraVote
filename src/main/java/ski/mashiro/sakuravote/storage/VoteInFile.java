package ski.mashiro.sakuravote.storage;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * @author MashiroT
 */
public class VoteInFile {
    private VoteInFile() {}

    public static final String NAME = "name";
    public static final String COMMAND = "command";
    public static final String RELEASE_TIME = "releasetime";
    public static final String EFFECT_TIME = "effecttime";
    public static final String REUSE = "reuse";

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
        if (Data.isInteger(modifyId)) {
            int id = Integer.parseInt(modifyId);
            if (voteFiles != null) {
                for (File voteFile : voteFiles) {
                    YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                    if (id == yamlVoteFile.getInt("TaskID")) {
                        try {
                            switch (operate.toLowerCase()){
                                case NAME:
                                    yamlVoteFile.set("Name", newValue);
                                    yamlVoteFile.save(voteFile);
                                    return true;
                                case COMMAND:
                                    yamlVoteFile.set("Command", Data.replaceCommand(newValue));
                                    yamlVoteFile.save(voteFile);
                                    return true;
                                case RELEASE_TIME:
                                    if (Data.transformTime(newValue) != -1) {
                                        long time = Data.transformTime(newValue) - System.currentTimeMillis();
                                        if (time > 0) {
                                            yamlVoteFile.set("releaseTime", newValue);
                                            yamlVoteFile.save(voteFile);
                                            return true;
                                        }
                                    }
                                    return false;
                                case EFFECT_TIME:
                                    yamlVoteFile.set("effectTime", Integer.parseInt(newValue));
                                    yamlVoteFile.save(voteFile);
                                    return true;
                                case REUSE:
                                    switch (newValue.toLowerCase()) {
                                        case "true":
                                            yamlVoteFile.set("reuse", true);
                                            yamlVoteFile.save(voteFile);
                                            return true;
                                        case "false":
                                            yamlVoteFile.set("reuse", false);
                                            yamlVoteFile.save(voteFile);
                                            return true;
                                        default:
                                            return false;
                                    }
                                default:
                                    return false;
                            }
                        } catch (Exception e) {
                            return false;
                        }
                    }
                }
            }
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

    public static void modifyReuseTime(VoteTask voteTask) {
        File[] voteFiles = new File(Data.plugin.getDataFolder() + "/VoteList").listFiles();
        if (voteFiles != null) {
            for (File voteFile : voteFiles) {
                YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                try {
                    if (voteTask.getTaskId() == yamlVoteFile.getInt("TaskID")) {
                        yamlVoteFile.set("releaseTime", voteTask.getReleaseTime());
                        yamlVoteFile.save(voteFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static YamlConfiguration findSpecifyVoteFile(String id) {
        if (Data.isInteger(id)) {
            File[] voteFiles = new File(Data.plugin.getDataFolder() + "/VoteList").listFiles();
            if (voteFiles != null) {
                for (File voteFile : voteFiles) {
                    YamlConfiguration yamlVoteFile = YamlConfiguration.loadConfiguration(voteFile);
                    if (Integer.parseInt(id) == yamlVoteFile.getInt("TaskID")) {
                        return yamlVoteFile;
                    }
                }
            }
        }
        return null;
    }

}