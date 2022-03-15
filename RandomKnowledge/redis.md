# Redis

## Description
Redis - Redis is an in-memory database that is used for caching responses and sometimes, as a complete database. It stores the value in a key-value based data structure.


## Installation
### MAC
```brew install redis```

### Python Module
```pip3 install redis```


## Basic working
Before using the redis-cli, we need to enable the Redis service. It runs on port 6379 by default.
```brew services start redis```

Once the server is up, we can use the redis interface by invoking
```$ redis-cli```

Redis support functions like get, set and del for getting, setting and deleting keys in the redis database
```python
127.0.0.1:6379> 
127.0.0.1:6379> keys *
(empty array)
127.0.0.1:6379> 
127.0.0.1:6379> set name xscorp
OK
127.0.0.1:6379> set pass jett123
OK
127.0.0.1:6379> 
127.0.0.1:6379> keys *
1) "pass"
2) "name"
127.0.0.1:6379> 
127.0.0.1:6379> get name
"xscorp"
127.0.0.1:6379> get pass
"jett123"
127.0.0.1:6379> 
```

## Using python module
```python
>>> import redis
>>> 
>>> redis_client = redis.Redis(host = "127.0.0.1" , port = 6379 , db = 0)
>>> 
>>> redis_client.set("name" , "xscorp")
True
>>> redis_client.set("pass" , "jett123")
True
>>> 
>>> redis_client.get("name").decode()
'xscorp'
>>> 
>>> redis_client.get("pass").decode()
'jett123'
>>> 
>>> 
>>> redis_client.keys()
[b'pass', b'name']
```
