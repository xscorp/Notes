## Kubernetes Basics

**Cluster**: Cluster is simply a group of computers/VMs (also called Nodes in Kubernetes) that are connected to work like a single unit.
**Node**: A physical computer or a VM in the cluster.
**Kubelet**: An agent to manage everything in a node. Kublet communicates with the Control Plane through Kubernetes API.
**Control Plane**: Program that manages all necessary things in a cluster like managing, scheduling, scaling etc.

### Making sense of the terminology

So we know that Kubernetes is used for container orchestration - Managing docker containers (scaling, scheduling, checking state etc). So these containers are called **pods**. These pods run inside computers/VMs (**nodes**). All the nodes have an agent **kublet** in them to communicate with the **Control Plane** (kinda like the control panel) through the **Kubernetes API**.

