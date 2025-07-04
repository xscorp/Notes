
## CKAD Preparation - Security

### Context Switching in Kubernetes

There can be multiple kubernetes users and multiple clusters configured by kubernetes administrator or DevOps team. For an example, users can be named `qa`, `devs`, `researchers`, etc, and clusters can be named `prod`, `dev`, `staging`. Lets say we are currently executing `kubectl` commands as `researchers` user in `staging` cluster. What if we have to switch context to `qa` user in `prod` cluster? This is managed via kube config file.

Path to kube config file: $HOME/.kube/config
Can also be viewed by `kubectl config view`

Each kube config file defines three things - `clusters`, `users`, and then `contexts` based on them. Switching users and clusters would require necessary authorization. For that, necessary credentials (passwords, tokens, certificates) are specified in their respective places. The following keys are required for an authenticated session:
* Client Certificate (For user)
* Client Key (For user)
* Server certificate (For cluster)


```yaml
apiVersion: v1
kind: Config
current-context: research

clusters:

- cluster:
    certificate-authority: /etc/kubernetes/pki/ca.crt
    server: https://controlplane:6443
  name: development

- cluster:
    certificate-authority: /etc/kubernetes/pki/ca.crt
    server: https://controlplane:6443
  name: kubernetes-on-aws

- cluster:
    certificate-authority: /etc/kubernetes/pki/ca.crt
    server: https://controlplane:6443
  name: production

- cluster:
    certificate-authority: /etc/kubernetes/pki/ca.crt
    server: https://controlplane:6443
  name: test-cluster-1


contexts:

- context:
    cluster: kubernetes-on-aws      # Name of the cluster
    user: aws-user                  # Name of the user
  name: aws-user@kubernetes-on-aws  # Name of the context to use to switch to this context

- context:
    cluster: test-cluster-1
    user: dev-user
  name: research

- context:
    cluster: development
    user: test-user
  name: test-user@development

- context:
    cluster: production
    user: test-user
  name: test-user@production

users:
- name: aws-user
  user:
    client-certificate: /etc/kubernetes/pki/users/aws-user/aws-user.crt
    client-key: /etc/kubernetes/pki/users/aws-user/aws-user.key

- name: dev-user
  user:
    client-certificate: /etc/kubernetes/pki/users/dev-user/dev-user.crt
    client-key: /etc/kubernetes/pki/users/dev-user/dev-user.key

- name: test-user
  user:
    client-certificate: /etc/kubernetes/pki/users/test-user/test-user.crt
    client-key: /etc/kubernetes/pki/users/test-user/test-user.key

preferences: {}
```

Context can be switch using the following command:
```bash
kubectl config use-context <context-name>
```

For example, If we wnat to switch to `dev-user` user in the `test-cluster-1` cluster based on the above config, We can use:
```bash
kubectl config use-context research
```

To specify a custom kube config instead of using the default one:
```bash
kubectl config --kubeconfig <path-to-config-file> use-context <context-name>
```

To avoid passing a custom config file repeatedly, We can set the `$KUBECONFIG` env variable instead of overwriting the default config.

<br/><br/>

### RBAC in Kubernetes

Instead of giving individual users access to resources, A role can be created. A role will have certain permissions that can later be bound to a user.

A role in kubernetes specifies what all API Groups (core, apps, etc), resources (pods, deployments, services, etc) and verbs (get, list, watch, create, delete, etc) a user/role can access. A role can be created using the following role definition specification:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: developer
  namespace: default

rules:
  - apiGroups: [""]
    resources: ["pods"]
    verbs: ["create", "list", "delete"]
```

Once a `Role` is created, That role can be bound to a user/group using `RoleBinding` object. A `RoleBinding` object creates binding between a role and a user/group. A sample role binding can be created using the following specification:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding

metadata:
  name: dev-user-binding
  namespace: default

subjects:
  - name: dev-user
    kind: User

roleRef:
  name: developer
  kind: Role
```

To verify if you can access a user can execute a certain command on a certain namespace, the `auth can-i` command can be used:

```bash
k auth can-i get pods/dark-blue-app -n blue --as dev-user
```


<br/><br/>

### Cluster scoped RBAC in Kubernetes

The classic `Role` and `RoleBinding` objects are **namespace scoped**, which means that the bounded user can only perform those `verbs` on `resources` from `apiGroups` on a specified namespace (`default` if not specified). But not every object in kubernetes is namespace scoped. For an example, nodes, storage classes, etc are tied to a cluster and not specific to a namespace. To implement RBAC in such cluster scoped or non-namespace scoped objects, We use `ClusterRole` and `ClusterRoleBinding`. The specification remains same as the classic `Role` and `RoleBinding` objects, just the `resources` are changed.

