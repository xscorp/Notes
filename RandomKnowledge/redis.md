# Redis

## Description
Redis - Redis is an in-memory database that is used for caching data for faster lookups and sometimes, It is used as a complete database for an application. It stores the value in a key-value based data structure.


## Installation
### MAC
```$ brew install redis```

### Python Module
```$ pip3 install redis```


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

## Key expiration
### redis-cli
```python
127.0.0.1:6379> get name
"xscorp"
127.0.0.1:6379> EXPIRE name 10
(integer) 1
127.0.0.1:6379> get name
"xscorp"
127.0.0.1:6379> get name
(nil)
```

### In python
```python
>>> import redis
>>> 
>>> redis_client = redis.Redis(host = "127.0.0.1" , port = 6379 , db = 0)
>>> 
>>> redis_client.get("pass").decode()
'jett123'
>>> 
>>> redis_client.expire("pass" , time = 10)
True
>>> 
>>> redis_client.get("pass").decode()
'jett123'
>>> 
>>> redis_client.get("pass").decode()
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
AttributeError: 'NoneType' object has no attribute 'decode'
```

## Redis in Golang
```go
package main

import (
	"context"
	"fmt"

	"github.com/go-redis/redis/v8"
)

var ctx = context.Background()

func ExampleClient() {
	rdb := redis.NewClient(&redis.Options{
		Addr:     "127.0.0.1:6379",
		Password: "", // no password set
		DB:       0,  // use default DB
	})

	err := rdb.Set(ctx, "key", "value", 0).Err()
	if err != nil {
		panic(err)
	}

	val, err := rdb.Get(ctx, "key").Result()
	if err != nil {
		panic(err)
	}
	fmt.Println("key", val)

	val2, err := rdb.Get(ctx, "key2").Result()
	if err == redis.Nil {
		fmt.Println("key2 does not exist")
	} else if err != nil {
		panic(err)
	} else {
		fmt.Println("key2", val2)
	}
	// Output: key value
	// key2 does not exist
}

func main() {
	ExampleClient()
}

```
