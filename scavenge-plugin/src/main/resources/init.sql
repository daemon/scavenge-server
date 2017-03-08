CREATE TABLE IF NOT EXISTS scavenge_user (uuid uuid PRIMARY KEY, name VARCHAR(40) NOT NULL, money float8 DEFAULT 0 NOT NULL
  CHECK (money >= 0), listings_limit INT NOT NULL DEFAULT 10, rank_no SMALLINT NOT NULL DEFAULT 0, n_friends SMALLINT NOT NULL DEFAULT 0,
  expiry NOT NULL TIMESTAMP DEFAULT '2099-01-01 01:01:01');
CREATE INDEX IF NOT EXISTS name_i ON scavenge_user(name);
CREATE TABLE IF NOT EXISTS scavenge_item (id SERIAL PRIMARY KEY, type_id VARCHAR(64) NOT NULL, dv SMALLINT NOT NULL DEFAULT 0,
  display_name VARCHAR(64) NOT NULL UNIQUE, max_stack SMALLINT NOT NULL DEFAULT 64, UNIQUE (type_id, dv));
CREATE INDEX IF NOT EXISTS display_name_i ON scavenge_item USING gin(to_tsvector('english', "display_name"));
CREATE TABLE IF NOT EXISTS scavenge_shop (id SERIAL PRIMARY KEY, owner_uuid uuid REFERENCES scavenge_user(uuid) ON DELETE CASCADE
  NOT NULL, item_id int REFERENCES scavenge_item(id) ON DELETE CASCADE NOT NULL, price float8 NOT NULL DEFAULT 0 CHECK (price >= 0),
  is_buy boolean NOT NULL DEFAULT FALSE, buy_money float8 NOT NULL DEFAULT 0);
CREATE TABLE IF NOT EXISTS scavenge_shop_trade (from_uuid uuid NOT NULL REFERENCES scavenge_user(uuid) ON DELETE CASCADE,
  to_uuid uuid NOT NULL REFERENCES scavenge_user(uuid) ON DELETE CASCADE, quantity INT NOT NULL DEFAULT 0 CHECK (quantity > 0), item_id INT
  NOT NULL REFERENCES scavenge_item(id), total_money float8 NOT NULL, ts TIMESTAMP DEFAULT now());
CREATE TABLE IF NOT EXISTS scavenge_trading_inv (player_uuid uuid NOT NULL REFERENCES scavenge_user(uuid), item_id INT NOT NULL REFERENCES scavenge_item(id),
  quantity INT NOT NULL CHECK (quantity > 0), PRIMARY KEY (player_uuid, item_id));
