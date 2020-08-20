# Broken Access Control
Broken Access Control is bad implementation of access control functionality that lets a user to perform activities that aren't intended or that they shouldn't be able to do.

## How to test?
* Check for robots.txt, chances are, some sensitive functionality might be listed there.

* "Security by Obscurity" might be there. That means sensitive locations might be named with an unguessable name like "/admin-panel-1231423". Sometimes, these URLs get leaked in source code or are generated dynamically using javascript code when admin logs in. Hence, we should throughly check source code and dynamically generated tags (in DOM) for links to resources. 
  
  In one lab exercise of portswigger academy, "Home" button was holding different link for different users, for normal users, it was /home, for admin, it was /admin-<something>. Since this was being dynamically generated according to the logged in user, we could easily see the javascript by inspecting the element of "Home" button.

* Check cookies! Chances are, resource access might be based on the value of a cookie. So check cookie, manipluate it, play with it. If it's encoded, decode it.

* Sometimes, JSON data is being passed in a request. There are chances that there is a presence of certain parameters that manage authorization. Check for them.

  For example, In one lab exercise of portswigger academy, on invoking the email-change functionality, it was sending the following JSON data in the request:

  ```{"email":"new_email@gmail.com"}```

  Attacker can try injecting variables like:

  ```{"email":"new_email@gmail.com", "isAdmin":"true"}``` or ```{"email":"new_email@gmail.coM", "role_id":0}```
  
* Sometimes, users/groups are restricted from accessing a sensitive resource using URL-filtering. Something like:
  
  ```DENY /admin/deleteUser, managers``` => This filtering rule will check if the URL requested by any user with the role ```managers``` is ```/admin/deleteUser``` and if it matches, it will restrict access to that resource.
    
  In such cases, attacker can make use of non-standard headers ```X-Original-URL``` or ```X-Rewrite-URL``` which lets a user modify the original resource URL. Attacker can access that restricted resource using:
    
  ```
  GET / HTTP/1.1
  X-Original-URL: /admin/deleteUser
  ```
    
  or
    
  ```
  GET / HTTP/1.1
  X-Rewrite-URL: /admin/deleteUser
  ```
    
  But please note that these headers are non-standard and are only allowed in some frameworks. Rest don't support it.

* Sometimes, access control is only implemented to specific methods. For an example, if querying using POST method to /changePassword is restricted, attacker can try changing request method to bypass access control.

  ```
  POST /changePassword HTTP/1.1
  ...
  new_password=hello
  ```
  
  to
  
  ```
  GET /changePassword?new_password=hello HTTP/1.1
  ```
  
  
