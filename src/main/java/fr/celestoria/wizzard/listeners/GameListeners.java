package fr.celestoria.wizzard.listeners;

import fr.celestoria.api.CelestAPI;
import fr.celestoria.api.gameapi.Leaderboard;
import fr.celestoria.api.gameapi.host.GameDataProvider;
import fr.celestoria.api.utils.ActionBar;
import fr.celestoria.api.utils.Cooldown;
import fr.celestoria.api.utils.ParticleAPI;
import fr.celestoria.api.utils.ParticleEffect;
import fr.celestoria.api.utils.inv.ItemBuilder;
import fr.celestoria.api.utils.xutils.XSound;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.game.WizzardGame;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class GameListeners implements Listener {

  // ========================================================================
  // FIELDS
  // ========================================================================

  private final Cooldown launchCooldown = new Cooldown(CelestWizzard.getInstance(), new GameDataProvider(CelestAPI.getInstance().getServerDisplayName()).getDataFromRedis().getInt("cooldown"));

  private final double SHOOT_STEP = 0.3D; // Précision de tir
  private final int SHOOT_MAX_CHECKS =
      150; // Distance de tir = SHOOT_MAX_CHECKS * SHOOT_STEP
  private final double SHOOT_RADIUS = 3D; // Hitbox du tir

  // ========================================================================
  // METHODS
  // ========================================================================

  private void launchTrail(Player player) {
    final Location loc = player.getEyeLocation().clone();
    final Vector dir = loc.getDirection().normalize().multiply(SHOOT_STEP);
    final Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
    Set<UUID> hurtedPlayers = new HashSet<>(0);
    Block lastBlock = null;
    int doubleKill = 0;

    for (int i = 0; i < SHOOT_MAX_CHECKS; i++) {
      loc.add(dir);
      Block block = loc.getBlock();
      if (lastBlock == null || !lastBlock.equals(block)) {
        if (block.getType() != Material.AIR) {
          break;
        }
        lastBlock = block;
      }
      for (Player nearPlayers : onlinePlayers) {
        if (nearPlayers.getLocation().distanceSquared(loc) <= SHOOT_RADIUS
            && nearPlayers != player
            && !nearPlayers.isDead()
            && !hurtedPlayers.contains(nearPlayers.getUniqueId())) {

          if (nearPlayers.getInventory().getChestplate() != null
              && nearPlayers.getInventory().getChestplate().getType().equals(Material.DIAMOND_CHESTPLATE)) {

            nearPlayers.getInventory().setChestplate(null);
            XSound.ITEM_SHIELD_BREAK.play(nearPlayers);
            continue;
          }

          hurtedPlayers.add(nearPlayers.getUniqueId());
          doubleKill++;
          CelestWizzard.getInstance().getGame().updateScoreboard(player);

          if (doubleKill >= 2) {
            Bukkit.broadcastMessage(
                "§e§lWOW ! §d"
                    + player.getName()
                    + " §evient de faire §d§l"
                    + doubleKill
                    + " kills d'un coup §e!");
            player.getInventory().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE));
            XSound.ENTITY_PLAYER_LEVELUP.play(player);
          }
        }
      }

      if (i > 2) {
        player.getWorld().playEffect(loc, Effect.COLOURED_DUST, 5);
      }
    }

    if (!hurtedPlayers.isEmpty()) {
      for (UUID hurtedPlayer : hurtedPlayers) {
        kill(hurtedPlayer, player);
      }
    }
  }

  private void kill(UUID victimUUID, Player killer) {
    WizzardGame game = CelestWizzard.getInstance().getGame();
    Player victim = Bukkit.getPlayer(victimUUID);
    UUID killerUUID = killer.getUniqueId();

    ActionBar.sendActionBar(killer, "§7Vous avez tué §b" + victim.getName() + "§7.");
    ActionBar.sendActionBar(victim, "§7Vous avez été tué par §b" + killer.getName() + "§7.");

    new ParticleAPI(victim.getLocation().clone().add(0, 1, 0), ParticleEffect.FIREWORKS_SPARK, game.getPlayers().toArray(new Player[0]))
        .spawnSphere(2, 100, 0.1f);
    XSound.ENTITY_FIREWORK_ROCKET_BLAST.play(killer);

    game.getGamePlayers().get(victimUUID).newDeath();
    game.getGamePlayers().get(killerUUID).newKill();

    game.getGamePlayers().get(victimUUID).secretTeleport(game.getFarSpawn());
    XSound.ENTITY_VILLAGER_DEATH.play(victim);

    Leaderboard leaderboard = new Leaderboard(game.getGamePlayers());
    if (leaderboard.getTopGamePlayers(1).get(0).getValue() >= 30) {
      game.endGame();
      return;
    }

    game.updateScoreboard(victim);
    game.updateScoreboard(killer);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action action = event.getAction();

    if (action.name().startsWith("RIGHT_") && event.getMaterial().equals(Material.STICK)) {
      if (launchCooldown.hasCooldown(player)) {
        return;
      }

      launchCooldown.putInCooldown(player);
      this.launchTrail(player);
    }

    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerInvClick(InventoryClickEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent event) {
    event.setCancelled(true);
  }
}
