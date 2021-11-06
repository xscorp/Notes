# Hunting OAuth 2.0 vulnerabilities

## Improper implementation of the implicit grant type
OAuth is often used for authentication via social media services. For an example, a client application may use OAuth to get your email from facebook API and use that email to authenticate. This is how the workflow looks like-  
```Client App <----> User(Browser) <----> Facebook Resource Server```

Since every data exchange happens through the web browser, attacker can manipulate the data sent/received by Client App/Resource Server. In this case, when Facebook Resource Server returns the email of the user(say user@website.com), it is sent to the Client App through browser(user) using javascript and redirects. The user can intercept the request in which the email is being sent to the Client App and modify the email to any arbitrary email(say user2@website.com), and the Client App will automatically trust it in case there is no validation(email to access_token mapping) in place.  
While implementing the OAuth, the Client App code should always check if the data received from the resource server is actually associated with the access_token used, if not, simply reject it.

### Steps
* Intercept the request where the required user data(email in this case) is being sent to the Client App.
* Modify the user data and send the request
* If no response is being printed, use view response in browser feature of Burp.
