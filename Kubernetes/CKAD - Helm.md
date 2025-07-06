
## CKAD Preparation - Helm

### Helm Charts in Kubernetes

Helm is a package manager for kubernetes. A "package" is nothing but an application which is made up of different objects of different resource types like deployment, services, secrets, etc. Kubernetes treats each of these things as individual objects rather than a part of a single application. To make sure we don't need to unnecessarily manage tonnes of definition files, Helm manages them for us. Helm allows us to "templatize" our specification files and allows us to specify values of certain fields from a different file to keep things moduler.

To create helm charts, We write our spec files in the form of templates with values referenced to `values.yaml`. We also create a `Chart.yaml` which contains the metadata about the overall application. The directory tree looks like this:
```
├── Chart.lock
├── Chart.yaml
├── README.md
├── charts
│   └── common
│       ├── Chart.yaml
│       ├── README.md
│       ├── templates                   # .tpl files contain template for entire logic instead of a specific property/field
│       │   ├── _affinities.tpl
│       │   ├── _capabilities.tpl
│       │   ├── _compatibility.tpl
│       │   ├── _errors.tpl
│       │   └── validations
│       │       ├── _cassandra.tpl
│       │       ├── _mariadb.tpl
│       └── values.yaml
├── templates                           # Contains the templated YAML files
│   ├── NOTES.txt
│   ├── deployment.yaml
│   ├── extra-list.yaml
│   ├── health-ingress.yaml
│   ├── deployment.yaml
├── values.schema.json
└── values.yaml                         # Contains the variables to be filled in the YAML files
```

* The `templates/` directory is where the templated YAML files are stored, values of which are supplied via `values.yaml` file.
* The `charts/` directory contains *Template Partials* used for reusable logic and functions written in Go template syntax.


To search available packages on Artifact Hub (Similar to DockerHub for container images), use:
```bash
helm search hub <keyword>
```

Example:
```bash
helm search hub wordpress
```

After searching, Once a help page link is identified, It contains info on how to install that specific package. Installing a package requires you to first add the associated repo with it.

Adding a remote repo:
```bash
helm repo add <preferred-repo-name> <repo-url>
```

Example:
```bash
helm repo add bitnami https:/charts.bitnami.com/bitnami
```

Once the repo is added, we can then install the package using:
```bash
helm install <preferred-package-name> <repo-name>/<package-to-install>
```

Example:
```bash
helm install my-nginx bitnami/nginx
```

Similarly, to uninstall,
```bash
helm uninstall <package-name>
```

Example:
```bash
helm uninstall bravo
```

Other than this, once a repo is added, we can search that repo for other packages:
Searching packages in the added repos
```bash
helm search repo <package-name>
```


Instead of directly installing, If we only want to pull the necessary charts for a package, we can do it via `pull` command.
Pulling a helm chart locally
```bash
helm pull <repo-name>/<package-name> -d <output-path>
```

Example:
```bash
helm pull bitnami/apache -d /root/
```

Post making modifications (if needed), We can then directly install from the pulled package.
Installing from the pulled helm chart
```bash
tar xvzf ./apache-11.3.2.tgz
cd <extracted-directory>
helm install mywebapp .
```


<br/><br/>

### Creating a helm chart


