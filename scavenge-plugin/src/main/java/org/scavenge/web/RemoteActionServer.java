package org.scavenge.web;

import com.rabbitmq.client.*;
import org.scavenge.ScavengePlugin;
import org.scavenge.announce.AnnounceCallback;
import org.scavenge.economy.trade.net.LockInventoryCallback;
import org.scavenge.economy.trade.net.UnlockInventoryCallback;
import org.spongepowered.api.scheduler.Task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RemoteActionServer {
  private final ConnectionFactory factory;
  private final Connection connection;
  private final Channel channel;
  private static final String QUEUE_NAME = "trade";
  private final Map<String, Callback> callbacks = new HashMap<>();

  public RemoteActionServer() throws IOException, TimeoutException {
    this.factory = new ConnectionFactory();
    this.factory.setHost("localhost");
    this.connection = factory.newConnection();
    this.channel = connection.createChannel();
    this.addCallback("lock", new LockInventoryCallback());
    this.addCallback("unlock", new UnlockInventoryCallback());
    this.addCallback("announce", new AnnounceCallback());
  }

  public RemoteActionServer addCallback(String prefix, Callback callback) {
    this.callbacks.put(prefix, callback);
    return this;
  }

  public void start() throws IOException {
    this.channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    this.channel.basicQos(1);
    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        Task.builder().execute(() -> {
          try {
            for (String key : callbacks.keySet())
              if (message.startsWith(key))
                callbacks.get(key).call(message.substring(key.length()));
          } finally {
            try {
              channel.basicAck(envelope.getDeliveryTag(), false);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }).submit(ScavengePlugin.instance);
      }
    };
    channel.basicConsume(QUEUE_NAME, false, consumer);
  }

  @FunctionalInterface
  public interface Callback {
    void call(String data);
  }
}
