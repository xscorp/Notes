## Kubernetes Basics

### Basic Terminology

* **Cluster**: Cluster is simply a group of computers/VMs (also called Nodes in Kubernetes) that are connected to work like a single unit.

* **Node**: A physical computer or a VM in the cluster.

* **Pod**: A group of containers in a node. While a container is the smallest unit in containerized environment, In k8s, A pod is the smallest deployment unit which consist of one or more containers which share all kinds of resources like volumes etc. So basically a pod is a group of one or more containers.

* **Kubelet**: An agent to manage everything in a node. Kublet communicates with the Control Plane through Kubernetes API.

* **Control Plane**: Program that manages all necessary things in a cluster like managing, scheduling, scaling etc.

<br/><br/>

### Making sense of the terminology

So we know that Kubernetes is used for container orchestration - Managing docker containers (scaling, scheduling, checking state etc). So these containers run inside a unit called a **pod**. These pods run inside computers/VMs (**nodes**). All the nodes have an agent **kublet** in them to communicate with the **Control Plane** (kinda like the control panel) through the **Kubernetes API**.

<br/><br/>

### Deployment & Service

**Deployment**: A deployment checks on your pods for their health and performs neecessary action like restart etc. A deployment is the recommended way to manage both the creation and scaling of pods. A deployment can be created simply by writing:  
```bash
$ kubectl create deployment <deployment-name> --image=<image-path>
```

Once a deployment is created, You can see stuff about deployment using the following commands:
```bash
$ kubectl get deployments        // list deployments
$ kubectl get pods               // list pods associated with the deployment
$ kubectl get events             // list events associated with a deployment/pod
$ kubectl logs <pod-name>        // view application log associated with a pod
```

<br/>

**Service**: A k8s service is used for creating a stable endpoint (An IP address and a DNS name) to access a logical set of pods and enforce a policy to access them. For an example, Once you create a deployment of multiple pods, You might want them to be accessible outside. You can make a logical set of pods accessible through a stable single IP address and a DNS name by creating a service. Even when the pods associated with the service go down and a new IP is assigned to them, you will still have a stable IP to logically access the group. Once an IP and DNS name is assigned to the logical set, creating a service also requires you to specify the **service type** - to who all it will be exposed to.
There are mainly 3 kinds of service types:
* **ClusterIP (default)**: When you create a service with `ClusterIP` type, It will assign a stable IP and DNS name to the logical set of pods and make it accessible only within the kubernetes cluster. That means by default any pods, any node can access it within the cluster. But it is not exposed outside the cluster, so It is not accessible from the internet.
* **NodePort**: It makes the logical set of pods accessible through a port in each of the nodes. While its not very usable, It is mostly used for testing/debugging.
* **LoadBalancer**: It is used in cloud environments. It is used for exposing the logical set of pods through a load balancer. When the service type is specified as `LoadBalancer`, It provisions a load balancer in the cloud environment automatically and makes it accessible through it. It is mostly used for exposing your application outside the cluster.

<br/><br/>

### Default Communication Behaviour

#### Pod-to-Pod Communication:

* By default, all pods in a cluster can communicate with each other directly
* Uses flat network space - every pod gets unique IP address
* Pods in same namespace can reach each other using pod name
* Pods in different namespaces need fully qualified domain name (FQDN)


#### Node-to-Node Communication:

* Nodes can communicate with each other by default
* Required for cluster operations like pod scheduling and networking
* Uses kubelet for node-to-control plane communication
* Node ports (30000-32767) are reserved for exposing services


#### Cluster-to-Cluster Communication:

By default, Kubernetes clusters CANNOT communicate with each other. Each cluster is an isolated environment with its own:
* Control plane
* Network space
* Service discovery
* Authentication/Authorization systems


#### Pod-to-Node Communication:

* Every pod gets a unique IP address that's accessible from any node
* Flat networking model - no NAT needed
* Communication works the same whether pods are on same or different nodes

<br/><br/>

### Exposing a service

Viewing services:
```bash
kubectl get services
```

Exposing your service (template):
```bash
kubectl expose deployment/{deployment-name} --type="{service-type}" --port {pod-port-to-expose}
```

Exposing your service (example):
```bash
kubectl expose deployment/kubernetes-bootcamp --type "NodePort" --port 8080
```

As discussed, the `NodePort` type exposes your application to each of the nodes on a specified port. The port passed to the expose command specifies what port to export in the pod, However the application is exposed on node level on a different port. That port can viewed by checking the `kubectl get services/{deployment-name}` command.

```bash
$ kubectl get services
kubernetes-bootcamp   NodePort       10.97.175.135    <none>        8080:31443/TCP   33d
```

In the above example, The `8080` is the port exposed on pods while `31443` is the port which exposed the application on each of the nodes. So the application is accessible from all the node IPs on this specific port.

NOTE:
Please note that the application will still not be accessible if minikube is being operated under docker desktop application. The reason can be understood by viewing the below text diagram:
```
Normal Setup (Linux):
Host → NodePort → Pod

Docker Desktop Setup:
Host → [Docker VM → NodePort → Pod]
      ^ This boundary needs tunneling
```

