package fr.celestoria.wizzard.game;

import fr.celestoria.api.gameapi.Game;
import fr.celestoria.api.gameapi.GamePlayer;
import fr.celestoria.api.gameapi.GameType;
import fr.celestoria.api.utils.ConvertTime;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.countdowns.EndCountdown;
import fr.celestoria.wizzard.countdowns.PreStartingCountdown;
import fr.celestoria.wizzard.countdowns.StartingCountdown;
import fr.celestoria.wizzard.listeners.GameListeners;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WizzardGame extends Game {

  // Exemple of future code (with provider -> Redis TODO)
  private boolean isHost;

  public WizzardGame() {
    super(GameType.WIZZARD);
    initCountdowns(
        new PreStartingCountdown(),
        new StartingCountdown(),
        new LoopScheduler(),
        new EndCountdown());
  }

  public void startWizardGame() {
    super.startGame(new GameListeners());
    for (UUID uuid : getGamePlayers().keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      player.setScoreboard(getScoreboard());
      getScoreboard().getTeam("default").addPlayer(player);
    }
  }

  @Override
  public void updateScoreboard(GamePlayer gamePlayer) {
    switch (getGameStatus()) {
      case WAITING_FOR_PLAYERS:
        gamePlayer
            .getBoard()
            .updateLines(
                "§8#"
                    + getId()
                    + " ❙ "
                    + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
                "§r",
                "  §f▪ Statut: §eEn attente",
                "§r",
                "  §f▪ Carte: §a" + CelestWizzard.getInstance().getGame().getWorldName(),
                "  §f▪ Joueurs: §b" + countGamePlayers() + "§f/§a" + getMaxPlayers(),
                "§r",
                "  §f▪ §7Attente de joueurs...",
                "§r",
                "§6play.celestoria.fr");
        break;
      case READY_TO_START:
      case STARTING:
        gamePlayer
            .getBoard()
            .updateLines(
                "§8#"
                    + getId()
                    + " ❙ "
                    + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
                "§r",
                "  §f▪ Statut: §eEn attente",
                "§r",
                "  §f▪ Carte: §a" + CelestWizzard.getInstance().getGame().getWorldName(),
                "  §f▪ Joueurs: §b" + countGamePlayers() + "§f/§a" + getMaxPlayers(),
                "§r",
                "  §f▪ §fDémarrage dans: §a" + ConvertTime.formatTime(getCurrentTask().getTimer()),
                "§r",
                "§6play.celestoria.fr");
        break;
      case IN_GAME:
        gamePlayer
            .getBoard()
            .updateLines(
                "§8#"
                    + getId()
                    + " ❙ "
                    + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
                "§r",
                "  §f▪ Carte: §a" + CelestWizzard.getInstance().getGame().getWorldName(),
                "§r ",
                "  §f▪ Temps restant: §e"
                    + ConvertTime.formatTime(((LoopScheduler) getCurrentTask()).getTimeLeft()),
                "  §f▪ Kill(s): §b" + gamePlayer.getKills(),
                "  §f▪ Mort(s): §c" + gamePlayer.getDeaths(),
                "  §f▪ Ratio: §a" + gamePlayer.getRatio(),
                "§r",
                "§6play.celestoria.fr");
        break;
      case FINISHED:
        gamePlayer
            .getBoard()
            .updateLines(
                "§8#"
                    + getId()
                    + " ❙ "
                    + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
                "§r",
                "  §f▪ Carte: §a" + CelestWizzard.getInstance().getGame().getWorldName(),
                "§r",
                "  §f▪ §aPartie terminée !",
                "  §f▪ Kills: §b" + gamePlayer.getKills(),
                "  §f▪ Mort(s): §c" + gamePlayer.getDeaths(),
                "  §f▪ Ratio: §a" + gamePlayer.getRatio(),
                "§r",
                "§6play.celestoria.fr");
        break;
      default:
        break;
    }
  }
}
