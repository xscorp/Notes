* Creating a docker

```docker container run <image_name>```


* Detach(run in background):

```docker container run -d <image_name>```


* Run and spawn shell(get inside it):

```docker container run -it <image_name> /bin/bash```
[Here '-it' is for "interactive" and "pseudo-tty" shell]


* Execute commands(or get shell) inside a running docker:

```docker container exec -it <container_id> <command>```


* Stop a container:

```docker container stop <container_id>```


* Pause a container(might be used for temperory stop):

```docker container pause <container_id>```


* Unpause a paused container:

```docker container unpause <container_id>```


* Forward port "x" of container to port "y" of main host:

```docker container -p y:x <docker_image>```


* Kill a docker container(stop sends "SIGTERM" and then "SIGKILL" , while kill command sends just "SIGKILL", In short, "stop" is polite while "kill" is vigrous):

```docker container kill <container_id>```


* Stop all containers:

```docker container stop $(docker container ls -aq)```


* Remove all "non-Up" containers:

```docker container prune -f```


* Create docker container("run" command pulls, creates and runs the command supplied), while "create" just does a part of the process. It pulls and creates the docker but does not run it:

```docker create <image_name>```


* Start a created container:

```docker start <container_id>```


* See the changes done on the image(compare core image to the changes done in the container):

```docker container diff <container_id>```

  This command will show output using codes like:
	C: Created
	A: Added
	D: Deleted


* Copy files from host system to docker:

```docker container cp <file_path_in_host> <container_id>:<file_path_in_container>```


