package fr.celestoria.wizzard.countdowns;

import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.AbstractCountdown;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.boards.StartingBoard;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PreStartingCountdown extends AbstractCountdown {

  public PreStartingCountdown() {
    super(26);
  }

  @Override
  public void run() {
    timer--;
    switch (timer) {
      case 30:
      case 20:
      case 10:
        String message = Prefix.GAME_WIZZARD + "Lancement de la partie dans §a" + timer + " secondes§f.";
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(message);
          onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ORB_PICKUP, 1f, 0.1f);
          new StartingBoard(onlinePlayer).updateBoard();
        }
        break;
      case 5:
        CelestWizzard.getInstance().getGame().startingGame();
        break;
      default:break;
    }
  }
}
