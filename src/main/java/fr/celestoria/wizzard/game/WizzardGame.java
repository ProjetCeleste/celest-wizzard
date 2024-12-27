package fr.celestoria.wizzard.game;

import fr.celestoria.api.CelestAPI;
import fr.celestoria.api.gameapi.Game;
import fr.celestoria.api.gameapi.GamePlayer;
import fr.celestoria.api.gameapi.GameType;
import fr.celestoria.api.gameapi.Leaderboard;
import fr.celestoria.api.gameapi.host.GameData;
import fr.celestoria.api.gameapi.host.GameDataProvider;
import fr.celestoria.api.utils.ConvertTime;
import fr.celestoria.api.utils.Titles;
import fr.celestoria.api.utils.inv.ItemBuilder;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.countdowns.EndCountdown;
import fr.celestoria.wizzard.countdowns.PreStartingCountdown;
import fr.celestoria.wizzard.countdowns.StartingCountdown;
import fr.celestoria.wizzard.gui.HostRulesGui;
import fr.celestoria.wizzard.listeners.DefaultListeners;
import fr.celestoria.wizzard.listeners.GameListeners;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class WizzardGame extends Game {

  public WizzardGame() {
    super(GameType.WIZZARD);
    initCountdowns(
        new PreStartingCountdown(),
        new StartingCountdown(),
        new LoopScheduler(),
        new EndCountdown());

    GameData data = new GameDataProvider(CelestAPI.getInstance().getServerDisplayName()).getDataFromRedis();
    data.set("cooldown", 500);

    setHostSettingsGui(HostRulesGui.class);

    Bukkit.getPluginManager().registerEvents(new DefaultListeners(), CelestWizzard.getInstance());
  }

  public void startWizardGame() {
    super.startGame(new GameListeners());
    ItemBuilder item = new ItemBuilder(Material.STICK).setName("§dBaguette magique").setLore("§7Avadaaaaa...").addUnsafeEnchant(Enchantment.DURABILITY).addFlag(
        ItemFlag.HIDE_ENCHANTS);
    for (UUID uuid : getGamePlayers().keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      player.setScoreboard(getScoreboard());
      player.getInventory().setItem(0, item);
      getScoreboard().getTeam("default").addPlayer(player);
      player.getInventory().setHeldItemSlot(0);
      Titles.sendTitle(player, 10, 40, 10, "§a§lDébut de la partie", "§fBonne chance !");
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
                "  §f▪ Carte: §a" + CelestWizzard.getInstance().getGame().getGameWorldName(),
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
                "  §f▪ Statut: §aLancement...",
                "§r",
                "  §f▪ Carte: §a" + CelestWizzard.getInstance().getGame().getGameWorldName(),
                "  §f▪ Joueurs: §b" + countGamePlayers() + "§f/§a" + getMaxPlayers(),
                "§r",
                "  §f▪ §fDémarrage dans: §a" + ConvertTime.formatTime(getCurrentTask().getTimer()),
                "§r",
                "§6play.celestoria.fr");
        break;
      case IN_GAME:
        Leaderboard leaderboard = new Leaderboard(gamePlayers);
        List<Entry<UUID, Integer>> board = leaderboard.getTopGamePlayers(3);
        gamePlayer
            .getBoard()
            .updateLines(
                "§8#"
                    + getId()
                    + " ❙ "
                    + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
                "§r",
                "  §f▪ Carte: §a" + CelestWizzard.getInstance().getGame().getGameWorldName(),
                "§r ",
                "  §f▪ Temps restant: §e"
                    + ConvertTime.formatTime(((LoopScheduler) getCurrentTask()).getTimeLeft()),
                "  §f▪ Kill(s): §b" + gamePlayer.getKills(),
                "  §f▪ Mort(s): §c" + gamePlayer.getDeaths(),
                "  §f▪ Ratio: §a" + gamePlayer.getRatio(),
                "§r",
                "§fClassement:",
                "  §f▪ §b" + Bukkit.getPlayer(board.get(0).getKey()).getName() + " §7(" + board.get(0).getValue() + ")",
                "  §f▪ §b" + Bukkit.getPlayer(board.get(1).getKey()).getName() + " §7(" + board.get(1).getValue() + ")",
                "  §f▪ §b" + (getGamePlayers().keySet().size() >= 3 ? Bukkit.getPlayer(board.get(2).getKey()).getName() + " §7(" + board.get(2).getValue() + ")" : "N/A"),
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
                "  §f▪ Carte: §a" + CelestWizzard.getInstance().getGame().getGameWorldName(),
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
