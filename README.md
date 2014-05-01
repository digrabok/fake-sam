fake-sam
========

ServerAuthModule (JSR 196) implementation allow configure remoteUser (javax.servlet.http.HttpServletRequest#getRemoteUser) and list of user security groups in runtime, without app server restart.

Usage (Glassfish 3)
-------------------
Put jar with SAM in glassfish lib directory ($AP_HOME/glassfish/lib).
Connect to Glassfish admin panel and in security configuration page add HttpServlet based Message Security provider:
![Edit Provider Configuration](https://cloud.githubusercontent.com/assets/2523981/2850742/fa9d0054-d107-11e3-84f4-e0757a75f40d.png)
In properties set <strong>login</strong> and <strong>groupNames (comma separated list)</strong>