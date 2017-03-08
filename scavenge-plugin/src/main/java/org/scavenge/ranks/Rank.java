package org.scavenge.ranks;

import com.google.common.base.Charsets;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;

public enum Rank {
  CADET(TextColors.YELLOW, new byte[] {-2, -1, 38, -118}, "Cadet", 0),
  ENSIGN(TextColors.YELLOW, new byte[]{-2, -1, 38, -117}, "Ensign", 7500),
  LIEUTENANT(TextColors.YELLOW, new byte[] {-2, -1, 38, -116}, "Lieutenant", 15000),
  COMMANDER(TextColors.GOLD, new byte[] {-2, -1, 38, -113}, "Commander", 30000),
  CAPTAIN(TextColors.GOLD,  new byte[] {-2, -1, 38, 48}, "Captain", 60000),
  ADMIRAL(TextColors.GOLD,  new byte[] {-2, -1, 38, 55}, "Admiral", 120000),
  FLEET_ADMIRAL(TextColors.RED,  new byte[] {-2, -1, 38, -99}, "Fleet Admiral", 250000);
  public final TextColor color;
  public final byte[] rank;
  public final String name;
  public final double cost;

  Rank(TextColor color, byte[] rank, String name, double cost) {
    this.color = color;
    this.rank = rank;
    this.name = name;
    this.cost = cost;
  }

  public static Rank find(Text text) {
    for (Rank rank : Rank.values()) {
      System.out.println(Arrays.toString(rank.rank) + " : " + Arrays.toString(text.toPlain().getBytes(Charsets.UTF_16)) + " " + text.getFormat().getColor().toString());
      if (text.getFormat().getColor().equals(rank.color) && Arrays.equals(text.toPlain().getBytes(Charsets.UTF_16), rank.rank))
        return rank;
    }
    return null;
  }
}
