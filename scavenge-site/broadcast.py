import time
import random
import trade

def identity(string):
  return string

def weather_forecast(string):
  return string.format("{}F {}".format(random.randint(-459, 459), random.choice(["Cloudy", "Clear", "Rainy", "Overcast"])))

announcements = [
  ("Spawn shops are now open! Check out /spawn.", identity),
  ("Online player shops/trading live! Do /register to join and go to http://scavenge.org/trade.", identity),
  ("Remember to /vote to get rewards and support the server!", identity),
  ("Current activities: building player shops, developing website, and reticulating splines.", identity)
]
  

if __name__ == "__main__":
  i = 0
  while True:
    if i == len(announcements):
      i = 0
    (announcement, fn) = announcements[i]
    trade.announce(fn(announcement))
    i += 1
    time.sleep(60 * 10)
