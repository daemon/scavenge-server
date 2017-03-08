package org.scavenge.lag;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ChunkInfoCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = (Player) src;
    List<Chunk> chunks = new LinkedList<>();
    for (Chunk chunk : player.getWorld().getLoadedChunks())
      chunks.add(chunk);
    Collections.sort(chunks, (c1, c2) -> c2.getEntities().size() - c1.getEntities().size());
    for (int i = 0; i < Math.min(10, chunks.size()); ++i) {
      Chunk chunk = chunks.get(i);
      Vector3i loc = chunk.getPosition();
      player.sendMessage(Text.of(TextColors.GOLD, "(", loc.getX() * 16, ", ", loc.getZ() * 16, ") | Entities: ", TextColors.AQUA, chunk.getEntities().size()));
    }
    return CommandResult.empty();
  }

  public static CommandSpec createSpec() {
    return CommandSpec.builder().description(Text.of("Finds laggy chunks"))
        .executor(new ChunkInfoCommand())
        .permission("scavenge.cmd.chunkinfo")
        .build();
  }
}
