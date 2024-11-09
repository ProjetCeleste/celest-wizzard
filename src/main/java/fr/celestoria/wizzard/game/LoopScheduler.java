package fr.celestoria.wizzard.game;

import fr.celestoria.api.gameapi.AbstractCountdown;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.boards.GameBoard;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class LoopScheduler extends AbstractCountdown {

  // ========================================================================
  // FIELDS
  // ========================================================================

  private int ticks = 0;
  private int timeLeft = 60 * 5;

  // ========================================================================
  // CONSTRUCTOR
  // ========================================================================

  protected LoopScheduler() {
    super(-1);
  }

  // ========================================================================
  // OVERRIDE METHODS
  // ========================================================================

  @Override
  public void run() {
    ticks++;

    boolean secondUpdated = false;
    if (ticks % 20 == 0) {
      secondUpdated = true;
      timeLeft--;

      for (Player onlinePlayers : Bukkit .getOnlinePlayers()) {
        new GameBoard(onlinePlayers).updateBoard();
      }
    }

    if (timeLeft <= 0) {
      CelestWizzard.getInstance().getGame().endGame();
    }
  }
}
