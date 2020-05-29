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
