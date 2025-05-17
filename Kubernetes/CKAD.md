
## CKAD Preparation

### Environment Variables & Secrets

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
      envFrom:
        - configMapRef:
            name: <name_of_the_config_map>
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
      env:
        - name: <name_of_the_key>
          valueFrom:
            configMapKeyRef:
                name: <name_of_the_config_map>
                key: <name_of_the_key>
```

#### Through Secrets:

While environment variables can be pushed through key-value mapping or config maps, storing sensitive secrets like password, api key etc is stored via `Secret` (It uses fkin Base64 to encode is, so never ever use it in production).

To createa a secret, The same format of type `ConfigMap` is used, however `kind` is set to `Secret`.
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: <secret_name>
data:
  SECRET_KEY_1: SECRET_VALUE_1
  SECRET_KEY_2: SECRET_VALUE_2
```


To reference a secret object, The same `envFrom` can be used. However, instead of `configMapRef`, We will use `secretRef`.
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: myapp
spec:
  containers:
    - name: random
      envFrom:
        - secretRef:
            name: <secret_name>
```

To only reference specific keys from the secret, We can use `valueFrom` with `secretKeyRef`.
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: myapp
spec:
  containers:
    - name: random
      env:
        - name: SECRET_KEY_1
          valueFrom:
            secretKeyRef:
              key: SECRET_KEY_1
              name: <secret_name>
```

> NOTE: Please note that `envFrom` is a list object while `valueFrom` is not!

The `Secret` object can also be created through command line:
```bash
kubectl create secret generic <secret_name> \
--from-literal=KEY1=VALUE1 \
--from-literal=KEY2=VALUE2
```


<br/><br/>

### Security Context in Kubernetes

While running a pod, We can specify properties associated with security context such as user ID to run the pod/container with and necessary capabilities.

For an example, If there is a pod consisting of multiple containers. And we want all of them to run with a specific user ID (lets say 1001), We can specify it the following way under `securityContext`.
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: random-pod
spec:
  securityContext:
    runAsUser: 1001
  containers:
    - name: random-container
      image: nginx

    - name: random-container-2
      image: mysql
```

We can also specify container specific security context:
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: random-pod
spec:
  containers:
    - name: random-container
      image: nginx
      securityContext:
        runAsUser: 1001

    - name: random-container-2
      image: mysql
```

We can also specify capabilities to containers (Only supported for container specific security context and not for Pods)
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: random-pod
spec:
  containers:
    - name: random-container
      image: nginx
      securityContext:
        runAsUser: 1001
        capabilities:
          add: ["SYS_TIME", "NET_ADMIN"]

    - name: random-container-2
      image: mysql
```

The first priority is always given to the container specific security context. If it is absent, only then the pod specific security context is considered.
