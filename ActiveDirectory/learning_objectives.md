++++++++++++++++++++++
Learning Objective 4
++++++++++++++++++++++
1. Enumerate all domains in the moneycorp.local.forest:
2. Map the trusts of the dollarcorp.moneycorp.local domain:
3. Map External trusts in moneycorp.local.forest:
4. Identify external trusts of dollarcorp domain. Can you enumerate trusts for a trusting forest?


++++++++++++++++++++++
Learning Objective 5
++++++++++++++++++++++
1. Exploit a service on dcorp-studentX and elevate privileges to local administrator
2. Identify a machine in the domain where studentX has local administrative access
3. Using privileges of a user on Jenkins on 172.16.3.11:8080, get admin privileges on 172.16.3.11 - the dcorp-ci server.


++++++++++++++++++++++
Learning Objective 6
++++++++++++++++++++++
1. Setup BloodHound and identify a machine where studentX has local administrative access.


++++++++++++++++++++++
Learning Objective 7
++++++++++++++++++++++
1. Domain user on one of the machines has access to a server where domain admin in logged in. Identify:
-The domain user
-The server where the domain is logged in

2. Escalate privileges to Domain Admin
-Using the method above
-Using derivative local admin


++++++++++++++++++++++
Learning Objective 8
++++++++++++++++++++++
1. Dump hashes on the domain controller of dollarcorp.moneycorp.local
2. Using the NTLM hash of krbtgt account, create a Golden ticket
3. Use the Golden ticket to (once again) get domain admin privileges from a machine


++++++++++++++++++++++
Learning Objective 9
++++++++++++++++++++++
1. Try to get command execution on the domain controller by creating silver ticket for:
	HOST service
	WMI

++++++++++++++++++++++
Learning Objective 10
++++++++++++++++++++++
1. Use Domain Admin privileges obtained earlier to execute the Skeleton Key attack


++++++++++++++++++++++
Learning Objective 11
++++++++++++++++++++++
1. Use Domain Admin privileges obtained earlier to abuse the DSRM credential for persistence.


