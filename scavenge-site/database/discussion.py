import database.base as base
from sqlalchemy import *

reply = Table("reply", base.Base.metadata,
  Column("id", Integer, primary_key=True),
  Column("body", String(8192), nullable=False),
  Column("creation_date", DateTime, nullable=False, index=True),
  Column("author_id", Integer, ForeignKey("user.id", ondelete="CASCADE"), nullable=False))

reply_idea_assoc = Table("reply_idea_assoc", base.Base.metadata,
  Column("reply_id", Integer, ForeignKey("reply.id", ondelete="CASCADE")),
  Column("idea_id", Integer, ForeignKey("idea.id", ondelete="CASCADE")))

class Reply:
  def __init__(self, *args):
    base.init_from_row(self, ["id", "body", "creation_date", "author_id", "idea_id", "username"])

  @staticmethod
  @base.access_point()
  def find_all(idea_id, page_no, page_size, connection=base.INIT_CONNECTION):
    stmt = '''SELECT reply.*, idea.id, username FROM reply JOIN idea ON idea.id=idea_id JOIN \"user\" 
      ON \"user\".id=author_id WHERE idea.id=%s ORDER BY reply.creation_date DESC OFFSET %s LIMIT %s'''
    c = connection.cursor()
    c.execute(stmt, (idea_id, page_no, min(page_size, 20)))
    return [Reply(*row) for row in c.fetchall()]