<br/>

A `ClusterRole` can be defined using:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: storage-admin
rules:
- apiGroups: [""]
  resources: ["storageclasses", "persistentvolumes"]
  verbs: ["get", "watch", "list", "create", "update", "delete"]
```

<br/>

A `ClusterRoleBinding` object can be defined using:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: michelle-storage-admin
subjects:
- kind: User
  name: michelle
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: ClusterRole
  name: storage-admin
  apiGroup: rbac.authorization.k8s.io
```

<br/>

To create a cluster role and cluster role binding using command line, following command can be used:

```bash
kubectl create clusterrole my-role --verb=get,list,create --resource=nodes,storageclasses
```


<br/><br/>

### Admission Controllers

Any command on any object/resource is executed after the authentication and authorization is done

```
kubectl command -> authentication (using client certificate) -> authorization (RBAC, etc) -> Kube API Server
```

The RBAC authorization doesn't provide fine grained control over resources. You can only specify "get", "list" etc over resources. But what if we want to perform additional checks - like create a namespace if it doesn't exist already. What if we want each pod creation request to have certain labels? Thats where Admission Controllers come into play, post authorization. Now the chain becomes:

```
kubectl command -> authentication (using client certificate) -> authorization (RBAC, etc) -> Admission Controllers -> Kube API Server
```

Admission Controllers allows fine grained control via plugins. For an example, Automatic namespace creation is enabled via `NamespaceAutoProvision` plugin.

Plugins can be enabled/disabled in a Kube API server via `--enable-admission-plugins` and `--disable-admission-plugins` flag as arguments to Kube API server.

These flags can be added via modifying the start command of kube api server in `/etc/kubernetes/manifests/kube-apiserver.yaml `.

If a user intends to create a pod in `test` namespace which doesn't exist, We can enable the `NamespaceAutoProvision` admission controller plugin like `--enable-admission-plugins=NamespaceAutoProvision`

A point worth noting is that admission controllers can only control create, modify and delete requests on a resource. It can't block get, list or watch requests as they bypass the admission controller.


<br/><br/>

### Enabling deprecated/older API versions

Kubernetes has alpha, beta, and stable (v1) versions for resources. Alpha ones are experimental, and can be removed without notice. Once converted to beta, support for atleast 3 releases must be provided. Ultimately, stables once are moved to v1.

To enable a deprecated API group version that is disabled by default, We can specify the `--runtime-config` flag in the kubernetes API service manifest file:

Example:
```
--runtime-config=rbac.authorization.k8s.io/v1alpha1
```

Similarly, to convert versions of existing resources, We can use the `kubectl convert` command, which is offered by `kubectl-convert` plugin installed explicitely.

For example, to change the group version in a file named `ingress-old.yaml` to `networking.k8s.io/v1`, We can execute:

```bash
kubectl-convert -f ./ingress-old.yaml --output-version "networking.k8s.io/v1"
```


<br/><br/>

### Custom Resource Definition (CRD) and Custom Controller

Imagine if we could create our own resource, say named "Person". Each person can have age, height, weight, strength and name. Then we could run "kubectl get persons" to list out persons and even have a short name for it - "kubectl get ps".

Kubernetes allows us to create Custom Resource Definitions, and custom controllers to achieve the same.

A *Custom Resource Definition* allows us to define what type of resource we want to create. On the other hand *Custom Controller* is a piece of code which allows us to specify what will happen if an instance of that custom resource is created.

A CRD can be created using the following specification:
```yaml
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: persons.api.goodpeople.com      # name must be (spec.names.plural) . (spec.group)
spec:
  group: api.goodpeople.com
  scope: Namespaced
  names:
    plural: persons
    singular: person
    shortNames: ["ps"]
    kind: Person
  versions:
  - name: v1
    served: true
    storage: true
    schema:
      openAPIV3Schema:
        type: object
        properties:
          spec:
            type: object
            properties:
              name:
                type: string
              age:
                type: integer
              height:
                type: integer
              weight:
                type: integer
              strength:
                type: integer
```

Once created, We can create an instance of the resource using the following specification:
```yaml
apiVersion: api.goodpeople.com/v1
kind: Person
metadata:
    name: shashank

spec:
    name: Shashank
    age: 24
    height: 170
    weight: 1
    strength: 80
```

Once this resource is created, Then we can view all the persons using the following commands:
```bash
kubectl get persons
```
```bash
kubectl get ps
```

To specify using preferred programming language, what will happen when a Person resource is created, We create a Custom Controller.