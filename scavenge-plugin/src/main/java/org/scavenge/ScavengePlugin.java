package org.scavenge;

import com.vexsoftware.votifier.sponge.Votifier;
import org.scavenge.lag.ChunkInfoCommand;
import org.scavenge.lag.NearbyEntitiesCommand;
import org.scavenge.lag.OutOfWorldFixListener;
import org.scavenge.lag.SpawnPreventListener;
import org.scavenge.ranks.FriendCommand;
import org.scavenge.web.auth.RegisterCommand;
import org.scavenge.chat.ChatListener;
import org.scavenge.database.DatabaseManager;
import org.scavenge.economy.base.MoneyCommand;
import org.scavenge.economy.base.PayCommand;
import org.scavenge.economy.base.UserDatabase;
import org.scavenge.economy.base.SCEconomyService;
import org.scavenge.economy.item.ItemDatabase;
import org.scavenge.economy.item.RegisterItemCommand;
import org.scavenge.economy.shop.CancelCommand;
import org.scavenge.economy.shop.ShopCommand;
import org.scavenge.economy.shop.ShopDatabase;
import org.scavenge.economy.shop.ShopPlaceListener;
import org.scavenge.economy.shop.builder.ShopChatManager;
import org.scavenge.economy.trade.TradeCommand;
import org.scavenge.economy.trade.TradeDatabase;
import org.scavenge.web.RemoteActionServer;
import org.scavenge.gui.HookedInventory;
import org.scavenge.inspect.InspectBlockCommand;
import org.scavenge.inspect.InspectItemCommand;
import org.scavenge.web.vote.VoteCommand;
import org.scavenge.web.vote.VoteRewardingListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Plugin(id="scavenge-plugin", name="ScavengeCraft Plugin", version="0.0.1", dependencies={
    @Dependency(id="griefprevention", version="2.3.1"),
    @Dependency(id="votifier", version="2.1")
})
public class ScavengePlugin {
  private DatabaseManager dbManager;
  public static ScavengePlugin instance;
  private RemoteActionServer tradeServer;
  private ItemDatabase itemDatabase;
  private TradeDatabase tradeDatabase;
  private SCEconomyService econService;
  private UserDatabase userDb;

  @Listener
  public void onServerInitialization(GamePreInitializationEvent event) {
    instance = this;
    try {
      this.dbManager = new DatabaseManager("jdbc:postgresql://127.0.0.1/minecraft", "skyfactory", "");
    } catch (PropertyVetoException e) {
      e.printStackTrace();
      System.out.println("Failed to load database manager!");
      return;
    }
    this.dbManager.initDatabase();
    this.registerServices();
    Task.builder().async().execute(() -> {
      try {
        tradeServer = new RemoteActionServer();
        tradeServer.start();
      } catch (IOException | TimeoutException e) {
        e.printStackTrace();
      }
    }).submit(this);
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) {
    this.registerListeners();
    this.registerCommands();
  }

  private void registerListeners() {
    Sponge.getEventManager().registerListeners(this, ShopChatManager.instance);
    Sponge.getEventManager().registerListeners(this, new ChatListener());
    Sponge.getEventManager().registerListeners(this, new ShopPlaceListener());
    Sponge.getEventManager().registerListeners(this, new HookedInventory.Listener());
    Sponge.getEventManager().registerListeners(this, new OutOfWorldFixListener());
    Votifier.getInstance().getListeners().add(new VoteRewardingListener(this.econService));
  }

  private void registerServices() {
    this.userDb = new UserDatabase(this.dbManager);
    this.econService = new SCEconomyService(this.userDb);
    Sponge.getServiceManager().setProvider(this, EconomyService.class, econService);
  }

  private void registerCommands() {
    ShopDatabase shopDatabase = new ShopDatabase(this.dbManager);
    this.itemDatabase = new ItemDatabase(this.dbManager);
    this.tradeDatabase = new TradeDatabase(this.dbManager);
    CommandSpec spec = CommandSpec.builder()
        .description(Text.of("Register for the website."))
        .permission("scavenge.cmd.register")
        .executor(new RegisterCommand()).build();
    // Sponge.getCommandManager().register(this, spec, "register");
    // Sponge.getCommandManager().register(this, TradeCommand.createSpec(this.tradeDatabase), "trade");
    // Sponge.getCommandManager().register(this, ShopCommand.createSpec(itemDatabase, shopDatabase), "shop");
    Sponge.getCommandManager().register(this, RegisterItemCommand.createSpec(itemDatabase), "regitem");
    Sponge.getCommandManager().register(this, CancelCommand.createSpec(), "cancel");
    Sponge.getCommandManager().register(this, InspectItemCommand.createSpec(), "inspectitem");
    Sponge.getCommandManager().register(this, InspectBlockCommand.createSpec(), "inspectblock");
    Sponge.getCommandManager().register(this, PayCommand.createSpec(this.econService), "pay");
    Sponge.getCommandManager().register(this, MoneyCommand.createSpec(this.econService), "money");
    Sponge.getCommandManager().register(this, VoteCommand.createSpec(), "vote");
    Sponge.getCommandManager().register(this, ChunkInfoCommand.createSpec(), "chunkinfo");
    Sponge.getCommandManager().register(this, NearbyEntitiesCommand.createSpec(), "nearbyentities");
    Sponge.getCommandManager().register(this, FriendCommand.createSpec(this.userDb), "addfriend");
  }
}
