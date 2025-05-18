
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

<br/><br/>

### Resource Management in Kubernetes

Pod and container specific resource requirements can be specified.
* **Resource Requests**: Actual value of CPU/Memory to actually assign to a resource.
* **Resource Limits**: Maximum allowed value of CPU/Memry to a resource.

The resource limits and requests can be specified in `resources` property under `spec`.
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: random-pod
spec:
  containers:
    - name: random-container
      image: ubuntu
      resources:
        limits:
          memory: "2Gi"
          cpu: "1000m"
        requests:
          memory: "1Gi"
          cpu: "500m"
```

In a sophisticated environment with multiple containers, We can directly manage resources at pod level instead of individual containers.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: random-pod
spec:
  resources:
    limits:
      memory: "2Gi"
      cpu: "1000m"
    requests:
      memory: "1Gi"
      cpu: "500m"
  containers:
    - name: random-container
      image: ubuntu
```

> **This feature is in alpha release till now, therefore its reliability can't be guarenteed.**

There are couple of memory units, which follow the following standard:
```
1M = 1000K
1Mi = 1024K
1G = 1000M
1Gi = 1024M
```

Similarly, for CPU, Numbers correspond to CPU core(s):
```
1 = 1000m = 1 vCPU / 1 core
0.5 = 500m = half of 1 vCPU / 1 core
```

<br/><br/>

### Service Accounts in Kubernetes

Service accounts are used by kubernetes components and applications to communicate with kubernetes API service. A pod is able to communicate with the kube API because it has the required token. Whenever we create a pod/token, A default service account is created for each namespace by default.

To be able to communicate with the kube API, the following steps are followed:
1. Create a service account
```bash
kubectl create serviceaccount <service_account_name>
```

2. Generate an authentication token for the service account
```bash
kubectl create token <service_account_name>
```

3. Mention the service account reference in your deployment/pod definition file:
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: random
spec:
  containers:
    name: random
  serviceAccountName: dashboard-sa
```
