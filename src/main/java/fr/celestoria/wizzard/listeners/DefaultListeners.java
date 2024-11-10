package fr.celestoria.wizzard.listeners;

import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.Status;
import fr.celestoria.api.gameapi.StatusChangeEvent;
import fr.celestoria.api.utils.PlayerUtils;
import fr.celestoria.api.utils.inv.ItemBuilder;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.game.WizzardGame;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DefaultListeners implements Listener {

  @EventHandler
  public void onStatusChange(StatusChangeEvent event) {
    CelestWizzard.getInstance().getGame().updateScoreboards();
    Status status = event.getStatus();
    switch (status) {
      case WAITING_FOR_PLAYERS:
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(
              Prefix.GAME_WIZZARD + "§cDémarrage annulé, il n'y a plus assez de joueurs !");
        }
        break;
      case IN_GAME:
        for (UUID uuid : CelestWizzard.getInstance().getGame().getGamePlayers().keySet()) {
          Player player = Bukkit.getPlayer(uuid);
          if (player == null) {
            return;
          }
          PlayerUtils.cleanPlayer(player);
          player
              .getInventory()
              .setItem(0, new ItemBuilder(Material.STICK).setName("§dBaguette magique"));
        }
        break;
      default:
        break;
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    event.setJoinMessage(null);

    WizzardGame game = CelestWizzard.getInstance().getGame();
    Player player = event.getPlayer();
    game.handleLogin(player);

    switch (game.getGameStatus()) {
      case STARTING:
      case READY_TO_START:
        CelestWizzard.getInstance().getGame().updateScoreboards();
        break;
      case WAITING_FOR_PLAYERS:
        CelestWizzard.getInstance().getGame().updateScoreboards();
        break;
      default:
        break;
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    event.setQuitMessage(null);

    WizzardGame game = CelestWizzard.getInstance().getGame();
    game.handleLogout(event.getPlayer());
  }

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
      event.setCancelled(true);
    }
  }
}
