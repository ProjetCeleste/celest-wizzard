package fr.celestoria.wizzard.game;

import fr.celestoria.api.gameapi.Game;
import fr.celestoria.api.gameapi.GameType;
import fr.celestoria.api.gameapi.Status;
import fr.celestoria.api.gameapi.StatusChangeEvent;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.countdowns.EndCountdown;
import fr.celestoria.wizzard.countdowns.PreStartingCountdown;
import fr.celestoria.wizzard.countdowns.StartingCountdown;
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
    if(gameStatus.equals(Status.WAITING_FOR_PLAYERS)) {
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
    if(gameStatus.equals(Status.READY_TO_START)) {
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
    }
  }

  @Override
  public void endGame() {
    Status gameStatus = getGameStatus();
    if(gameStatus.equals(Status.IN_GAME)) {
      setGameStatus(Status.FINISHED);
      cancelTask();
      setCurrentTask(new EndCountdown());
      setGameTask(getCurrentTask().runTaskTimer(CelestWizzard.getInstance(), 0, 20));
      Bukkit.getPluginManager().callEvent(new StatusChangeEvent(Status.FINISHED));
    }
  }
}
