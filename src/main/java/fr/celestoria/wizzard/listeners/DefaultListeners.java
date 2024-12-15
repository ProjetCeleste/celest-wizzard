package fr.celestoria.wizzard.listeners;

import fr.celestoria.api.database.Account;
import fr.celestoria.api.database.AccountProvider;
import fr.celestoria.api.gameapi.Status;
import fr.celestoria.api.gameapi.StatusChangeEvent;
import fr.celestoria.api.utils.PlayerUtils;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.game.WizzardGame;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
@RequiredArgsConstructor
public class DefaultListeners implements Listener {

  @EventHandler
  public void onStatusChange(StatusChangeEvent event) {
    WizzardGame game = CelestWizzard.getInstance().getGame();

    game.updateScoreboards();
    Status status = event.getStatus();
    switch (status) {
      case WAITING_FOR_PLAYERS:
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          onlinePlayer.sendMessage(
              game.getPrefix() + "§cDémarrage annulé, il n'y a plus assez de joueurs !");
        }
        break;
      case IN_GAME:
        List<Location> locs = game.getSpawns();
        for (UUID uuid : game.getGamePlayers().keySet()) {
          Player player = Bukkit.getPlayer(uuid);
          if (player == null) {
            return;
          }
          PlayerUtils.cleanPlayer(player);
          game.getGamePlayer(player).secretTeleport(locs.get(0));
          locs.remove(0);
        }
        break;
      default:
        break;
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    WizzardGame game = CelestWizzard.getInstance().getGame();
    event.setJoinMessage(null);

    Player player = event.getPlayer();
    game.handleLogin(player);

    game.updateScoreboard(player);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    event.setQuitMessage(null);

    CelestWizzard.getInstance().getGame().handleLogout(event.getPlayer());
  }

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    Status status = CelestWizzard.getInstance().getGame().getGameStatus();
    if (status == Status.FINISHED && event.getMessage().equalsIgnoreCase("gg")) {
        Account account = new AccountProvider(event.getPlayer().getUniqueId()).getAccount();
        if (account.isSubscriptionActive()) {
          event.setMessage("§e§lGG");
        }
      }

    event.setFormat("%s§f: %s");
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
      event.setCancelled(true);
    }
  }
}
