Powershell remoting is of 2 types:
1. One-to-One: It is interactive , Stateful , Runs in a new process. PSSession methods can be used for it's implementation.
2. One-to-Many(Fan-out): Non-Interactive , Parallel command execution. Generally used when we have to execute something remotely on many computers simulaneously. Invoke-Command can be used for it's implementation.

=======================================================================================

If we have admin access on another computer in a domain, to get shell on that domain:
> Enter-PSSession -ComputerName <computer_name> 
It's like PSExec for Powershell Remoting

========================================================================================

To have a stateful(state can be saved) powershell remoting session on the remote system:
> $sess = New-PSSession -ComputerName <computer_name>

Now, whenever you want to get back to that session, just use:
> Enter-PSSession -Session $sess

It's like meterpreter session in metasploit-framework

========================================================================================

For One-to-Many powershell remoting, "Invoke-Command" is used:

To run a command on remote system:
> Invoke-Command -ComputerName <computer_name> -ScriptBlock {<command>}


Let's say (yoyo.ps1):
function hello {
Write-Output "hello from ninja"
}

To run a locally defined function:
> Invoke-Command -ComputerName <computer_name> -ScriptBlock ${function:<function_name>}

In case we want to run a powershell script on remote system:
> Invoke-Command -ComputerName <computer_name> -FilePath <path_to_script>

In case we have a list of computers where we want to execute the commands:
> Invoke-Command -ComputerName (Get-Content <path_to_computers_list>) 

If we have session objects for every session and we wanna execute commands on a session:
> Invoke-Command -Session $sess

=======================================================================================

