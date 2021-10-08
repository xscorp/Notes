## AWS Notes:

* AWS CLI uses AWS SDK internally which further makes call to AWS API.  
```AWS CLI-->AWS SDK-->AWS API```


### HTTPS API Request Signing
All AWS HTTPS requests to AWS API must be signed for authorization and security reasons. The request is signed using the AWS keys(client key and client secret). The request must be signed while sending the HTTPS request manually. When we are using AWS CLI or AWS SDK, our request gets automatically signed using the specified keys.


### AWS Credentials Check sequence in SDK
We don't necessarily need to hard code our AWS keys in the source code. The SDK checks for AWS keys in these places in the below sequence:  
* Credentials passed into the client(AWS client)
* Credentials passed into the session(a default session gets created while working with AWS SDK)
* Environment variables
* Credentials file(usually located in ```~/.aws/credentials```) or config file that gets created when we run ```aws configure``` command(usually ```~/.aws/config```)
* Quering Instance Metadata Service in EC2 instance associated with an IAM role(most preferred way of storing credentials). The IAM role will allow us to generate temperory credentials to access AWS APIs. Hence, we don't need to hard code credentials in the source code.


