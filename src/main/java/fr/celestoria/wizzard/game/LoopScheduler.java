package fr.celestoria.wizzard.game;

import fr.celestoria.api.CelestAPI;
import fr.celestoria.api.gameapi.AbstractCountdown;
import fr.celestoria.api.gameapi.host.GameData;
import fr.celestoria.api.gameapi.host.GameDataProvider;
import fr.celestoria.wizzard.CelestWizzard;
import lombok.Getter;

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
    // this.timeLeft = new GameDataProvider(CelestAPI.getInstance().getServerDisplayName()).getDataFromRedis().getInt("minutes");
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

      if (timeLeft <= 0) {
        CelestWizzard.getInstance().getGame().endGame();
        return;
      }

      CelestWizzard.getInstance().getGame().updateScoreboards();
    }
  }
}
