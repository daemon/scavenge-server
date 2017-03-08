from mako.template import Template
from mako.lookup import TemplateLookup
import cherrypy
import database.user
import database.idea
import database.inventory
import database.trade
import datetime
import forum
import json
import mako
import os
import requests
import trade

class config:
  root_dir = os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)), "."))

mako_load_cache = {}
def cached_load_mako(lookup, path):
  try:
    return mako_load_cache[path]
  except KeyError:
    mako_load_cache[path] = lookup.get_template(path).render()
    return mako_load_cache[path]

def route_mako(path):
  def decorator(f):
    def wrapper(*args, **kwargs):
      result = f(*args, **kwargs)
      if isinstance(result, RerouteMako):
        return args[0].template_lookup.get_template(result.template_name).render(data=result.data, user=user_from_request(), request=cherrypy.request, response=cherrypy.response)
      try:
        return f.template.render(data=result, user=user_from_request(), request=cherrypy.request, response=cherrypy.response)
      except AttributeError:
        f.template = args[0].template_lookup.get_template(path)
        return f.template.render(data=result, user=user_from_request(), request=cherrypy.request, response=cherrypy.response)
    return wrapper
  return decorator

class RerouteMako:
  def __init__(self, template_name, data=None):
    self.template_name = template_name
    self.data = data

def json_in(f):
  def merge_dicts(x, y):
    z = x.copy()
    z.update(y)
    return z
  def wrapper(*args, **kwargs):
    cl = cherrypy.request.headers["Content-Length"]
    data = json.loads(cherrypy.request.body.read(int(cl)).decode("utf-8"))
    kwargs = merge_dicts(kwargs, data)
    return f(*args, **kwargs)
  return wrapper

class TradeExecuteEndpoint:
  exposed = True
  @cherrypy.tools.json_out()
  @json_in
  def POST(self, **kwargs):
    user = user_from_request()
    if not user:
      cherrypy.response.status = 403
      return dict(success=False, message="Not logged in")
    uuid = user.uuid
    try:
      trade_id = kwargs["trade_id"]
      quantity = int(kwargs["quantity"])
    except:
      cherrypy.response.status = 400
      return dict(success=False, message="You did not provide the right parameters!")
    try:
      database.trade.Trade.execute_by_id(trade_id, uuid, quantity)
      cherrypy.response.status = 200
      return dict(success=True)
    except ValueError:
      cherrypy.response.status = 400
      return dict(success=False, message="Could not find trade with that ID!")
    except database.trade.MoneyInsufficientError:
      cherrypy.response.status = 400
      return dict(success=False, message="Not enough money!")
    except database.trade.ItemInsufficientError:
      cherrypy.response.status = 400
      return dict(success=False, message="Too much quantity!")
    except database.trade.InventoryInsufficientError:
      cherrypy.response.status = 400
      return dict(success=False, message="Your trading inventory doesn't have enough room!")

class ItemEndpoint:
  exposed = True
  @cherrypy.tools.json_out()
  def GET(self, **kwargs):
    try:
      query = kwargs["query"]
    except:
      cherrypy.response.status = 400
      return dict(success=False, message="You did not provide the right parameters!")
    return dict(success=True, items=[item.__dict__ for item in database.inventory.Item.list_items(query=query)])

class TradeDeleteEndpoint:
  exposed = True
  @cherrypy.tools.json_out()
  @json_in
  def POST(self, **kwargs):
    user = user_from_request()
    if not user:
      cherrypy.response.status = 403
      return dict(success=False, message="Not logged in")
    uuid = user.uuid
    try:
      trade_id = kwargs["trade_id"]
    except:
      cherrypy.response.status = 400
      return dict(success=False, message="You did not provide the right parameters!")
    try:
      database.trade.Trade.delete_by_id(trade_id, uuid)
      cherrypy.response.status = 200
      return dict(success=True)
    except ValueError:
      cherrypy.response.status = 400
      return dict(success=False, message="Could not find trade with that ID!")
    except database.trade.InventoryInsufficientError:
      cherrypy.response.status = 400
      return dict(success=False, message="Your trading inventory doesn't have enough room!")

class TradeEndpoint:
  exposed = True
  @cherrypy.tools.json_out()
  @json_in
  def POST(self, **kwargs):
    user = user_from_request()
    if not user:
      cherrypy.response.status = 403
      return dict(success=False, message="Not logged in")
    uuid = user.uuid
    try:
      display_name = kwargs["display_name"]
      quantity = kwargs["quantity"]
      price = kwargs["price"]
    except:
      cherrypy.response.status = 400
      return dict(success=False, message="You did not provide the right parameters!")
    try:
      database.trade.Trade.create(uuid, quantity, price, display_name=display_name)
      trade.announce("{} is selling {} on the online trading platform for ${} each!".format(user.username, display_name.lower(), (float(price) * 100.0) / 100.0))
      cherrypy.response.status = 200
      return dict(success=True)
    except database.trade.ItemNotFoundError:
      cherrypy.response.status = 400
      return dict(success=False, message="That item is not tradeable!")
    except database.trade.ItemInsufficientError:
      cherrypy.response.status = 400
      return dict(success=False, message="You do not have enough of the item in your virtual trading account.")
    except database.trade.TooManyListingsError as e:
      cherrypy.response.status = 400
      return dict(success=False, message="You're limited to {} listings.".format(e.limit))

def user_from_request(token=None):
  if not token:
    token = cherrypy.request.cookie.get("auth_token")
  user = None
  if token:
    cherrypy.response.cookie["auth_token"] = token
    cherrypy.response.cookie["auth_token"]["max-age"] = 3600 * 24 * 16
    token = token.value
    user = database.user.session_store.get_user(token)
  return user

