* Always load a module like:
> . .\modulename.ps1
and not like:
> .\modulename.ps1

The later worked but some functions were not being identified.

===========================================================

* To get a list of all functions in a powershell script,
> cat .\modulename.ps1 | select-string function

===========================================================

SIDs of domains and forests are different. But an admin account(either domain or enterprise) can be identified by looking at it's SID.
The SID of admin accounts are like "<some_random_sid_part>-500".
That "500" in the end denotes that the account is and admin account. And this SID can't be changed.
So even if somebody renames the Admin account from "Administrator" to something else, it can still be identified using SID.

===========================================================

To have a reverse shell in windows environment, we can use the "powercat.ps1" module:
> powercat -v -l -p 443 -t 10000
Here, "-t" denotes the timeout.
Note that we need to keep pressing enter in the listner window after executing the payload as it doesn't show shell poppup without it.

===========================================================


