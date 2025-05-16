
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

