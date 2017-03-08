import os

def register_user(username, password, email):
  os.system("php /home/td/forum/add_user.php {} {} {}".format(username, password, email))