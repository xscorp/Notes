Finding current domain's forest, DC, children, domain mode, DomainModeLevel , Parent etc, we can use the following method of the .NET class:

Class : ```System.DirectoryServices.ActiveDirectory.Domain```    
Method: ```GetCurrentDomain()```

There are two ways to invoke methods from the class in powershell.

* ```$myclass = [System.DirectoryServices.ActiveDirectory.Domain]```   
  ```$myclass::GetCurrentDomain()```
  
* ```[System.DirectoryServices.ActiveDirectory.Domain]::GetCurrentDomain()```

Powerview version:
The same output can be achieved by using the "Get-NetDomain" method from "PowerView.ps1" module

ADModule version:
A more verbose information about the about the same elements can be obtained by using the "Microsoft.ActiveDirectory.Management.dll" microsoft signed DLL for AD stuff.
> Import-Module .\Microsoft.ActiveDirectory.Management.dll

But, as instructed in the course, sometimes loading just the "dll" doesnt work as expected in interactive powershell environment, so we need to import the "psd1" file, i.e "script with the dll included" like this:
> Import-Module .\ActiveDirectory\ActiveDirectory.psd1
> Get-ADDomain

======================================================================================
When we have enough permissions to do so, we can list information from another domain by using the necessary flags with functions:
Powerview version:
> Get-NetDomain -Domain <domain>
eg: Get-NetDomain -Domain moneycorp.local

ADModule version:
> Get-ADDomain -Identity moneycorp.local

=======================================================================================

