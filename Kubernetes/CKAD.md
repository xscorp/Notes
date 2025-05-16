
## CKAD Preparation

### Passing environment variables in Kubernetes

There are three ways to set environment variables in Kubernetes:

#### Plain Key Value Format:

Providing key value pair of enviornment variables directly in definition file under the `env` key.
```yaml
apiVersion: v1
kind: Pod
metadata:
    name: random-pod
spec:
    containers:
        - name: random
          image: nginx
          env:
            - name: ENVIRONMENT_VARIABLE_NAME_1
              value: ENVIRONMENT_VARIABLE_VALUE_1

            - name: ENVIRONMENT_VARIABLE_NAME_2
              value: ENVIRONMENT_VARIABLE_VALUE_2
```


#### Through ConfigMap:

The other way to store and manage environment variables is via `ConfigMap`. A `ConfigMap` can be created either through YAML definition file or directly by a command.

YAML:
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
    name: random-pod
data:
    ENVIRONMENT_VARIABLE_NAME_1: ENVIRONMENT_VARIABLE_VALUE_1
    ENVIRONMENT_VARIABLE_NAME_2: ENVIRONMENT_VARIABLE_VALUE_2
```

Command:
```bash
kubectl create configmap <configmap_name> \
--from-literal=ENVIRONMENT_VARIABLE_NAME_1=ENVIRONMENT_VARIABLE_VALUE_1 \
--from-literal=ENVIRONMENT_VARIABLE_NAME_2=ENVIRONMENT_VARIABLE_VALUE_2
```

<br/>

Once the config map is created, It can be referenced in a pod definition file like this:
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: myapp
spec:
  containers:
    - name: random
      env:
        - name: <name_of_the_key>
          valueFrom:
            configMapKeyRef:
                name: <name_of_the_config_map>
                key: <name_of_the_key>
```

However, If we only want specific keys from the file, Then instead of the above `envFrom`, `valueFrom` should be used:
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: myapp
spec:
  containers:
    - name: random
      envFrom:
        - configMapRef:
            name: <name_of_the_config_map>
```

#### Through Secrets:

