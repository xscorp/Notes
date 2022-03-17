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
```
