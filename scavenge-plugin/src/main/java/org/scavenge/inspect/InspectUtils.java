package org.scavenge.inspect;

import org.spongepowered.api.data.DataContainer;

public class InspectUtils {
  public static void printContainer(DataContainer container) {
    container.getValues(true).forEach((dataQuery, o) -> {
      System.out.println(dataQuery.toString() + " : " + o.toString());
    });
  }
}
