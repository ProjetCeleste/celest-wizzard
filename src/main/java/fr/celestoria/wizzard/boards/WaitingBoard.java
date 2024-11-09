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
public class WaitingBoard {

  private final Player player;

  public void updateBoard() {

    WizzardGame game = CelestWizzard.getInstance().getGame();
    FastBoard board = new FastBoard(player);
    board.updateTitle("§6§lWIZZARD");

    board.updateLines(
        "§8#" + game.getId() + " ❙ " + new ConvertTime(System.currentTimeMillis()).getDateFormatted(),
        "§r",
        "  §f▪ Statut: §eEn attente",
        "  §f▪ Joueurs: §b" + game.countGamePlayers() + "§f/§a" + game.getMaxPlayers(),
        "§r",
        "  §f▪ §7Attente de joueurs...",
        "§r",
        "§6play.celestoria.fr");
  }

}
