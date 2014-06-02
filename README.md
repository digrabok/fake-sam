fake-sam
========

ServerAuthModule (JSR 196) implementation allow configure remoteUser (javax.servlet.http.HttpServletRequest#getRemoteUser) and list of user security groups in provider properties..

Usage (Glassfish 3)
-------------------
Put jar with SAM in glassfish lib directory ($AP_HOME/glassfish/lib).
Connect to Glassfish admin panel and in security configuration page add HttpServlet based Message Security provider:

![Edit Provider Configuration](https://cloud.githubusercontent.com/assets/2523981/3148187/cd916ba8-ea5d-11e3-807a-2d933ee193de.png)

In properties set <strong>login</strong>, <strong>groupNames</strong> and <strong>delimiter</strong>