package fr.celestoria.wizzard;

import fr.celestoria.wizzard.game.WizzardGame;
import fr.celestoria.wizzard.listeners.DefaultListeners;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CelestWizzard extends JavaPlugin {

  // ========================================================================
  // STATIC FIELDS
  // ========================================================================

  @Getter private static CelestWizzard instance;

  // ========================================================================
  // FIELDS
  // ========================================================================

  private WizzardGame game = null;

  // ========================================================================
  // METHODS
  // ========================================================================

  @Override
  public void onEnable() {
    getLogger().info("Chargement du plugin...");
    if (instance == null) {
      instance = this;
    }

    this.game = new WizzardGame();

    Bukkit.getPluginManager().registerEvents(new DefaultListeners(), this);
  }

  @Override
  public void onDisable() {
    getLogger().info("Déchargement du plugin...");

    Bukkit.getScheduler().cancelAllTasks();

    if (instance != null) {
      instance = null;
    }
  }
}
