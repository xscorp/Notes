# RabbitMQ
## Description and Example
RabbitMQ is a queueing service and a message broker. It can be used for data communication between multiple programs. Let's say you are creating a port scanner that accepts an IP address and scans for all the open ports. Each enumerated port is sent to another program for service enumeration. A more reliable and efficient way of doing this is to pass the enumeraed port on the queue, which will be consumed by the service enumerator. In this way, as soon as we have an open port, service scan will start on it. And in case the service scanner goes down due to some reason, it won't affect the port scanner as both services are running independently.

## Installation
### MAC
```console
brew install rabbitmq
```
To run rabbitmq as a docker, the image can be downloaded from [here](https://registry.hub.docker.com/_/rabbitmq/). To spawn the container-
```console
docker container run -it --rm --name "myrabbit" --hostname "myrabbit" -p 5672:5672 -p 15672:15672 rabbitmq:3.9-management
```
In the command above, we can see two ports being exposed. Port 5672 is used by rabbitmq for communication of data using queues while port 15672 is used for RabbitMQ Management console UI.

## Fundamentals
RabbitMQ has some components that are worth learning-
* **Producer** - The application that sends(produces) messages to the queue
* **Consumer** - The application that receives(consumes) messages from the queue
* **Queue** - A FIFO data structure
* **Channel** - A virtual connection to exchanges/queues etc
* **Exchange** - Message Routing Agents. Queues are bound to an exchange. The producer sends the message to an exchange and it puts them to queues.
* **Bindings** - Relationship between a queue and an exchange

## Using rabbitmq with python
Once the rabbitmq is running, we can use python to operate it using the **pika** module.
```console
pip3 install pika
```
Here is an example consisting of *send.py* which shows how to push data to a queue and *receive.py* which shows how to consume data from a queue.

#### *send.py*
```python
import pika

# Connecting to rabbitmq service
rabbitmq_connection = pika.BlockingConnection(pika.ConnectionParameters(host = "127.0.0.1"))

# Creating a channel to connect to a queue
channel = rabbitmq_connection.channel()

# Declaring queue (will be created if doesn't exist)
channel.queue_declare(queue = "hello")

# Message
msg_body = "Hello from xscorp"

# Publishing(Sending) a message to queue
channel.basic_publish(
    exchange = "" ,
    routing_key = "hello" ,
    body = msg_body
)

print(f"Sent: {msg_body}")

# Flushing the network buffer (Closing the connection)
rabbitmq_connection.close()
```

#### *receive.py*
```python
import pika
import sys

def main():
    # Connecting to rabbitmq service
    rabbitmq_connection = pika.BlockingConnection(pika.ConnectionParameters(host = "127.0.0.1"))

    # Creating channel
    channel = rabbitmq_connection.channel()

    # Declaring queue
    channel.queue_declare(queue = "hello")

    # Callback function to call everytime we consume a queue
    def rabbitmq_callback(ch , method , properties , body):
        print(f"Received: {body}")


    # Declaring callback function
    channel.basic_consume(
        queue = "hello" , 
        auto_ack = True ,
        on_message_callback = rabbitmq_callback
    )

    # Listening for messages
    print("[+] Listening for messages")
    channel.start_consuming()


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        sys.exit()
```

The above program was very basic and is not reliable. In case a consumer goes down for some reason, all the data assigned to it on the queue will be lost. For this, we can use explicit acknowledgement so the an ACK is only sent when the data is received and processed. 
Also, if the RabbitMQ server goes down for sometime, all the data inside it will be lost. So to withstand a RabbitMQ failure, We need to mark our queue as *durable*. Please note that to achieve this, we need to mark both the queue as well as messages as persistent. A durable program can be found below.

#### *send.py*
```python
import pika
import sys

# Connecting to rabbitmq service
rabbitmq_connection = pika.BlockingConnection(pika.ConnectionParameters(host = "127.0.0.1"))

# Creating a channel to connect to a queue
channel = rabbitmq_connection.channel()

# Declaring queue (durable = True is used for persistent queue)
channel.queue_declare(queue = "hello" , durable = True)

# Message
msg_body = sys.argv[1]

# Publishing(Sending) a message to queue
channel.basic_publish(
    exchange = "" ,
    routing_key = "hello" ,
    body = msg_body ,
    properties = pika.BasicProperties(
        # For persistent message
        delivery_mode = pika.spec.PERSISTENT_DELIVERY_MODE
    )
)

print(f"Sent: {msg_body}")

# Flushing the network buffer (Closing the connection)
rabbitmq_connection.close()
```

#### *receive.py*
```python
import pika
import time
import sys

def main():
    # Connecting to rabbitmq service
    rabbitmq_connection = pika.BlockingConnection(pika.ConnectionParameters(host = "127.0.0.1"))

    # Creating channel
    channel = rabbitmq_connection.channel()

    # Declaring queue
    channel.queue_declare(queue = "hello" , durable = True)

    # Callback function to call everytime we consume a queue
    def rabbitmq_callback(ch , method , properties , body):
        print(f"Processing: {body}")
        time.sleep(body.count(b"."))
        print("Done")
        # Explicit acknowledgement for reliability
        ch.basic_ack(delivery_tag = method.delivery_tag)


    # Setting prefetch count
    channel.basic_qos(prefetch_count = 1)

    # Declaring callback function
    channel.basic_consume(
        queue = "hello" , 
        on_message_callback = rabbitmq_callback
    )

    # Listening for messages
    print("[+] Listening for messages")
    channel.start_consuming()


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        sys.exit()
```

As can be seen in all the above codes, we are passing an empty(" ") exchange which allows RabbitMQ to use the default exchange to send messages to queues. Other thing that could be seen is, if we have multiple consumers, a message is received by either of them but not all. Once message is received by one consumer only. But there might be cases where all the consumers need to receive all the messages(for example, log monitoring). In this case, we can create multiple queues and transfer data to each of them. But this process can be managed nicely using an exchange. As described in the fundamentals, exchange is a routing agent which routes messages to different queues.
In a modified version of the above programs, the sender 


## Rabbitmq commands for monitoring
```console
root@myrabbit:/# rabbitmqctl list_queues
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages
amq.gen-PcYyoN328MI5JEuVGYKsxw	0
amq.gen-7DzAKY9Gs5bEOAxcMt-sFA	0
root@myrabbit:/# 
root@myrabbit:/# rabbitmqctl list_exchanges
Listing exchanges for vhost / ...
name	type
logs	fanout
amq.fanout	fanout
amq.rabbitmq.trace	topic
	direct
amq.topic	topic
amq.match	headers
amq.direct	direct
amq.headers	headers
root@myrabbit:/# 
root@myrabbit:/# rabbitmqctl list_bindings
Listing bindings for vhost /...
source_name	source_kind	destination_name	destination_kind	routing_key	arguments
	exchange	amq.gen-PcYyoN328MI5JEuVGYKsxw	queue	amq.gen-PcYyoN328MI5JEuVGYKsxw	[]
	exchange	amq.gen-7DzAKY9Gs5bEOAxcMt-sFA	queue	amq.gen-7DzAKY9Gs5bEOAxcMt-sFA	[]
logs	exchange	amq.gen-7DzAKY9Gs5bEOAxcMt-sFA	queue	amq.gen-7DzAKY9Gs5bEOAxcMt-sFA	[]
logs	exchange	amq.gen-PcYyoN328MI5JEuVGYKsxw	queue	amq.gen-PcYyoN328MI5JEuVGYKsxw	[]
root@myrabbit:/# 
root@myrabbit:/# rabbitmqctl list_channels
Listing channels ...
pid	user	consumer_count	messages_unacknowledged
<rabbit@myrabbit.1647493694.981.0>	guest	1	0
<rabbit@myrabbit.1647493694.1009.0>	guest	1	0
root@myrabbit:/# 
root@myrabbit:/# rabbitmqctl list_consumers
Listing consumers in vhost / ...
queue_name	channel_pid	consumer_tag	ack_required	prefetch_count	active	arguments
amq.gen-PcYyoN328MI5JEuVGYKsxw	<rabbit@myrabbit.1647493694.981.0>	ctag1.497f444f82484c14a162999861ccc27b	true	1	true	[]
amq.gen-7DzAKY9Gs5bEOAxcMt-sFA	<rabbit@myrabbit.1647493694.1009.0>	ctag1.c96695fd1aa848c4b87343865ef15b92	true	1	true	[]
root@myrabbit:/# 
root@myrabbit:/# rabbitmqctl list_connections
Listing connections ...
user	peer_host	peer_port	state
guest	172.17.0.1	59690	running
guest	172.17.0.1	59696	running
root@myrabbit:/#
root@myrabbit:/# rabbitmqctl list_queues name messages_ready messages_unacknowledged
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
amq.gen-PcYyoN328MI5JEuVGYKsxw	0	0
amq.gen-7DzAKY9Gs5bEOAxcMt-sFA	0	0

```
