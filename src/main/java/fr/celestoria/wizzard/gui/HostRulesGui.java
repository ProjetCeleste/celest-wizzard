package fr.celestoria.wizzard.gui;

import fr.celestoria.api.CelestAPI;
import fr.celestoria.api.gameapi.Game;
import fr.celestoria.api.gameapi.host.GameData;
import fr.celestoria.api.gameapi.host.GameDataProvider;
import fr.celestoria.api.gameapi.host.HostSettingsGui;
import fr.celestoria.api.utils.inv.ItemBuilder;
import fr.celestoria.api.utils.xutils.XSound;
import fr.celestoria.wizzard.CelestWizzard;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HostRulesGui extends HostSettingsGui {

  GameData data = new GameDataProvider(CelestAPI.getInstance().getServerDisplayName()).getDataFromRedis();
  int cooldown = data.getInt("cooldown");

  public HostRulesGui() {
    super();

    setItem(20, new ItemBuilder(Material.WATCH)
        .setName("§f▪ §eTemps de jeu")
        .setLore("cutlines:§7Modifiez le temps de la partie", "", "  §7❙ Actuel: " + "5 minutes"));

    setItem(22, new ItemBuilder(Material.BARRIER)
        .setName("§f▪ §eCooldown de la baguette")
        .setLore("cutlines:§7Modifiez le temps de rechargement entre deux tirs de la baguette.",
            "",
            "  §7❙ Actuel: " + String.format("%.2f", (double) cooldown / 1000) + "s",
            "",
            "§6§l» §eClique gauche: §a+0.25s",
            "§6§l» §eClique droit: §c-0.25s"));


  }

  @Override
  protected void onClick(InventoryClickEvent event) {
    super.onClick(event);
    Player player = (Player) event.getWhoClicked();

    switch (event.getSlot()) {
      case 20:
        if (event.getClick().isLeftClick()) {
          if (cooldown >= 10000) {
            XSound.ENTITY_VILLAGER_NO.play(player);
            break;
          }
          data.set("cooldown", cooldown + 250);
        } else if (event.getClick().isRightClick()) {
          if (cooldown <= 0) {
            XSound.ENTITY_VILLAGER_NO.play(player);
            break;
          }
          data.set("cooldown", cooldown - 250);
        }
        break;
      default:break;
    }

    Game game = CelestWizzard.getInstance().getGame();
    game.openRulesGui((Player) event.getWhoClicked());
    game.updateScoreboards();
  }
}
