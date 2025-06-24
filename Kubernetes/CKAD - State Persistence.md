## CKAD Preparation - State Persistence

### Persistent Volume (PV) and Persistent Volume Claim (PVC)

As in docker, when a pod is terminated, Any stored item in it is deleted. For the state to persist, A persistent volume can be attached to the pods. 

To create a persistent volume:
```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv-log

spec:
  accessModes:
    - ReadWriteMany

  capacity:
    storage: 100Mi

  hostPath:
    path: /pv/log

  persistentVolumeReclaimPolicy: Retain
```

Once a persistent volume is created, It can be used by multiple pods by creating a persistent volume claim.

To create a Persistent Volume Claim (PVC):
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: claim-log-1

spec:

  accessModes:
    - ReadWriteOnce

  resources:
    requests:
      storage: 50Mi
```

Once the PVC is created, It can be used in a pod by specifying the PVC in pod definition like this:
```yaml
  volumes:
  - persistentVolumeClaim:
      claimName: claim-log-1
    name: webapp-vol
```

