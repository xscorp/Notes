## Kubernetes Basics

**Cluster**: Cluster is simply a group of computers/VMs (also called Nodes in Kubernetes) that are connected to work like a single unit.
**Node**: A physical computer or a VM in the cluster.
**Pod**: A group of containers in a node. While a container is the smallest unit in containerized environment, In k8s, A pod is the smallest deployment unit which consist of one or more containers which share all kinds of resources like volumes etc. So basically a pod is a group of one or more containers.
**Kubelet**: An agent to manage everything in a node. Kublet communicates with the Control Plane through Kubernetes API.
**Control Plane**: Program that manages all necessary things in a cluster like managing, scheduling, scaling etc.

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

### Default Communication Behaviour:
