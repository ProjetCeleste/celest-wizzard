package fr.celestoria.wizzard.powerups;

import fr.celestoria.api.utils.inv.ItemBuilder;
import fr.celestoria.api.utils.xutils.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PowerUpType {
  SHORT_SHOOT("Réduction délai de tir", new ItemBuilder(XMaterial.CLOCK.parseMaterial())),
  SPEED("Réduction délai de tir", new ItemBuilder(XMaterial.CLOCK.parseMaterial())),
  INVISIBILITY("Réduction délai de tir", new ItemBuilder(XMaterial.CLOCK.parseMaterial()));

  private final String displayName;
  private final ItemBuilder item;
}
