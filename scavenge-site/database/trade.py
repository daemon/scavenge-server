import database.base as base
import database.user as user
import database.inventory as inventory
import trade

class ItemNotFoundError(Exception):
  pass

class ItemInsufficientError(Exception):
  pass

class TooManyListingsError(Exception):
  def __init__(self, message, limit):
    super(TooManyListingsError, self).__init__(message)
    self.limit = limit

class MoneyInsufficientError(Exception):
  pass

class InventoryInsufficientError(Exception):
  pass

class Trade:
  def __init__(self, *args):
    base.init_from_row(self, ["id", "owner_uuid", "item_id", "quantity", "price", "buying", "ts"], args)

  @base.access_point()
  def execute(self, other_uuid, quantity, connection=base.INIT_CONNECTION):
    trade.lock_uuid(self.owner_uuid)
    trade.lock_uuid(other_uuid)
    try:
      other_user = user.GameUser.find(uuid=other_uuid, connection=connection)
      owner_user = user.GameUser.find(uuid=self.owner_uuid, connection=connection)
      # TODO support buying listings
      if float(other_user.money) < float(self.price) * quantity and other_uuid != self.owner_uuid:
        raise MoneyInsufficientError
      if quantity > int(self.quantity):
        raise ItemInsufficientError
      item = inventory.Item.find(id=self.item_id, connection=connection)
      inv = inventory.Inventory.find(other_uuid, connection=connection)
      if item.n_stacks(quantity) > inv.n_empty_slots():
        raise InventoryInsufficientError
      if quantity == int(self.quantity):
        self.delete(connection=connection)
      else:
        self.add_quantity(-quantity, connection=connection)
      owner_user.add_money(float(self.price) * quantity, connection=connection)
      other_user.add_money(-float(self.price) * quantity, connection=connection)
      inv.create_item(item, quantity, connection=connection)
    finally:
      trade.unlock_uuid(other_uuid)
      trade.unlock_uuid(self.owner_uuid)

  @base.access_point()
  def delete(self, connection=base.INIT_CONNECTION):
    c = connection.cursor()
    c.execute("DELETE FROM website_trade_listings WHERE id=%s", (self.id,))

  @base.access_point()
  def add_quantity(self, quantity, connection=base.INIT_CONNECTION):
    c = connection.cursor()
    c.execute("UPDATE website_trade_listings SET quantity=quantity+%s WHERE id=%s", (quantity, self.id))

  @staticmethod
  @base.access_point()
  def find(**kwargs):
    connection = kwargs["connection"]
    (conditions, params) = base.join_conditions(kwargs, "AND", ["id", "item_id", "owner_uuid"])
    c = connection.cursor()
    c.execute("SELECT * FROM website_trade_listings WHERE " + conditions + " ORDER BY ts DESC FOR UPDATE", params)
    return [Trade(*row) for row in c.fetchall()]

  @staticmethod
  @base.access_point()
  def delete_by_id(id, uuid, connection=base.INIT_CONNECTION):
    trades = Trade.find(id=id, owner_uuid=uuid, connection=connection)
    if not trades:
      raise ValueError
    trades[0].execute(uuid, trades[0].quantity, connection=connection)

  @staticmethod
  @base.access_point()
  def execute_by_id(id, other_uuid, quantity, connection=base.INIT_CONNECTION):
    trades = Trade.find(id=id, connection=connection)
    if not trades:
      raise ValueError
    trades[0].execute(other_uuid, quantity, connection=connection)

  @staticmethod
  @base.access_point()
  def list_trades(page_no=0, page_size=10, item_id=None, uuid=None, display_name=None, order_by="ts", order="DESC", connection=base.INIT_CONNECTION):
    page_no = page_no - 1
    if display_name:
      item = inventory.Item.find(display_name=display_name)
      if item:
        item_id = item.id
      else:
        return []
    stmt = '''SELECT website_trade_listings.*, username, display_name FROM website_trade_listings JOIN website_users ON 
      website_users.uuid=owner_uuid JOIN scavenge_item ON scavenge_item.id=item_id '''
    if item_id:
      stmt += " WHERE website_trade_listings.item_id=%s "
    elif uuid:
      stmt += " WHERE owner_uuid=%s "
    stmt += '''ORDER BY {} {} LIMIT %s OFFSET %s'''.format(order_by, order)
    c = connection.cursor()
    if item_id:
      c.execute(stmt, (item_id, page_size, page_no * page_size))
    elif uuid:
      c.execute(stmt, (uuid, 50, 0))
    else:
      c.execute(stmt, (page_size, page_no * page_size))
    trades = []
    rows = c.fetchall()
    for row in rows:
      trades.append(dict(trade=Trade(*row[0:6]), username=row[7], item_name=row[8]))
    return trades

  @staticmethod
  @base.access_point()
  def create(owner_uuid, quantity, price, buying=False, display_name=None, item_id=None, connection=base.INIT_CONNECTION):
    trade.lock_uuid(owner_uuid)
    try:
      # TODO combine statements in joins
      if not item_id:
        item = inventory.Item.find(display_name=display_name.lower(), connection=connection)
        if not item:
          raise ItemNotFoundError
        item_id = item.id
      stmt = "SELECT * FROM scavenge_trading_inv WHERE item_id=%s AND player_uuid=uuid(%s) FOR UPDATE"
      c = connection.cursor()
      c.execute(stmt, (item_id, owner_uuid))
      rows = c.fetchall()
      if not rows:
        raise ItemInsufficientError
      for row in rows:
        item_quantity = row[2]
      if int(item_quantity) < int(quantity):
        raise ItemInsufficientError
      stmt = "SELECT COUNT(*) FROM website_trade_listings WHERE owner_uuid=uuid(%s)"
      c.execute(stmt,(owner_uuid,))
      count = c.fetchone()[0]
      stmt = "SELECT listings_limit FROM scavenge_user WHERE uuid=uuid(%s)"
      c.execute(stmt, (owner_uuid,))
      limit = c.fetchone()[0]
      if int(count) >= int(limit):
        raise TooManyListingsError("Too many listings", limit)
      if int(item_quantity) == int(quantity):
        stmt = "DELETE FROM scavenge_trading_inv WHERE item_id=%s AND player_uuid=uuid(%s)"
        c.execute(stmt, (item_id, owner_uuid))
      else:
        stmt = "UPDATE scavenge_trading_inv SET quantity=quantity-%s WHERE item_id=%s AND player_uuid=uuid(%s)"
        c.execute(stmt, (quantity, item_id, owner_uuid))
      stmt = "INSERT INTO website_trade_listings (owner_uuid, item_id, quantity, price, buying) VALUES (%s, %s, %s, %s, %s) RETURNING id, ts"
      c.execute(stmt, (owner_uuid, item_id, quantity, price, buying))
      ret = c.fetchone()
      return Trade(*(ret[0], owner_uuid, item_id, quantity, price, buying, ret[1]))
    finally:
      trade.unlock_uuid(owner_uuid)
