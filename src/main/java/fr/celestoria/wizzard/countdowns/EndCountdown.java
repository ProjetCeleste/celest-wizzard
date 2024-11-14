package fr.celestoria.wizzard.countdowns;

import fr.celestoria.api.database.Account;
import fr.celestoria.api.database.AccountProvider;
import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.AbstractCountdown;
import fr.celestoria.api.gameapi.GamePlayer;
import fr.celestoria.api.gameapi.Leaderboard;
import fr.celestoria.api.utils.Titles;
import fr.celestoria.wizzard.CelestWizzard;
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
      Leaderboard leaderboard =
          new Leaderboard(CelestWizzard.getInstance().getGame().getGamePlayers());

      giveCoins();

      for (Player players : Bukkit.getOnlinePlayers()) {
        players.sendMessage(leaderboard.getLeaderboardMessage());
        players.sendMessage(
            Prefix.GAME_WIZZARD + "§aPartie terminée§f. Retour au lobby dans §b10 §fsecondes.");
        Titles.sendTitle(
            players,
            "§a§lPartie terminée",
            "§eVainqueur: §b"
                + Bukkit.getOfflinePlayer(leaderboard.getTopGamePlayers(1).get(0).getKey())
                    .getName());
      }
    }

    if (timer == 0) {
      for (Player players : Bukkit.getOnlinePlayers()) {
        players.kickPlayer("§cPartie terminée.");
      }
      Bukkit.shutdown();
    }
  }

  private void giveCoins() {
    for (GamePlayer gamePlayer : CelestWizzard.getInstance().getGame().getGamePlayers().values()) {
      Player player = gamePlayer.getPlayerIfOnline();
      if (player != null) {
        double coins = 1.5 * (gamePlayer.getKills() + gamePlayer.getFinalkills());
        int finalCoins = (int) Math.round(coins);
        AccountProvider accountProvider = new AccountProvider(player.getUniqueId());
        Account account = accountProvider.getAccount();
        if (account != null) {
          account.addCoins(finalCoins);
          player.sendMessage(
              Prefix.GAME_WIZZARD
                  + "§fVous avez gagné §e+"
                  + finalCoins
                  + " §e"
                  + Prefix.SYMBOL_COINS);
        }
      }
    }
  }
}
