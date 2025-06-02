## CKAD Preparation - Services & Networking

### Networking Policies in Kubernetes

By default, All the pods can reach each other. To have control over this beahaviour, Network Policies are applied so that in-flow and out-flow traffic can be controlled.

* **Ingress**: A policy to specify incoming traffic to the resource in kubernetes.
* **Egress**: A policy to specify outgoing traffic from the resource in kubernetes.

Consider an example where there are three pods - `application`, `api`, `db`. We want to disallow `application` pod from reaching the `db` pod. For this, We can create an Ingress (incoming) network policy for the `db` pod where we specify that only `api` pod should be able to reach the `db` pod.

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: db-allow-api-only
  namespace: default
spec:
  podSelector:              # Pod on which this policy applies
    matchLabels:
      app: db
  policyTypes:
    - Ingress
  ingress:
    - from:
        - podSelector:      # Pod that should be allowed to reach the `db` pod
            matchLabels:
              app: api
```

Similarly, Here is an example of an egress (outgoing) network policy that allows the pod `internal` to reach `mysql` pod on 3306 and `payroll` pod on 8080.

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: internal-policy
  namespace: default
spec:
  podSelector:
    matchLabels:
      name: internal
  policyTypes:
  - Egress
  - Ingress
  ingress:
    - {}
  egress:
  - to:
    - podSelector:
        matchLabels:
          name: mysql
    ports:
    - protocol: TCP
      port: 3306

  - to:
    - podSelector:
        matchLabels:
          name: payroll
    ports:
    - protocol: TCP
      port: 8080

  - ports:
    - port: 53
      protocol: UDP
    - port: 53
      protocol: TCP
```


<br/><br/>

### Ingress in Kubernetes

In the days when on-premises infrastructure was common, People used to have a single load balancer or a single reverse proxy for all their needs. It did host/path based routing for all the different services, and had other cool features also. Some examples are - Nginx, F5, Trafeik etc.

Later when kubernetes came, Kubernetes had `LoadBalancer` service type to provision a load balancer. But it lacked the host/path based routing, route rewrites and additional capabilities. Also, each publicly exposed service in kubernetes required it's own load balancer costing

