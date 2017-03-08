package org.scavenge.web.auth;

import org.spongepowered.api.entity.living.player.Player;
import redis.clients.jedis.Jedis;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

public class RegistrationTokenStore {
  private final Jedis jedis;
  private final Random random;

  public RegistrationTokenStore() {
    this.jedis = new Jedis();
    this.random = new Random();
  }

  public String token(Player player) {
    UUID uuid = player.getUniqueId();
    String token = this.jedis.get(uuid.toString());
    if (token == null) {
      token = new BigInteger(64, this.random).toString(16);
      this.jedis.setex(uuid.toString(), 3600, token);
      this.jedis.lpush(token, uuid.toString());
      this.jedis.lpush(token, player.getName());
      this.jedis.expire(token, 3600);
    }
    return token;
  }
}
