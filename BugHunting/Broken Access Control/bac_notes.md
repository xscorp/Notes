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
  
* If any kind of role/user_id related data is present in URL or is being sent as POST data, try manipulating it and see if you can access data of other users. This is called **Horizontal Privilege Escalation** as we access data of another user who has **same privilege** as ours.

* Sometimes, applications don't use simple UIDs like 0,1,2, instead, they use GUIDs which are unpredictible like "a3dds-4dsf4-dsf4-43sd". In that case, attacker needs to find out GUIDs of other users. It might be exposed in other parts of the application like Comment section and authors section in a website.

* Many applications detect if you are trying to access any restricted resource and redirects you to either login page or some other page. Often that redirection logic is flawed and that 302 response reveals the data of requested resource. So, if a redirection is happening whenever a restricted resource is being checked, always intercept that request and see the response in repeater. There is a possibility that we might be able to see the response of requested resource inside that 302 response.

* Often, sensitive functionalities are implemented in multi-steps like:

  Step 1: Fill the form and click on submit.  
  Step 2: Ask for confirmation to submit.  
  Step 3: Perform the operation.  
  
  If this multi-step functionality is not implemented perfectly, attackers can skip one of these steps.
  
  For example, in a portswigger lab, to change permission of a user, you need to go through following three steps:
  
  Step 1: Log in as Administraor(to be able to change permissions).  
  Step 2: Set the permission in /admin  
  Step 3: Confirm the permission change in /admin-roles/  
  
  Ideally, a user should only be able to reach step three when 1 and 2 are completed. Sometimes, developers implement this security check in some of the steps but miss the rest thinking users won't be able to access some specific resource without previos ones.
  
  So in this, first 2 steps were implmented nicely but not the third one:
  
  ```GET /admin HTTP/1.1``` => Access Denied
  
  ```GET /admin-roles HTTP/1.1``` => Unauthorized
  
  ```
  POST /admin-roles HTTP/1.1
  ...
  action=upgrade&confirmed=true&username=wiener
  ```
  
  And it worked! So the reason was first two steps in the multi-step functionality was implemented nicely but not the last one. So the takeaway is, If you ever encounter a multi-step functionality, always check if you able to miss any step and move to higher one.
  
  
