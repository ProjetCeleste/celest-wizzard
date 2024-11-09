package fr.celestoria.wizzard.boards;

import fr.celestoria.api.utils.ConvertTime;
import fr.celestoria.api.utils.board.FastBoard;
import fr.celestoria.wizzard.CelestWizzard;
import fr.celestoria.wizzard.game.WizzardGame;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class FinishedBoard {

  private final Player player;

  public void updateBoard() {

    WizzardGame game = CelestWizzard.getInstance().getGame();
    FastBoard board = new FastBoard(player);
    board.updateTitle("§6§lWIZZARD");

    board.updateLines(
        "§8#" + game.getId() + " ❙ " + new ConvertTime(System.currentTimeMillis()).getDateTimeFormattedBis(),
        "§r",
        "  §f▪ &aPartie terminée !",
        "  §f▪ Kills: §b" + game.getGamePlayers().get(player.getUniqueId()).getKills(),
        "§r",
        "§6play.celestoria.fr");
  }

}
