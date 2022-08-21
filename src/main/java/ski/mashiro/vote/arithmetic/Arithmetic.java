package ski.mashiro.vote.arithmetic;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ski.mashiro.vote.storage.Data;

import java.util.*;

/**
 * @author FeczIne
 */
public class Arithmetic {

    private static final int HALF = 2;

    public static int result(HashMap<Integer, Player> hashMap){
        Collection<Player> playerCollection = hashMap.values();
        int votePlayerCounts = playerCollection.size();
        int onlinePlayer = Bukkit.getServer().getOnlinePlayers().size();

        if (votePlayerCounts > (onlinePlayer / HALF)) {

            int disApprove = 0;
            List<Player> approvePlayers = new ArrayList<>();
            List<Player> disApprovePlayers = new ArrayList<>();

            Set<Map.Entry<Integer, Player>> singlePlayerChoices = hashMap.entrySet();
            for (Map.Entry<Integer, Player> singlePlayerChoice : singlePlayerChoices) {
                disApprove += singlePlayerChoice.getKey();
                if (singlePlayerChoice.getKey() != 0) {
                    disApprovePlayers.add(singlePlayerChoice.getValue());
                }else {
                    approvePlayers.add(singlePlayerChoice.getValue());
                }
            }
            if (disApprove < (votePlayerCounts / HALF)) {
                Data.showResult(approvePlayers, disApprovePlayers);
                return 0;
            }
            Data.showResult(approvePlayers, disApprovePlayers);
            return 1;
        }
        return -1;
    }
}
