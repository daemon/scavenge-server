import pika

def publish_message(message):
  connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
  try:
    channel = connection.channel()
    channel.queue_declare(queue="trade", durable=True)
    channel.basic_publish(exchange="", routing_key="trade", body=message, 
      properties=pika.BasicProperties(delivery_mode=2))
  finally:
    connection.close()

def lock_uuid(uuid):
  publish_message("lock {}".format(uuid))

def unlock_uuid(uuid):
  publish_message("unlock {}".format(uuid))

def announce(message):
  publish_message("announce {}".format(message))
