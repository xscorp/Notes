## AWS Notes:

* AWS CLI uses AWS SDK internally which further makes call to AWS API.  
```AWS CLI-->AWS SDK-->AWS API```


### HTTPS API Request Signing
All AWS HTTPS requests to AWS API must be signed for authorization and security reasons. The request is signed using the AWS keys(client key and client secret). The request must be signed while sending the HTTPS request manually. When we are using AWS CLI or AWS SDK, our request gets automatically signed using the specified keys.

