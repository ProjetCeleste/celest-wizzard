package fr.celestoria.wizzard.powerups;

import fr.celestoria.api.gameapi.AbstractCountdown;

public class PowerUpCountdown extends AbstractCountdown {

  private PowerUpType powerUpType;

  protected PowerUpCountdown(PowerUpType powerUpType, int timer) {
    super(timer);
    this.powerUpType = powerUpType;
  }

  @Override
  public void run() {

  }
}
