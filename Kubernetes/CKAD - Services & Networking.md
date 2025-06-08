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

Later when kubernetes came, Kubernetes had `LoadBalancer` service type to provision a load balancer. But it lacked the host/path based routing, route rewrites and additional capabilities, WAF capabilities, etc. Also, each publicly exposed service in kubernetes required it's own load balancer costing a lot of money for the organizations.

To overcome this, Kubernetes asked the companies like F5, Cloudflare etc to create a kubernetes version of their products like Nginx, called `Ingress Controllers`. And then deployments can create `Ingress Resource` to be able to use it.

So now if we want a single load balancer for all the services deployed in the company? We can use an nginx or similar deployment. But maintaining it will be difficult. Everytime a new service needs to be integrated, we need to get inside the nginx deployment, change the routes, the reload the Nginx. So maintainence will be all manual. But ingress version of this, called "Nginx Ingress Controller" would allow developers to write a nice YAML to define their rules, and then automatically reload the Nginx, do certificate management etc.

As conclusion, In large environments, Ingress Controllers are the preferred way for load balancing as they are scalable, easily maintainable and reduce cost.

Once an ingress controller is configured, This is how an ingress resource is created to route to an application:
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
    name: color-deployments-ingress

spec:
    ingressClassName: nginx
    rules:
        - http:
            paths:
                - path: /red
                  pathType: Prefix
                  backend:
                    service:
                        name: red-deployment
                        port:
                            number: 80

                - path: /blue
                  pathType: Prefix
                  backend:
                    service:
                        name: blue-deployment
                        port:
                            number: 80
          host: kube.local
```

As can be seen, It specifies that `/red` path on the host `kube.local` on port `80` (service port) will route to `red-deployment` service. And a similar `/blue` path for `blue-deployment`.

Now if a user visits `http://kube.local/red`, They will be routed to the `red-deployment` and `http://kube.local/blue` to the `blue-deployment`.

<br/>

#### Using Ingress Controllers and Resources

Consider a scenerio where we have two deployments:

`red-deployment`
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
    name: red-deployment

spec:
    replicas: 2
    selector:
        matchLabels:
            app: red
    template:
        metadata:
            labels:
                app: red

        spec:
            containers:
                - name: red-pod
                  image: hashicorp/http-echo
                  args: 
                    - "-text=<html><body style='background-color:red'></body></html>"
                  ports:
                    - containerPort: 5678
```

`blue-deployment`
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
    name: blue-deployment

spec:
    replicas: 2
    selector:
        matchLabels:
            app: blue
    template:
        metadata:
            labels:
                app: blue

        spec:
            containers:
                - name: blue-pod
                  image: hashicorp/http-echo
                  args: 
                    - "-text=<html><body style='background-color:blue'></body></html>"
                  ports:
                    - containerPort: 5678
```

These deployments simply host a webapp on port `5678` that displays red and blue color depending on their deployment.

Exposing the deployments:
```bash
$ kubectl expose deployments/red-deployment --type=ClusterIP --port=80 --target-port=567

$ kubectl expose deployments/blue-deployment --type=ClusterIP --port=80 --target-port=567
```

~~Please note that we could have used the service port (`--port`) as `5678`. But the service port is used by ingress resource and it makes it accessible on the service port. Since we want our deployments to be accessible on port 80 of the main IP, We need to put `80` as service port.~~

*Any port can be choosen for service port as ingress always listens on port 80 and 443 and will redirect you to the right service and right port based on the route rules.*


Before creating an ingress resource, We need to make sure our kubernetes deployment supports Ingress. In minikube, The Nginx Ingress controller can be enabled simply by:
```bash
$ minikube addons enable ingress
```

In real environments, where minikube is not used, We can deploy the nginx ingress controller using this:
```bash
$ kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.12.3/deploy/static/provider/baremetal/deploy.yaml
```

Once ingress controller is there, We can create an `Ingress Resource` to route to the services:
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
    name: color-deployments-ingress

spec:
    ingressClassName: nginx
    rules:
        - http:
            paths:
                - path: /red
                  pathType: Prefix
                  backend:
                    service:
                        name: red-deployment
                        port:
                            number: 80

                - path: /blue
                  pathType: Prefix
                  backend:
                    service:
                        name: blue-deployment
                        port:
                            number: 80
          host: kube.local
```

Now both the deployments are accessible using a single host based on their route.

