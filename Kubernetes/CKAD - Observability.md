## CKAD Preparation - Observability

### Readiness and Liveness Probes

Consider a group of containers which receive live traffic from users as soon as they are up. But the tech stack running in the containers is a bit heavy. So it takes 15-20 seconds to get things ready. However, the container is already marked as "ready" and therefore receiving live traffic. But the container is actually not ready until 15-20 seconds. To address these issues, Readiness ("Ready-ness") probe is used.

Similarly, Consider a case where the pod is stuck due to a poor application logic (maybe infinite loop or deadlock). The pod will still show up as "running" even when it is stuck. This is where Liveness probe comes to rescue.

* **Readiness Probe** - To specify when a pod should be marked as ready.
* **Liveness Probe** - To specify when a pod is marked as live.

The definition of "ready" and "live" can be specific to each container. Most common ways to communicate this are:
* By exposing an API endpoint like `/health`, `/is-live` etc.
* Testing by connecting to a port to see if it is open
* By inspecting the output of a command

Kubernetes has readiness probes for all the above three conditions. All the below three probes can be used under `readinessProbe` and `livenessProbe` inside a container specification.

* By exposing an API
```yaml
httpGet:
    path: /health
    port: 8080
```

* By testing connection to a port
```yaml
tcpSocket:
    port: 8080
```

* By testing output of a command
```yaml
exec:
    command:
    - cat
    - /tmp/healthy
```

For further assistance, There are other options like:
* `initialDelaySeconds`: Seconds to wait for before testing the probe
* `periodSeconds`: Delay before each probe
* `failureThreshold`: Maximum number of probes to test before considering the test as failed

Example:
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: random-pod
spec:
  containers:
    - name: random-container
      image: nginx
      readinessProbe:
        httpGet:
          path: /health
          port: 9000
        initialDelaySeconds: 5
        periodSeconds: 3
        failureThreshold: 4

      livenessProbe:
        tcpSocket:
          port: 8080
```


<br/><br/>

### Logs Inspection in Kubernetes

Logs of a pod can be viewed using the following command:
```bash
kubectl logs <pod_name>
```

In case of multiple container pod, A specific container can be specified like:
```bash
kubectl logs <pod_name> <container_name>
```