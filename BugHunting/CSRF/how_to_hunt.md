Since CSRF is usually found in forms, **enumerate all forms** in a website.

An important note: Always be aware about how "normal behaviour" looks like. That means, to check whether or not a POC/technique worked, you must **be aware how the "successful" response and "failure" response looks like**.

Once found, check how many of them are protected by anti-CSRF token and 
how many are not by simply intercepting the request in burpsuite and 
checking whether the CSRF token is being passed or not.

If an essential service like login/logout/register/update_details etc 
is not protected by anti-CSRF token, work on them rather.

If essential services are protected by CSRF mitigation techniques, 
follow the following steps for enumeration: 

* **Check if removing the CSRF token works**: 
Some websites validates the CSRF token only when it is present, if it is absent, the validation is not performed.

* **Check for token duplicacy**:
Some websites are too lazy to handle CSRF tokens. So, instead of generating a random CSRF token and binding it to session cookie, they simply duplicate the session cookie in CSRF. And then, token validation is performed by checking if the cookie supplied is same as the CSRF token. In this case, attacker can simply pass the value of session cookie as the value of CSRF parameter and check the response.

* **Change the request method(method="post/get")** and check the response because some websites validate CSRF tokens only when a certain method is used.

* **Check if the CSRF token is actually tied to the specific session cookie**:
  1. If changing just the CSRF token shows error about token being invalid, it's normal 
  2. If changing just the session cookie logs you out, then it might be possible that token is not properly tied to the session cookie and just a global and independent of valid CSRF tokens is being maintained in the server. In this case, attacker can simply use his CSRF token in POC because that token is not tied to session and is still a valid token.

* **Check for "Referer" header validation**:
  1. Some websites check the "Referer" header of an HTTP request to detect whether or not the request is originated from same website. If it's not from the same/trusted website, the request is dropped. But sometimes, the "Referer" validation is performed only when the "Referer" header is present in the request, an attacker can try to remove it and check the response. The "Referer" header can be removed using Burpsuite but, to do it in POC, include this tag inside <head>:
  ```<meta name="referrer" content="no-referrer">```

  2. Many times, referer validation is done by checking if the "Referer" header value (contains/starts with/ends with) a particular string(URL):
     Let's say the expected "Referer" header is "http://example.com/user_details", attacker can try things like:
        
        If check is being performed for (contains/starts with) "example.com":
        
        ```"Referer: http://example.com.attacker.com/"```

        If check is being performed for (contains/ends with) "example.com"
        
        ```"Referer: http://attacker.com/example.com```

* **Check all the cookies present in the browser for that website**. Sometimes, the **CSRF token is not tied to a session cookie, but to another non-session cookie**. In that case, attacker can find a way(or find a bug) to introduce his own cookie and then use his own CSRF token in the POC. The session will be handled by session cookie, while the CSRF validation will be handled by the CSRF token and non-session cookie which we have control over.
