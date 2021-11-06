# OAuth 2.0 workflows
## Authorization Code Flow
![](media/authorization_code_flow.png)

Technical Workflow Diagram of Authorization Code Flow:
![image](https://user-images.githubusercontent.com/37046662/140601012-959b8499-bee5-4ce7-bafc-f90a006253f0.png)  


## Implicit Flow
Implicit Flow is nearly same as the Authorization Code Flow. The difference is that it skips step 5 and step 6 mentioned in Authorization Code Flow implementation. It doesn't assign the client any Authorization Token. In this method, directly the access token is given to the client. Due to unavailibility of Authorization Token, this method is considered as less secure as anybody having the access key can access the resource.
![image](https://user-images.githubusercontent.com/37046662/140601073-8c8c658f-2dd4-4946-8cc6-da1f27f6f370.png)

Please note that in Implicit Flow, there is no back-channel for secure exchange, everything happens using the browser redirects only and hence, makes this grant type more suspectible to attacks.

For detailed information on OAuth 2.0 Grants, visit [OAuth Grant Types - PortSwigger Academy](https://portswigger.net/web-security/oauth/grant-types).  



