# OAuth 2.0 workflows
## Authorization Code Flow
![](media/authorization_code_flow.png)


## Implicit Flow
Implicit Flow is nearly same as the Authorization Code Flow. The difference is that it skips step 5 and step 6 mentioned in Authorization Code Flow implementation. It doesn't assign the client any Authorization Token. In this method, directly the access token is given to the client. Due to unavailibility of Authorization Token, this method is considered as less secure as anybody having the access key can access the resource.
