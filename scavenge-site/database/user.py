import base64
import datetime
import hashlib
import json
import random
import redis
import database.base as base
from sqlalchemy import *

class session_store:
  store = redis.StrictRedis(host="127.0.0.1", port=6379, db=0)
  @staticmethod
  def create_token(user):
    token = sec_random_gen(24)
    session_store.store.set(token, user.id, ex=3600 * 24 * 14)
    return token

  @staticmethod
  def get_user(token):
    user_id = session_store.store.get(token)
    if not user_id:
      return None
    return User.find(id=int(user_id))

  @staticmethod
  def delete_token(token):
    session_store.store.delete(token)

  @staticmethod
  def get_register_data(token):
    data = session_store.store.lrange(token, 0, -1)
    return dict(username=data[0].decode("utf-8"), uuid=data[1].decode("utf-8"))

class GameUser:
  def __init__(self, *args):
    base.init_from_row(self, ["uuid", "name", "money", "listings_limit"], args)

  def add_money(self, money, connection=base.INIT_CONNECTION):
    c = connection.cursor()
    stmt = "UPDATE scavenge_user SET money=money+%s WHERE uuid=uuid(%s)"
    c.execute(stmt, (money, self.uuid))

  @staticmethod
  @base.access_point()
  def find(**kwargs):
    connection = kwargs["connection"]
    c = connection.cursor()
    (conditions, params) = base.join_conditions(kwargs, "AND", ["uuid"])
    stmt = "SELECT * FROM scavenge_user WHERE " + conditions + " FOR UPDATE"
    c.execute(stmt, params)
    for row in c.fetchall():
      return GameUser(*row)
    return None

class User:
  def __init__(self, id, username, password, salt, email, uuid):
    self.id = id
    self.username = username
    self.password = password
    self.salt = salt
    self.email = email
    self.uuid = uuid

  def login(self, password):
    if self.password.encode() == sha256x2(password, self.salt):
      return session_store.create_token(self)
    else:
      return None

  def logout(self, token):
    session_store.delete_token(token)

  @staticmethod
  @base.access_point()
  def find(**kwargs):
    connection = kwargs["connection"]
    c = connection.cursor()
    (conditions, params) = base.join_conditions(kwargs, "AND", ["id", "email", "uuid"])
    if "username" in kwargs:
      if conditions:
        conditions = " AND ".join([conditions, "lower(username)=lower(%s)"])
      else:
        conditions = "lower(username)=lower(%s)"
      params = params + (kwargs["username"].lower(),)
    stmt = "SELECT * FROM website_users WHERE " + conditions
    c.execute(stmt, params)
    for row in c.fetchall():
      return User(row[0], row[1], row[2], row[3], row[4], row[5])
    return None

  @staticmethod
  @base.access_point()
  def create(email, username, password, uuid, connection=base.INIT_CONNECTION):
    salt = sec_random_gen(16)
    stmt = "INSERT INTO website_users (username, email, password, salt, uuid) VALUES (%s, %s, %s, %s, uuid(%s))"
    c = connection.cursor()
    c.execute(stmt, (username, email, sha256x2(password, salt).decode("utf-8"), salt, uuid))

def sec_random_gen(length, alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789$!@#$%^&*()"):
  return ''.join(random.SystemRandom().choice(alphabet) for _ in range(length))

def sha256x2(password, salt):
  image1 = ''.join([hashlib.sha256(password.encode()).hexdigest(), salt])
  image2 = base64.b64encode(hashlib.sha256(image1.encode()).digest())
  return image2