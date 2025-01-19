package fr.celestoria.wizzard.countdowns;

import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.AbstractCountdown;
import fr.celestoria.api.utils.ActionBar;
import fr.celestoria.api.utils.xutils.XSound;
import fr.celestoria.wizzard.CelestWizzard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PreStartingCountdown extends AbstractCountdown {

  private int ticks = 0;

  public PreStartingCountdown() {
    super(30);
  }

  @Override
  public void run() {
    ticks++;

    if (ticks % 20 == 0) {
      timer--;
      switch (timer) {
        case 30:
        case 20:
        case 10:
          String message =
              Prefix.GAME_WIZZARD + "Lancement de la partie dans §a" + timer + " secondes§f.";
          for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(message);
            XSound.ENTITY_ARROW_HIT_PLAYER.play(onlinePlayer);
          }
          break;
        case 5:
          CelestWizzard.getInstance().getGame().startingGame();
          break;
        default:
          break;
      }

      CelestWizzard.getInstance().getGame().updateScoreboards();
    }
  }
}
