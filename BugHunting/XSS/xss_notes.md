# XSS notes

* When input is reflected as value of an attribute, breaking the syntax and injecting a new tag most of the times doesn't work as the angled brackets are escaped/filtered. In such cases, we can try injecting javascript inside an event attribute. Example:

	Lets say "xscorp" is being reflected in source code inside "value" attribute of an \<input\> tag, then we can try injecting using event attributes like:
	
	```" onfocus=alert(1) autofocus x="```
	
	The first double quote will get us out of value attribute and the last x=" is added to balance the ending quote.
  

* Sometimes in websites which have comment section, users are asked to enter their email/website which gets reflected inside href= in an anchor tag in the website. In such cases, attacker can try injecting javascript inside href like:

	```javascript:alert(1)```
	
	Due to this, whenever someone clicks on their profile_link/email/website, XSS will trigger.
	
 
* Websites use ```<link rel="canonical" href="https://xscorp.com" />``` for telling the website crawlers about the original among the "duplicate" pages. If the URL entered in the address bar is reflecting directly inside /<link/> tag, then attacker can break the syntax and inject javascript code using accesskey and onclick attributes with ```" accesskey="x" onclick="alert(1)``` payload like:

	```<link rel="canonical" href="https://xscorp.com " accesskey="x" onclick="alert(1)"```


* If you can inject stuff inside a javascript code like:

	```<script>var x = 'injected_stuff'; document.write("hello");</script>```

	In such cases, attacker can simply try closing the /<script/> tag and then injecting a normal XSS payload:

	```<script>var x = '</script><img src=1 onerror=alert(1)>'; document.write("hello");</script>```

	The syntax will work because before execution of javascript, HTML tag parsing takes place, so our img tag will execute first.


* If the case is same as above and quotes are being escaped using "\" character, sometimes, Developers commit mistake by leaving the backslash character which can be used to bypass quote escaping. If quote is being escaped, passing "\'" will cancel out the backslash and the raw quote will be left.


* In case a WAF or some code has blacklisted rount brackets to prevent attacker from calling functions like alert(), attacker can assign the alert() function to global exception handler and then use the "throw" keyword to pass argument "1" to it like:

	```<img src=1 onerror="alert;throw 1">```
