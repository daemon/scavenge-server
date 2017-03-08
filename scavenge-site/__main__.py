import datetime
import cherrypy
import config
import database.base as base
#import db.base as base
import mako
import route
import os

if __name__ == "__main__":
  #base.init_databases(config.db_config)
  cherrypy.config.update({
    "environment": "production",
    "log.screen": True
  })
  cherrypy.server.socket_port = 8888
  cherrypy.server.socket_host = "127.0.0.1"
  base.initialize(config.db_config["postgresql"])
  route.mount("/")