Therefore a tunnel needs to be created that allows us to access the application running on a node inside a VM (docker). This can be done through minikube using:
```bash
minikube service {service-name} --url
```

In our case,
```bash
minikube service kubernetes-bootcamp --url
```

The minkube automatically manages the tunneling and the necessary port forwarding to make our application accessible outside the docker VM. Now, the application will be accessible via the URL showed by minikube.



### Using labels

The deployment automatically assigns labels to each of the pods. For an example, all the pods of a deployment named `kubernetes-bootcamp` will have the label `app=kubernetes-bootcamp`. This label can be used for refering to the deployment or all the pods/services related to the deployment.

Examples:
```bash
kubectl get pods -l app=kubernetes-bootcamp
```
```bash
kubectl get services -l app=kubernetes-bootcamp
```

To assign a label to a pod:
```bash
kubectl label pods {pod-name} {label}
```

Example:
```bash
kubectl label pods kubernetes-bootcamp-a23caff345 version=v1
```

More details about a pod can be seen using:
```bash
kubectl describe pods
```

<br/><br/>

### Executing Commands

A command can be executed inside a pod the same way we do with dockers:
```bash
kubectl exec -it {pod-name} -- {command}
```

<br/><br/>

### Deleting Services

A service related to your deployment can be deleted using:
```bash
kubectl delete service -l app={deployment-name}
```

<br/><br/>

### Scaling Deployment

To upscale/downscale a deployment, The replicas can be increased using:
```bash
kubectl scale {deployment-name} --replicas {desired-replica-count}
```
Example:
```bash
kubectl scale deployments/kubernetes-bootcamp --replicas 4
```

Note that the distribution of these pods among multiple nodes depends upon various factors including memory and CPU capacity of each node. So equal distribution of pods among the nodes is not guarenteed.


<br/><br/>

### Rolling Updates

Rolling updates help in updating the application without requiring any service downtime. Whenever any changes to the application or properties (such as image modification) are done, The pods start updating one by one instead of all at once so there is no downtime. If the new update turns out to be flawed, the deployment can be rolled back to the previous stable version (or arbitrary version).

Lets say the docker image for a particular deployment was changed:
```bash
kubectl set image deployments/{deployment-name} {image-path}
```

The older pods will start terminating and restarting with the updated image one by one. In case the pods fail, There will still be pods running the older deployment to avoid downtime. This can be verified using `kubectl get pods` command.

If the update to the deployment needs to be rolled back, The following command will restore all the pods to their last stable stage:
```bash
kubectl rollout undo deployments/{deployment-name}
```
Example:
```bash
kubectl rollout undo deployments/kubernetes-bootcamp
```

All the deployment updates can be listed using:
```bash
kubectl rollout history deployments/{deployment-name}
```

To rollback to a specific version, use the command:
```bash
kubectl rollout undo deployments/{deployment-name} --to-revision {rollout-id}
```
Example:
```bash
kubectl rollout undo deployments/kubernetes-bootcamp --to-revision 2
```


<br/><br/>

### Replications

Pod replicas can be set similar to what we do in docker compose. Replicas allows us to specify how many pods to keep running at any point in time.

There are two ways specify replicas - Through `ReplicationController` and through `Replica Set`. The formed is an older technique which is now replaced by the latter.

To understand the usage of both, consider an `nginx` pod created using the following config:
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx

spec:
  containers:
    - name: nginx-container
      image: nginx:latest
```

Now lets say we want to have atleast 3 pods of this running at any moment. We can do this the following two ways:

#### Using `ReplicationController`
```yaml
apiVersion: v1
kind: ReplicationController
metadata:
  name: nginx-rc
  labels:
    app: myapp

spec:
  replicas: 3
  template:
    metadata:
      name: nginx

    spec:
      containers:
        - name: nginx-container
          image: nginx:latest
```

#### Using `ReplicaSet`
```yaml
apiVersion: apps/v1
kind: ReplicaSet
metadata:
  name: nginx-rs
  labels:
    app: myapp

spec:
  replicas: 3

  selector:
    matchLabels:
      app: myapp

  template:
    metadata:
      name: nginx

    spec:
      containers:
        - name: nginx-container
          image: nginx:latest
```

The key difference in the replicaset is the capability to specify selectors which are used for specifying what all types of resources the replication should apply to, even if they are created independently. The other difference is the usage of `apps/v1` instead of `v1` in the `apiVersion`. It is simply because `ReplicaSet` is available in the new `apps/v1` API and no on the older `v1` API.

The reason we need to specify the entire `template` field even when we have selectors is to allow the Replica set to spawn the necessary pods in case there are lesser pods. The Replica set needs to know what type of pod to create, Thats why entire pod definition is passed in the `template` field.

Couple of handy commands while dealing with `ReplicaSet`:

* To list all replica sets
```bash
kubectl get replicaset
```

* To modify an existing replica set
```bash
kubectl replace replicaset <replicaset_name> ...
```

* To easily edit the configuration of a replicaset live
```bash
kubectl edit replicaset <replicaset_name>
```