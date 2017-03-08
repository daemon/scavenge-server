package org.scavenge.gui;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.net.MalformedURLException;
import java.net.URL;

public class TextLink {
  private final String text;

  public TextLink(String text) {
    this.text = text;
  }

  public Text build() throws MalformedURLException {
    URL url = new URL(this.text);
    return Text.builder(this.text).style(TextStyles.UNDERLINE).color(TextColors.AQUA).onClick(TextActions.openUrl(url)).build();
  }
}
