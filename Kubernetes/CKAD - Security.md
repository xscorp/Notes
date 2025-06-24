
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

To avoid passing a custom config file repeatedly, We can set the `$KUBECONFIG` env variable instead of overwriting the default config