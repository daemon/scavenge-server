import datetime
import psycopg2
import psycopg2.errorcodes as errorcodes
import sqlalchemy.exc as errors
from psycopg2.pool import ThreadedConnectionPool
from sqlalchemy import *
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.exc import OperationalError
from sqlalchemy.orm import sessionmaker, relationship, subqueryload, joinedload, Bundle
from threading import local
import time

Base = declarative_base()
engine = create_engine("postgresql+psycopg2://td:password@127.0.0.1/minecraft", 
  pool_recycle=3600, pool_size=1)
#engine = create_engine("sqlite:///file.db")
Base.metadata.bind = engine
Session = sessionmaker(bind=engine)
th_local = local()
INIT_SESSION = None
INIT_TRANSACTION = None
INIT_CONNECTION = None

def orm_access_point(f):
  def wrapper(*args, **kwargs):
    try:
      session = th_local.session
    except AttributeError:
      session = th_local.session = Session()
    if "session" not in kwargs or not kwargs["session"]:
      kwargs["session"] = session    
    return f(*args, **kwargs)
  return wrapper

last_check_time = time.time()
def get_connection():
  global last_check_time
  connection = connection_pool.getconn()
  if time.time() - last_check_time > 1800:
    last_check_time = time.time()
    try:
      c = connection.cursor()
      c.execute("SELECT 1")
    except:
      pass
    if connection.closed:
      return_connection(connection, close=True)
      return get_connection()
  return connection

def return_connection(connection, close=False):
  connection_pool.putconn(connection, close=close)

def access_point(transact=True, retries=3):
  def decorator(f):
    def wrapper(*args, **kwargs):
      tries = 0
      created_connection = False
      if not "connection" in kwargs or kwargs["connection"] is None:
        created_connection = True
        kwargs["connection"] = get_connection()
      try:
        if not transact:
          return f(*args, **kwargs)
        if created_connection:
          while tries < retries:
            try:
              value = f(*args, **kwargs)
              kwargs["connection"].commit()
              return value
            except psycopg2.Error as e:
              kwargs["connection"].rollback()
              if e.pgcode != errorcodes.DEADLOCK_DETECTED:
                raise e
            except:
              kwargs["connection"].rollback()
              raise
        else:
          return f(*args, **kwargs)
      finally:
        if created_connection:
          return_connection(kwargs["connection"])
    return wrapper
  return decorator

def core_access_point(transact=True, retries=3):
  def decorator(f):
    def wrapper(*args, **kwargs):
      tries = 0
      created_connection = False
      if not "connection" in kwargs or kwargs["connection"] is None:
        created_connection = True
        kwargs["connection"] = connection = engine.connect()
      created_transaction = False
      if (not "transaction" in kwargs or kwargs["transaction"] is None) and transact:
        created_transaction = True
        kwargs["transaction"] = transaction = connection.begin()
      if not transact:
        return f(*args, **kwargs)
      try:
        if created_transaction:
          while tries < retries:
            try:
              value = f(*args, **kwargs)
              kwargs["transaction"].commit()
              return value
            except OperationalError:
              kwargs["transaction"].rollback()
        else:
          return f(*args, **kwargs)
      finally:
        if created_connection:
          kwargs["connection"].close()
    return wrapper
  return decorator

def init_from_row(obj, names, args):
  for (name, arg) in zip(names, args):
    setattr(obj, name, arg)

def bulk_insert_str(c, data):
  columns = len(data[0])
  stmt = " ({}) ".format(",".join(["%s"] * columns))
  return ','.join(c.mogrify(stmt, row).decode("utf-8") for row in data)

def subset_dict(dictionary, keys):
  return {k: dictionary[k] for k in dictionary.keys() & keys}

def join_where(statement, table, dictionary):
  for key in dictionary:
    statement = statement.where(getattr(table.c, key) == dictionary[key])
  return statement

def join_conditions(kwargs, condition, params, col_names=None):
  condition = " " + condition + " "
  join_strings = []
  for key, value in kwargs.items():
    try:
      index = params.index(key)
    except ValueError:
      continue
    if value is None:
      string = "ISNULL(%s)" % (col_names[index] if col_names else key)
    else:
      string = "{}=%s".format(col_names[index] if col_names else key)
    join_strings.append(string)
  conditions = condition.join(join_strings)
  found_params = tuple((value for key, value in kwargs.items() if (key in params and value is not None)))
  return (conditions, found_params)

@access_point()
def init_tables(connection=INIT_CONNECTION):
  stmts = ['''CREATE TABLE IF NOT EXISTS website_users (id SERIAL PRIMARY KEY, username VARCHAR(40) NOT NULL, password CHAR(44) NOT NULL, salt CHAR(16) NOT NULL,
    email VARCHAR(32) UNIQUE NOT NULL, uuid uuid NOT NULL UNIQUE REFERENCES scavenge_user(uuid) ON DELETE CASCADE)''',
    '''CREATE INDEX IF NOT EXISTS username_i ON website_users(lower(username))''',
    '''CREATE TABLE IF NOT EXISTS website_trade_listings (id SERIAL PRIMARY KEY, owner_uuid uuid NOT NULL REFERENCES website_users(uuid), item_id INT NOT NULL REFERENCES scavenge_item(id), 
    quantity INT NOT NULL CHECK (quantity > 0), price float8 NOT NULL CHECK (price > 0), buying boolean NOT NULL DEFAULT FALSE, ts TIMESTAMP DEFAULT now())''',
    '''CREATE INDEX IF NOT EXISTS owner_uuid_i ON website_trade_listings (owner_uuid)''',
    '''CREATE INDEX IF NOT EXISTS ts_i ON website_trade_listings (ts)''',
    '''CREATE TABLE IF NOT EXISTS website_trade_history (from_uuid uuid NOT NULL REFERENCES website_users(uuid) ON DELETE CASCADE, to_uuid uuid NOT NULL REFERENCES website_users(uuid)
    ON DELETE CASCADE, item_id INT REFERENCES scavenge_item(id) NOT NULL, quantity INT NOT NULL CHECK (quantity > 0), price float8 NOT NULL CHECK (price > 0), ts TIMESTAMP DEFAULT now() NOT NULL)''',
    '''CREATE INDEX IF NOT EXISTS history_ts_i ON website_trade_history(ts)''',
    '''CREATE INDEX IF NOT EXISTS history_item_id_i ON website_trade_history(item_id)''']
  c = connection.cursor()
  for stmt in stmts:
    c.execute(stmt) 

def initialize(conf):
  global connection_pool
  connection_pool = ThreadedConnectionPool(3, 5, **conf)
  init_tables()
