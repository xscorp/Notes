# List of basic questions that I need to check to clear my basics   

### What are some of the use cases of IAM Roles?  
https://www.javatpoint.com/aws-iam-roles-use-cases 


### What is an instance profile in AWS?  
Instance profile is a container for IAM Roles. In simply words, An instance profile is a kind of role attached to an EC2 instance.
Source: [https://www.youtube.com/watch?v=EVmbnmae3vg]()


### What is path prefix in AWS?  
Path is a friendly name configured to better organize the users and resources etc. A path may look like ```/company/product/engineering/username```. In this way, we can create iam roles and policies to allow users to do something based on that path.  
Source: [https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html]()
   

### What is the difference between ```get-user``` and ```list-users``` command?
The ```get-user``` command outputs information about the specified user. In case no username is provided, it outputs the information about the user whose access key ID is used to sign this request, that simply means the user whose credentials we are using to issue this command.  
On the other end, ```list-users``` outputs list of user under the specified path prefix. If no path prefix is specified, it returns list of all the users in the AWS account.  

