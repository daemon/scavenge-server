import database.base as base
import json
import math

class Item:
  def __init__(self, *args):
    base.init_from_row(self, ["id", "type_id", "dv", "display_name", "max_stack"], args)
    self.max_stack = int(self.max_stack)
    self.dv = int(self.dv)
    self.id = int(self.id)

  @staticmethod
  @base.access_point()
  def find(id=None, display_name=None, connection=base.INIT_CONNECTION):
    c = connection.cursor()
    if id:
      c.execute("SELECT * FROM scavenge_item WHERE id=%s", (id,))
    else:
      c.execute("SELECT * FROM scavenge_item WHERE display_name=%s", (display_name,))
    for row in c.fetchall():
      return Item(*row)
    return None

  @staticmethod
  @base.access_point()
  def list_names(connection=base.INIT_CONNECTION):
    c = connection.cursor()
    c.execute("SELECT display_name FROM scavenge_item ORDER BY display_name ASC")
    return [row[0] for row in c.fetchall()]

  def __str__(self):
    return json.dumps(dict(id=self.id, type_id=self.type_id, dv=self.dv, display_name=self.display_name, max_stack=self.max_stack))

  def __repr__(self):
    return self.__str__()

  @staticmethod
  @base.access_point()
  def list_items(query=None, connection=base.INIT_CONNECTION):
    c = connection.cursor()
    words = query.lower().split(' ')
    stmt = "select * from scavenge_item where to_tsvector('english', display_name) @@ to_tsquery('english', {})".format("|| '&' ||".join(['%s'] * len(words)))
    c.execute(stmt, tuple(words))
    return [Item(*row) for row in c.fetchall()]

  def n_stacks(self, quantity):
    return math.ceil(quantity / self.max_stack)

class Inventory:
  def __init__(self, player_uuid):
    self.uuid = player_uuid
    self.contents = {}

  def add_item(self, item, quantity):
    self.contents[item.id] = dict(item=item, quantity=int(quantity))

  def n_empty_slots(self):
    slots = 54
    for (item_id, data) in self.contents.items():
      slots = slots - math.ceil(data["quantity"] / data["item"].max_stack)
    return slots

  @base.access_point()
  def create_item(self, item, quantity, connection=base.INIT_CONNECTION):
    c = connection.cursor()
    stmt = '''INSERT INTO scavenge_trading_inv (player_uuid, item_id, quantity) VALUES (uuid(%s), %s, %s) ON CONFLICT (player_uuid, item_id) DO UPDATE SET
      quantity=scavenge_trading_inv.quantity+%s'''
    c.execute(stmt, (self.uuid, item.id, quantity, quantity))

  @staticmethod
  @base.access_point()
  def find(player_uuid, connection=base.INIT_CONNECTION):
    c = connection.cursor()
    inv = Inventory(player_uuid)
    c.execute("SELECT * FROM scavenge_trading_inv JOIN scavenge_item ON item_id=scavenge_item.id WHERE player_uuid=uuid(%s) FOR UPDATE", (player_uuid,))
    for row in c.fetchall():
      inv.add_item(Item(*row[3:]), row[2])
    return inv