class Root:
  def __init__(self, template_lookup):
    self.template_lookup = template_lookup
    self.item_names = database.inventory.Item.list_names()

  @cherrypy.expose
  @route_mako("index.mako")
  def index(self):
    if user_from_request():
      raise cherrypy.HTTPRedirect("/dashboard")

  @cherrypy.expose
  @route_mako("tradeable_items.mako")
  def tradeable_items(self):
    return self.item_names

  @cherrypy.expose
  @route_mako("my_listings.mako")
  def my_listings(self):
    if not user_from_request():
      raise cherrypy.HTTPRedirect("/login")
    user = user_from_request()
    trades = database.trade.Trade.list_trades(uuid=user.uuid, order="ASC", order_by="display_name")
    return dict(trades=trades)

  @cherrypy.expose
  @route_mako("create_listing.mako")
  def create_listing(self):
    if not user_from_request():
      raise cherrypy.HTTPRedirect("/login")
    user = user_from_request()
    inv = database.inventory.Inventory.find(user.uuid)
    data = [(item_data["item"].display_name, item_data["quantity"]) for item_data in inv.contents.values()]
    return sorted(data, key=lambda x: x[0])

  @cherrypy.expose
  @route_mako("dashboard.mako")
  def dashboard(self):
    if not user_from_request():
      raise cherrypy.HTTPRedirect("/login")

  @cherrypy.expose
  @route_mako("trade.mako")
  def trade(self, page_no=1, alert_message=None, query=None):
    page_no = int(page_no)
    if page_no < 1:
      page_no = 1
    if not user_from_request():
      raise cherrypy.HTTPRedirect("/login")
    if query:
      trades = database.trade.Trade.list_trades(display_name=query, page_no=page_no, order_by="price", order="ASC")
    else:
      trades = database.trade.Trade.list_trades(page_no=page_no)
    return dict(query=query, trades=trades, page_no=page_no, alert_message=alert_message)

  @cherrypy.expose
  @route_mako("register.mako")
  def register(self, id=None, email=None, password=None):
    if id:
      cherrypy.response.cookie["reg_token"] = id
      cherrypy.response.cookie["reg_token"]["max-age"] = 3600
      try:
        return database.user.session_store.get_register_data(id)
      except:
        return RerouteMako("index.mako", data=dict(alert_message="Expired registration link. Try sending <b>/register</b> again.", alert_title="Link expired"))
    else:
      try:
        token_id = cherrypy.request.cookie["reg_token"].value
        username = database.user.session_store.get_register_data(token_id)["username"]
        uuid = database.user.session_store.get_register_data(token_id)["uuid"]
      except:
        raise cherrypy.HTTPRedirect("/")
      try:
        database.user.User.create(email, username, password, uuid)
        forum.register_user(username, password, email)
        return RerouteMako("index.mako", data=dict(alert_message="Registration success! You may login now.", alert_title="Success"))
      except:
        return RerouteMako("index.mako", data=dict(alert_message="That email is taken.", alert_title="Registration failure"))

  @cherrypy.expose
  @route_mako("login.mako")
  def login(self, username=None, password=None):
    if not username or not password:
      return
    try:
      user = database.user.User.find(username=username)
      if not user:
        raise ValueError("login failure")
      token = user.login(password)
      if not token:
        raise ValueError("login failure")
      cherrypy.response.cookie["auth_token"] = token
      cherrypy.response.cookie["auth_token"]["path"] = "/"
      cherrypy.response.cookie["auth_token"]["max-age"] = 3600 * 24 * 16
      print(token)
      raise cherrypy.HTTPRedirect("/")
    except ValueError:
      return dict(alert_message="Incorrect username/password combination", alert_title="Login failure")

  @cherrypy.expose
  def logout(self):
    user = user_from_request()
    try:
      user.logout(cherrypy.request.cookie["auth_token"].value)
      cherrypy.response.cookie["auth_token"]["path"] = "/"
      cherrypy.response.cookie["auth_token"]["expires"] = 0
      cherrypy.response.cookie["auth_token"]["max-age"] = 0
    except:
      pass
    raise cherrypy.HTTPRedirect("/")

def mount(path):
  template_lookup = TemplateLookup(directories=[os.path.join(config.root_dir, "templates")], input_encoding="utf-8",
    output_encoding="utf-8", encoding_errors="replace")
  cherrypy_conf = {
    '/favicon.ico': {
        'tools.staticfile.on': True,
        'tools.staticfile.filename': os.path.join(config.root_dir, "public", "favicon.ico")
    }
  }
  import time
  a = time.time()
  endpoint = Root(template_lookup)
  #for x in range(0, 1300):
  #  database.idea.Idea.find_all(0, 20)
  #for i in range(30, 60):
  #  database.idea.Idea.create('Short Apple Now ' + str(i * i), 'Test', 1, 100000000000, 'AAPL', 532.7, "NYSE")
  print(time.time() - a)
  rest_conf = {"/": {"request.dispatch": cherrypy.dispatch.MethodDispatcher()}}
  cherrypy.tree.mount(Root(template_lookup), path, cherrypy_conf)
  cherrypy.tree.mount(TradeEndpoint(), "/trade/trade", rest_conf)
  cherrypy.tree.mount(TradeDeleteEndpoint(), "/trade/delete", rest_conf)
  cherrypy.tree.mount(TradeExecuteEndpoint(), "/trade/execute", rest_conf)
  cherrypy.tree.mount(ItemEndpoint(), "/item", rest_conf)
  cherrypy.engine.start()
  cherrypy.engine.block()
