package fr.celestoria.wizzard.countdowns;

import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.AbstractCountdown;
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
      for (Player players : Bukkit.getOnlinePlayers()) {
        players.sendMessage(
            Prefix.GAME_WIZZARD + "§aPartie terminée§f. Retour au lobby dans §b5 §fsecondes.");
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
