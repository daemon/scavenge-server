import database.base as base
import datetime
from sqlalchemy import *

hashtag = Table("hashtag", base.Base.metadata,
  Column("id", Integer, primary_key=True),
  Column("tag", String(32), unique=True, nullable=False))

idea_hashtag_assoc = Table("idea_hashtag_assoc", base.Base.metadata,
  Column("idea_id", Integer, ForeignKey("idea.id", ondelete="CASCADE")),
  Column("hashtag_id", Integer, ForeignKey("hashtag.id", ondelete="CASCADE")))

idea = Table("idea", base.Base.metadata,
  Column("id", Integer, primary_key=True),
  Column("title", String(64), nullable=False),
  Column("body", String(32768), nullable=False),
  Column("upvotes", Integer, nullable=False),
  Column("views", Integer, nullable=False),
  Column("author_id", Integer, ForeignKey("user.id", ondelete="CASCADE"), nullable=False, index=True),
  Column("creation_date", DateTime, default=datetime.datetime.now(), nullable=False, index=True),
  Column("cap", BigInteger),
  Column("symbol", String(24), nullable=False, index=True),
  Column("price", Float, nullable=False),
  Column("exchange", String(16), nullable=False))

class Hashtag:
  def __init__(self, hashtag_id, tag):
    self.id = hashtag_id
    self.tag = tag

  @staticmethod
  @base.access_point()
  def create(tag, connection=base.INIT_CONNECTION):
    c = connection.cursor()
    c.execute("INSERT INTO hashtag (tag) VALUES (%s) RETURNING id", (tag,))
    return Hashtag(c.fetchone()[0], tag)

  @staticmethod
  @base.access_point()
  def find(**kwargs):
    connection = kwargs["connection"]
    c = connection.cursor()
    if "tags" in kwargs:
      stmt = "SELECT * FROM hashtag WHERE tag IN %s"
      c.execute(stmt, (tuple(kwargs["tags"]),))
      rows = c.fetchall()
      hashtags = {}
      for row in rows:
        hashtags[row[1]] = row[0]
      return hashtags
    (conditions, params) = base.join_conditions(kwargs, "AND", ["id", "tag"])
    stmt = "SELECT * FROM hashtag WHERE " + conditions
    c.execute(stmt, params)
    rows = c.fetchall()
    for row in rows:
      return Hashtag(row[0], row[1])
    return None

class Idea:
  def __init__(self, *args):
    self.hashtags = []
    base.init_from_row(self, ["id", "title", "body", "upvotes", "author_id", "creation_date", 
      "cap", "symbol", "price", "exchange", "username"], args)

  def json(self):
    json_dict = self.__dict__.copy()
    json_dict["creation_date"] = str(json_dict["creation_date"])
    return json_dict

  @staticmethod
  @base.access_point()
  def find(**kwargs):
    connection = kwargs["connection"]
    (conditions, params) = base.join_conditions(kwargs, "AND", ["id", "title", "author_id"], ["idea.id", "title", "author_id"])
    stmt = "SELECT idea.*, username FROM idea JOIN \"user\" ON idea.author_id=\"user\".id WHERE " + conditions
    c = connection.cursor()
    c.execute(stmt, params)
    ideas = [Idea(*row) for row in c.fetchall()]
    return ideas

  @staticmethod
  @base.access_point()
  def find_all(page_no, page_size, connection=base.INIT_CONNECTION):
    stmt = "SELECT idea.*, username FROM idea JOIN \"user\" ON idea.author_id=\"user\".id ORDER BY creation_date DESC OFFSET %s LIMIT %s"
    c = connection.cursor()
    c.execute(stmt, (page_no * page_size, page_size))
    ideas = [Idea(*row) for row in c.fetchall()]
    return ideas

  @staticmethod
  @base.access_point()
  def create(title, body, author_id, cap, symbol, price, exchange, hashtags=[], connection=base.INIT_CONNECTION):
    if not isinstance(hashtags, set):
      hashtags = set(hashtags)
    if hashtags:
      found_tags = Hashtag.find(tags=hashtags, connection=connection)
      missing_tags = hashtags - found_tags.keys()
      for tag in missing_tags:
        tag = Hashtag.create(tag, connection=connection)
        found_tags[tag.tag] = tag.id
    c = connection.cursor()
    stmt = '''INSERT INTO idea (title, body, upvotes, views, author_id, creation_date, cap, symbol, price, exchange
      ) VALUES (%s, %s, 0, 0, %s, now(), %s, %s, %s, %s) RETURNING id'''
    c.execute(stmt, (title, body, author_id, cap, symbol, price, exchange))
    idea_id = c.fetchone()[0]
    if hashtags:
      idea_hashtag = [(idea_id, hashtag_id) for hashtag_id in found_tags.values()]
      stmt = "INSERT INTO idea_hashtag_assoc (idea_id, hashtag_id) VALUES " + base.bulk_insert_str(c, idea_hashtag)
      c.execute(stmt)