To get the unique identifier "SID" of a domain(which can be seen in the Get-ADDomain, we can view that in powerview using:
Powerview:
> Get-DomainSID

ADModule:
> Get-ADDomain | findstr DomainSID
or
> (Get-ADDomain).DomainSID

=======================================================================================

To list Domain Policy:
Powerview:
> Get-DomainPolicy
This method will list all the available policy names. Once you get the policy name, we we can use:
> (Get-DomainPolicy).<policy_name_in_quotes>
example:
> (Get-DomainPolicy)."system access" 
> (Get-DomainPolicy)."Kerberos Policy"

=========================================================================================

For Domain controller enumeration:
Powerview:
> Get-NetDomainController

ADModule:
> Get-ADDomainController


==========================================================================================

For getting a list of users in current domain:
Powerview:
> Get-NetUser
or 
> Get-Netuser -Username <username>

ADModule:
1. For listing default properties of all users:
> Get-ADUser -filter *
Note that -filter flag is mandatory to pass. It gives what users you want to select. In this case, we are selecting all users.

2. For listing all properties of all users:
> Get-ADUser -filter * -Properties *

3. For listing all properties of a specific user:
> Get-ADUser -Identity <username> -Properties *


Powerup:
For further user enumeration, we can use:
> get-userproperty 
and 
> get-userproperty <property_name>

=======================================================================================

To check certain fields in the user enum data:
For example, to search for the term "built" in "Description" field of user data, we can use:

Powerup:
> Find-UserField -SearchField Description -SearchTerm "built"


========================================================================================

To list all computers in the current domain:
Powerview:
> Get-NetComputer
or
> Get-NetComputer -FullData

ADModule:
> Get-ADComputer -Filter *

========================================================================================

To list all domain groups in current domain:
Powerview:
> Get-NetGroup
> Get-NetGroup -FullData

To filter out records that contain the word "admin":
> Get-NetGroup *admin*

ADModule:
> Get-ADGroup -Filter *

To filter out records that contain the word "admin" in "Name" field:
> Get-ADGroup -Filter 'Name -like "*admin*"' | select Name


To get all members of "Domain Admins" group:
Powerview:
> Get-NetGroupMember -GroupName "Domain Admins"
When the displayed member is a group itself, it can be expanded using:
> Get-NetGroupMember -GroupName "Domain Admins" -Recurse

ADModule:
> Get-ADGroupMember -Identity "Domain Admins" -Recursive


To find the groups a specific user is a member of:
Powerview:
> Get-NetGroup -Username <username>

ADModule:
> Get-ADPrincipalGroupMembership -Identity <username>

=================================================================================

To get a list of localgroups in a host, we can use:
Powerview:
> Get-NetLocalGroup -ComputerName <hostname> -ListGroups

=================================================================================


To find all shares in current domain:
Powerview:
> Invoke-ShareFinder -verbose

But this will print some unnecessary and "generally unaccessible" shares like "Print" , "IPC" , "Print" etc.
To exclude them:
> Invoke-ShareFinder -verbose -ExcludeStandard -ExcludePrint -ExcludeIPC

=================================================================================

To find out all the Group Policy objects set by DC:
Powerview:
> Get-NetGPO 

To find out what Group Policy Objects are applied on our system:
Powerview:
> Get-NetGPO -ComputerName <hostname>

or use the following command:
> gpresult /R

================================================================================

To find Restricted Group GPO:
Powerview:
> Get-NetGPOGroup

================================================================================

To find users that are in a local group of a machine using GPO:
> Find-GPOComputerAdmin -Computer <hostname> 

To find computers where the given user is a member of a specific group:
> Find-GPOLocation -Username <username> -Verbose

===============================================================================

To find out all the Organisational Units (OU) inside a domain:
> Get-NetOU 
> Get-NetOU -FullData

To find which GPO is applied to a particular OU:
> Get NetGPO -GPOName <gplink_id>

================================================================================

To get ACLs associated with specific object:
Powerview:
> Get-ObjectAcl -SamAccountName <object_name> -ResolveGUIDs

The "-ResolveGUIDs" creates a list of GUID rights and then automatically selects the needed one. 
Due to this, the commands takes a bit time to return output.

================================================================================

To search for interesting ACEs:
> Invoke-ACLScanner -ResolveGUIDs

Interesting ACEs are the ACE entries that might be useful for an attacker like interesting places with interesting access rights for a user

================================================================================

For mapping domain trusts:
Powerview:
> Get-NetDomainTrust -Domain <domain_name>

ADModule:
> Get-ADTrust -Identity <domain_name>
Note: In ADModule's command, "-Identity" shouldn't be used for current domain. It doesn't work.

================================================================================

For mapping forest trusts:
Powerview:
>Get-NetForestTrust -Forest <forestname>

================================================================================

For mapping trusts within a forest:
Powerview:
> Get-NetForest -Forest <forestname>

ADModule:
> Get-ADForest -Identity <forestname>

================================================================================

For listing all domains in a forest:
Powerview:
> Get-NetForestDomain -Forest <forest_name>

ADModule:
> (Get-ADForest).Domains

=================================================================================



+++++++++++++++++++++++
User hunting
+++++++++++++++++++++++
Up until now, our entire enumeration process was non-noisy and simple.
We were only contacting DC for asking stuff.
But now, we will do "User hunting" which is noisy and requires more interaction than just contacting DC.


==================================================================================

To find all the machines in a domain where the current user has local admin access:
> Find-LocalAdminAccess -Domain <domain_name>

The above command works by asking for a list of computers from DC using Get-NetComputer,
and then issues "Invoke-CheckLocalAdminAccess" command for each host,
which returns whether or not our user has local admin access there.

==================================================================================

To find local admins on all the machines in a domain:
> Invoke-EnumerateLocalAdmin

The above command works by getting a list of computers from DC using Get-NetComputer,
and then querying "Get-NetLocalGroup" on each host to identify Admin groups and users.

==================================================================================

To get session details of a computer:
> Get-NetSession -ComputerName <computername>

==================================================================================

To find all computers with a "Domain Admin" session:
> Invoke-UserHunter

If we want to hunt any other user/group other than "Domain Admin":
> Invoke-UserHunter -GroupName <group_name>
example: >Invoke-UserHunter -GroupName "RDPUsers"

To confirm Local admin access in the listed computers:
> Invoke-UserHunter -CheckAccess

The main aim of "Invoke-UserHunter" is to find such computers where an interesting active logged on session like of "Domain Admins" etc, and to check if we have local admin access in the same machine so that we can do some privesc.
It works by Asking a list of computers(Get-NetComputer) and a list of members in the specified group(Get-NetGroupMember),
then it gets all the logged on sessions(Get-NetSession/Get-NetLoggedon) and then checks if any user with "admin" rights has active session and also checks if we have local admin access there.

To be stealthy and only go for high value targets(High traffic targets like DC, file servers etc):
> Invoke-UserHunter -Stealth

Mitigation:
The most intrusive and yet, powerful attack is the UserHunting attack. To mitigate it, we need to disable it's capability to check sessions on a computer.
This can be done by running the script "NetCease.ps1". The script changes ther permission on "NetSessionEnum" function and removes permission for Authenticated Users.
Due to this, the Invok-UserHunter becomes incapable of getting session details from a system.
To revert the permission change made using NetCease:
> .\NetCease.ps1 -Revert

===================================================================================

