package fr.celestoria.wizzard.countdowns;

import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.AbstractCountdown;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.boards.GameBoard;
import fr.celestoria.wizzard.boards.StartingBoard;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class StartingCountdown extends AbstractCountdown {

  public StartingCountdown() {
    super(26);
  }

  @Override
  public void run() {
    timer--;
    if (timer == 0) {
      String rulesMessage = Prefix.GAME_WIZZARD
          + "Elimine tes adversaires avec un baton magique. Lorsque tu fais clic droit avec celui-là un sort est lancé tout droit !";
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.sendMessage(rulesMessage);
        onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 0.1f);
        new GameBoard(onlinePlayer).updateBoard();
      }
      CelestWizzard.getInstance().getGame().startGame();
    } else {
      String defaultMessage = "Lancement de la partie dans §b" + timer + "§f seconde" + (timer == 1 ? "" : "s");
      for (Player players : Bukkit.getOnlinePlayers()) {
        players.sendMessage(defaultMessage);
        players.playSound(players.getLocation(), Sound.ORB_PICKUP, 1F, 0.1F);
        new StartingBoard(players).updateBoard();
      }
    }
  }
}
