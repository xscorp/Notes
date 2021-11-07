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
  
  
  
## Flawed CSRF protection
There is a ```state``` parameter which works like a CSRF token for the Client App. It makes sure that the request to the callback endpoint of client application is made by the same person who started the OAuth flow. When OAuth flow is started, an unguessable, unique value in ```state``` parameter(which is tied to the current session) is passed to the OAuth authorization server along with other parameters. After this point, each communication between Client App and OAuth server involves passing the same parameter value back and forth. In case an attacker initiates an OAuth flow using a fake or different ```state``` value, the client app will reject the request as the ```state``` parameter will have a different value than the one which is tied to the current session.   
Absense of ```state``` parameter simply means that there is no way for the Client Application or the OAuth server to know whether the request/response is originated from the Client App or anybody else.

### Steps
* Intercept the initial OAuth flow request(authorization request) made to the authorization server.
* Check if there is any state parameter being passed in the request. 
* If there is no state parameter, the OAuth implementation is vulnerable and the impact may vary.


## Leaking authorization codes and access tokens(unvalidated ```redirect_uri```)
```redirect_uri``` is a parameter that is passed during the authorization request. This parameter tells the OAuth authorization server to send authorization code/access token to this specific callback endpoint/url. If this parameter is not validated, the attacker can pass the URL of an attacker control server to capture the authorization code/access token.  
In case of Authorization Code grant, once the attacker has obtained the authorization code, it can simply be passed to the original callback endpoint to gain access. The attacker doesn't need to worry about guessing client secret or the process of exchange of tokens as all of that will be handled by the back-channels.   
To prevent this, OAuth authorization server should always validate the ```redirect_uri``` parameter. Also, the OAuth server should re-validate the ```redirect_uri``` again during the exchange of authorization code with access token to check if the ```redirect_uri``` is same as what was received in the initial authorization request. If not, reject the request otherwise, grant the access token.  

### Steps
* Intercept the authorization request and replace the ```redirect_uri``` parameter with an attacker controlled endpoint URI.
* Check if the attacker has received an access token in the server logs.
* If yes, This means attacker can craft a phishing attack to obtain code/token of other users.
* In case of Implicit grant type, once the token is obtained, it can be used for making API request to the resource server to fetch the user data.
* In case of Authorization Code grant type, the attacker simply needs to send the token to the original callback endpoint(original redirect_uri). And the attacker will get access.  
  
It is adviced to have a whitelist based approach for allowed ```redirect_uri``` values. But sometimes, the validation logic can still be exploited. Often developers implement the validation in a way which checks if the ```redirect_uri``` parameter starts with a specific domain or string, in that case, we can exploit is by using that allowed domain name as a subdomain in attacker controlled domain.  
Allowed redirect_uri(must start with) => ```https://client-app.com```  
Attacer => ```https://client-app.com&@attacker.com```  


