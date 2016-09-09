# jenkins-groovy-scripts

Run script with command:

```bash
java -jar /var/lib/jenkins/.cli/jenkins-cli.jar -s http://localhost:8080 groovy     /opt/groovyScripts/show-all-credentials.groovy
```


Create credentials domain:

```bash
cat /opt/provision/domain.xml | java -jar ./.cli/jenkins-cli.jar -s http://localhost:8080/ create-credentials-domain-by-xml "SystemCredentialsProvider::SystemContextResolver::jenkins"
```

domain.xml content:
```xml
<com.cloudbees.plugins.credentials.domains.Domain>
    <name>GithubCredentials</name>
</com.cloudbees.plugins.credentials.domains.Domain>
```
