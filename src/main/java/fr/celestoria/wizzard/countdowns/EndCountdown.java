package fr.celestoria.wizzard.countdowns;

import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.AbstractCountdown;
import fr.celestoria.api.gameapi.Leaderboard;
import fr.celestoria.api.utils.Titles;
import fr.celestoria.wizzard.CelestWizzard;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndCountdown extends AbstractCountdown {

  public EndCountdown() {
    super(11);
  }

  @Override
  public void run() {
    timer--;
    if (timer == 10) {
      Leaderboard leaderboard = new Leaderboard(CelestWizzard.getInstance().getGame().getGamePlayers());
      List<Map.Entry<UUID, Integer>> leaderList = leaderboard.getTopGamePlayers(3);
      String leaderboardMessage = "\n  §f▪ §6§lPremier: §6" + Bukkit.getOfflinePlayer(leaderList.get(0).getKey()).getName() + " §7(" + leaderList.get(0).getValue() + ")"
          + "\n  §f▪ §7§lDeuxième: §7"  + Bukkit.getOfflinePlayer(leaderList.get(1).getKey()).getName() + " §7(" + leaderList.get(1).getValue() + ")"
          + "\n  §f▪ §8§lTroisième: §8" + Bukkit.getOfflinePlayer(leaderList.get(2).getKey()).getName() + " §7(" + leaderList.get(2).getValue() + ")\n§r ";
      for (Player players : Bukkit.getOnlinePlayers()) {
        players.sendMessage(
            Prefix.GAME_WIZZARD + "§aPartie terminée§f. Retour au lobby dans §b5 §fsecondes.");
        players.sendMessage(leaderboardMessage);
        Titles.sendTitle(players, "§a§lPartie terminée", "§eVainqueur: §b" + Bukkit.getOfflinePlayer(leaderList.get(0).getKey()).getName());
      }
    }

    if (timer == 0) {
      for (Player players : Bukkit.getOnlinePlayers()) {
        players.kickPlayer("§cPartie terminée.");
      }
      Bukkit.shutdown();
    }
  }
}
