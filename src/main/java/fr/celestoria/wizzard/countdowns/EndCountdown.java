package fr.celestoria.wizzard.countdowns;

import fr.celestoria.api.CelestAPI;
import fr.celestoria.api.database.Account;
import fr.celestoria.api.database.AccountProvider;
import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.AbstractCountdown;
import fr.celestoria.api.gameapi.GamePlayer;
import fr.celestoria.api.gameapi.Leaderboard;
import fr.celestoria.api.utils.JsonMessage;
import fr.celestoria.api.utils.MathUtils;
import fr.celestoria.api.utils.Titles;
import fr.celestoria.wizzard.CelestWizzard;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndCountdown extends AbstractCountdown {

  private int ticks = 0;

  public EndCountdown() {
    super(11);
  }

  @Override
  public void run() {
    ticks++;
    if (ticks % 20 == 0) {

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
  }

  private void giveCoins() {
    int coinsBooster = CelestWizzard.getInstance().getGame().getGameBooster();
    String boostedBy = "";
    if (coinsBooster > 0) {
      boostedBy = " §7(Boosté par §a"
          + Bukkit.getOfflinePlayer(
          CelestWizzard.getInstance().getGame().getPlayerBooster()).getName() + "§7)";
    }

    for (GamePlayer gamePlayer : CelestWizzard.getInstance().getGame().getGamePlayers().values()) {
      Player player = gamePlayer.getPlayerIfOnline();
      if (player != null) {
        int totalKills = gamePlayer.getKills() + gamePlayer.getFinalkills();
        double baseCoins = 0.5 * totalKills;
        int roundBaseCoins = (int) Math.round(baseCoins);
        int finalCoins = 0;


        if (coinsBooster > 0) {
          finalCoins = MathUtils.addPourcentage(roundBaseCoins, coinsBooster);
        }

        int coinsBoosted = finalCoins - roundBaseCoins;
        AccountProvider accountProvider = new AccountProvider(player.getUniqueId());
        Account account = accountProvider.getAccount();
        if (account != null) {
          account.addCoins(finalCoins);
          player.spigot().sendMessage(new JsonMessage(
              Prefix.GAME_WIZZARD
              + "§fVous avez gagné").build(),
              new JsonMessage(" §e+" + finalCoins + " §e" + Prefix.SYMBOL_COINS)
                  .addHoverEvent(
                      "§fCoins de partie: §e" + roundBaseCoins + " " + Prefix.SYMBOL_COINS,
                      "§fCoins boostés: §e" + coinsBoosted + " " + Prefix.SYMBOL_COINS).build(),
              new JsonMessage(" " + boostedBy).build());
        }
      }
    }

    // +       (45 x nombre de joueur de partie) * 2
  }
}
