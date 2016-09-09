# jenkins-groovy-scripts

Run script with command:

```java -jar /var/lib/jenkins/.cli/jenkins-cli.jar -s http://localhost:8080 groovy     /opt/groovyScripts/show-all-credentials.groovy```


Create credentials domain:

`cat /opt/provision/domain.xml | java -jar ./.cli/jenkins-cli.jar -s http://localhost:8080/ create-credentials-domain-by-xml "SystemCredentialsProvider::SystemContextResolver::jenkins"`

domain.xml content:
```<com.cloudbees.plugins.credentials.domains.Domain>
    <name>4FinaceIT</name>
</com.cloudbees.plugins.credentials.domains.Domain>```
