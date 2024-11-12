package fr.celestoria.wizzard.listeners;

import fr.celestoria.api.enums.Prefix;
import fr.celestoria.api.gameapi.Status;
import fr.celestoria.api.gameapi.StatusChangeEvent;
import fr.celestoria.api.utils.PlayerUtils;
import fr.celestoria.api.utils.inv.ItemBuilder;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.game.WizzardGame;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
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
        List<Location> locs = CelestWizzard.getInstance().getGame().getSpawns();
        for (UUID uuid : CelestWizzard.getInstance().getGame().getGamePlayers().keySet()) {
          Player player = Bukkit.getPlayer(uuid);
          if (player == null) {
            return;
          }
          PlayerUtils.cleanPlayer(player);
          player
              .getInventory()
              .setItem(0, new ItemBuilder(Material.STICK).setName("§dBaguette magique"));
          CelestWizzard.getInstance().getGame().getGamePlayer(player).secretTeleport(locs.get(0));
          locs.remove(0);
        }
        break;
      default:
        break;
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    event.setJoinMessage(null);

    WizzardGame game = CelestWizzard.getInstance().getGame();
    Player player = event.getPlayer();
    game.handleLogin(player);


    CelestWizzard.getInstance().getGame().updateScoreboard(player);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    event.setQuitMessage(null);

    WizzardGame game = CelestWizzard.getInstance().getGame();
    game.handleLogout(event.getPlayer());
  }

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    event.setFormat("%s§f: %s");
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

  @EventHandler
  public void onDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerDrop(PlayerDropItemEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerPickup(PlayerPickupItemEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerInventoryInteract(PlayerInteractEvent event) {
    event.setCancelled(true);
  }
}
