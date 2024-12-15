package fr.celestoria.wizzard.countdowns;

import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.AbstractCountdown;
import fr.celestoria.api.utils.Titles;
import fr.celestoria.api.utils.xutils.XSound;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.game.WizzardGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StartingCountdown extends AbstractCountdown {

  private int ticks = 0;

  public StartingCountdown() {
    super(6);
  }

  @Override
  public void run() {
    ticks++;

    if (ticks % 20 == 0) {
      timer--;
      if (timer <= 0) {
        String rulesMessage =
            Prefix.GAME_WIZZARD
                + "Règles à faire";
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(rulesMessage);
          XSound.ENTITY_ENDER_DRAGON_GROWL.play(onlinePlayer);
        }
        CelestWizzard.getInstance().getGame().startWizardGame();
      } else {
        WizzardGame game = CelestWizzard.getInstance().getGame();
        game.updateScoreboards();
        String defaultMessage =
            Prefix.GAME_WIZZARD
                + "Lancement de la partie dans §a"
                + timer
                + " seconde"
                + (timer == 1 ? "" : "s")
                + "§f.";
        for (Player players : Bukkit.getOnlinePlayers()) {
          players.sendMessage(defaultMessage);
          XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(players);
        }
      }
    }
  }
}
