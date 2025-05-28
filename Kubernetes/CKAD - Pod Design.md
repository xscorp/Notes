## CKAD Preparation - Pod Design

### Other Deployment Strategies

We already know of `RollingUpdate` and `Recreate` strategy. There are other strategies used in kubernetes, for which there is no direct way of implementing them.

* **Rolling Update Strategy**: New pods are created and older pods are destroyed few at a time to avoid downtime.
* **Recreate Strategy**: All the older pods are destroyed and new pods are created, can lead to temporary downtime
* **Blue/Green Strategy**: Half of the traffic is routed to older pods (blue) and half to new ones (green). Post testing, all the traffic is moved to the newer (green) pods.
* **Canary Strategy**: Most of the traffic is routed to older pods only and a small fraction to new pods. Once everything checks out, All the traffic is routed to newer pods.

#### Implementation of Blue/Green and Canary Strategy:

Consider a scenerio where a deployment `D1` with multiple pods is exposed via a service `S1`. To implement the blue/green strategy, A new deployment `D2` is created with the same selector labels as that of `D1`. In case service `S1` is configured with selectors only for `D1`, They are modified with a common set of selectors that can select both `D1` and `D2`. With this, now half of the traffic is being routed to `D1` and half to `D2`. Once tested, The `D1` can be deleted safely.

The Canary strategy is almost same. The only difference is in the number of pods is the newer deployment. To route less traffic to the newer pods, replica count is set to very low.


<br/><br/>

### Job in Kubernetes

Kubernetes Replica sets (or pods in general) are designed to keep the pods running. If a pod finishes/crashes, It gets restarted. But what if the task meant for the pod is short lived - like execution of a command or process and write to a DB and exit? Thats where `Job` comes into play.

To create a job, The pod definition file is passed inside `spec.template`. The only new addition is `restartPolicy: Never` which by default in all pods is set to `Always`.

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: my-job
spec:
  template:
    spec:
        containers:
        -  image: nginx
            name: nginx-container
        restartPolicy: Never
```

Similar to `replicas` in replica sets, We have `completions` and `parallelism` in jobs.

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: my-job
spec:
  completions: 2    # Atleast 2 completions required, spawn as much as you want
  parallelism: 3    # Run 3 pods at once, default is one at a time.
  backoffLimit: 5   # Wait for 5 seconds, to make sure pod doesn't get destroyed even before the job finishes.
  template:
    spec:
        containers:
        -  image: nginx
            name: nginx-container
        restartPolicy: Never
```


<br/><br/>

### Cronjob in Kubernetes

To create a cron job in kubernetes, Provide a schedule and the Job template like this:
```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: my-cron-job
spec:
  jobTemplate:
    spec:
      completions: 3
      parallelism: 4
      backoffLimit: 5
      template:
        spec:
          containers:
            - name: my-container
              image: nginx
          restartPolicy: Never
  schedule: "30 11 * * *"
```

> Note that Cronjob spec contains a Job spec which contains a Pod spec. This can be confusing so it should be written carefully.