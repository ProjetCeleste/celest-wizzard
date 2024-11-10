package fr.celestoria.wizzard.game;

import fr.celestoria.api.gameapi.Game;
import fr.celestoria.api.gameapi.GamePlayer;
import fr.celestoria.api.gameapi.GameType;
import fr.celestoria.api.gameapi.Status;
import fr.celestoria.api.gameapi.StatusChangeEvent;
import fr.celestoria.api.utils.ConvertTime;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.countdowns.EndCountdown;
import fr.celestoria.wizzard.countdowns.PreStartingCountdown;
import fr.celestoria.wizzard.countdowns.StartingCountdown;
import fr.celestoria.wizzard.listeners.InGameListeners;
import org.bukkit.Bukkit;

public class WizzardGame extends Game {

  public WizzardGame() {
    super(GameType.WIZZARD);
  }

  @Override
  public void waitingGame() {
    Status gameStatus = getGameStatus();
    if (gameStatus.equals(Status.READY_TO_START) || gameStatus.equals(Status.STARTING)) {
      setGameStatus(Status.WAITING_FOR_PLAYERS);
      cancelTask();
      Bukkit.getPluginManager().callEvent(new StatusChangeEvent(Status.WAITING_FOR_PLAYERS));
    }
  }

  @Override
  public void preStartingGame() {
    Status gameStatus = getGameStatus();
    if (gameStatus.equals(Status.WAITING_FOR_PLAYERS)) {
      setGameStatus(Status.READY_TO_START);
      cancelTask();
      setCurrentTask(new PreStartingCountdown());
      setGameTask(getCurrentTask().runTaskTimer(CelestWizzard.getInstance(), 0, 20));
      Bukkit.getPluginManager().callEvent(new StatusChangeEvent(Status.READY_TO_START));
    }
  }

  @Override
  public void startingGame() {
    Status gameStatus = getGameStatus();
    if (gameStatus.equals(Status.READY_TO_START)) {
      setGameStatus(Status.STARTING);
      cancelTask();
      setCurrentTask(new StartingCountdown());
      setGameTask(getCurrentTask().runTaskTimer(CelestWizzard.getInstance(), 0, 20));
      Bukkit.getPluginManager().callEvent(new StatusChangeEvent(Status.STARTING));
    }
  }

  @Override
  public void startGame() {
    Status gameStatus = getGameStatus();
    if (gameStatus.equals(Status.STARTING)) {
      setGameStatus(Status.IN_GAME);
      cancelTask();
      setCurrentTask(new LoopScheduler());
      setGameTask(getCurrentTask().runTaskTimerAsynchronously(CelestWizzard.getInstance(), 0, 1));
      Bukkit.getPluginManager().callEvent(new StatusChangeEvent(Status.IN_GAME));
      setCurrentListener(new InGameListeners());
      Bukkit.getPluginManager().registerEvents(getCurrentListener(), CelestWizzard.getInstance());
    }
  }

  @Override
  public void endGame() {
    Status gameStatus = getGameStatus();
    if (gameStatus.equals(Status.IN_GAME)) {
      setGameStatus(Status.FINISHED);
      cancelTask();
      setCurrentTask(new EndCountdown());
      setGameTask(getCurrentTask().runTaskTimer(CelestWizzard.getInstance(), 0, 20));
      Bukkit.getPluginManager().callEvent(new StatusChangeEvent(Status.FINISHED));
    }
  }

  @Override
  public void updateScoreboard(GamePlayer gamePlayer) {
    switch (getGameStatus()) {
      case WAITING_FOR_PLAYERS:
        gamePlayer.getBoard().updateLines("§8#"
                + getId()
                + " ❙ "
                + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
            "§r",
            "  §f▪ Statut: §eEn attente",
            "  §f▪ Joueurs: §b" + countGamePlayers() + "§f/§a" + getMaxPlayers(),
            "§r",
            "  §f▪ §7Attente de joueurs...",
            "§r",
            "§6play.celestoria.fr"
        );
        break;
      case READY_TO_START:
      case STARTING:
        gamePlayer.getBoard().updateLines("§8#"
                + getId()
                + " ❙ "
                + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
            "§r",
            "  §f▪ Statut: §eEn attente",
            "  §f▪ Joueurs: §b" + countGamePlayers() + "§f/§a" + getMaxPlayers(),
            "§r",
            "  §f▪ §fDémarrage dans: §a" + ConvertTime.formatTime(getCurrentTask().getTimer()),
            "§r",
            "§6play.celestoria.fr");
        break;
      case IN_GAME:
        gamePlayer.getBoard().updateLines(
            "§8#"
                + getId()
                + " ❙ "
                + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
            "§r",
            "  §f▪ Temps restant: §e"
                + ConvertTime.formatTime(((LoopScheduler) getCurrentTask()).getTimeLeft()),
            "  §f▪ Kills: §b" + gamePlayer.getKills(),
            "  §f▪ Ratio: §a" + gamePlayer.getRatio(),
            "§r",
            "§6play.celestoria.fr"
        );
        break;
      case FINISHED:
        gamePlayer.getBoard().updateLines(
            "§8#"
                + getId()
                + " ❙ "
                + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
            "§r",
            "  §f▪ &aPartie terminée !",
            "  §f▪ Kills: §b" + gamePlayer.getKills(),
            "§r",
            "§6play.celestoria.fr"
        );
      default:break;
    }
  }
}
