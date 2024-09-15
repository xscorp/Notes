## Tips & Tricks to Remember while testing for Broken Authentication

1. Always check for host header injection
2. X-Forwarded-* headers are as important as Host header. So while testing functionalities like reset password, update profile etc, always check for the support of different `X-Forwarded-*` headers such as `X-Forwarded-For` and `X-Forwarded-Host`.
3. Functionalities requring authentication other than the primary authentication page (the login page) are as important as the primary authentication functionality. For an example, If there is a feature to update password, send password reset link, update email or update profile, That attack surface is as important as the main login page.
4. Before blindly attempting to bruteforce just based on status code, response size and keyword match etc, Always test for each and every case with every type of different values in different fields to create a sneaky baseline to identiy the anomaly. Sometimes, one small case alphabet could denote the success of attack.
5. If the bruteforce is really needed for an attack and there is account based locking in place, try to utilize different techniques such as trying the username every alternative time, or keep logging to a known account to reset the lockout functionality.
