# jenkins-groovy-scripts

# Show all credentials

## Run script which shows all credentials:

```bash
java -jar /var/lib/jenkins/.cli/jenkins-cli.jar -s http://localhost:8080 groovy     /opt/groovyScripts/show-all-credentials.groovy
```

## Create credentials domain:

```bash
cat /opt/provision/domain.xml | java -jar /var/lib/jenkins/.cli/jenkins-cli.jar -s http://localhost:8080/ create-credentials-domain-by-xml "SystemCredentialsProvider::SystemContextResolver::jenkins"
```

domain.xml content:
```xml
<com.cloudbees.plugins.credentials.domains.Domain>
    <name>GithubCredentials</name>
</com.cloudbees.plugins.credentials.domains.Domain>
```

## Create credentials
```bash
cat /opt/provision/credentials.xml | java -jar /var/lib/jenkins/.cli/jenkins-cli.jar -s http://localhost:8080/ create-credentials-by-xml "SystemCredentialsProvider::SystemContextResolver::jenkins" GithubCredentials
```

credentials.xml content:
```xml
<com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl>
  <scope>GLOBAL</scope>
  <id>jenkins-github-read-write-user</id>
  <description>Github user for RW operations</description>
  <username>someGithubUsername</username>
  <password>typicalSecretPassword</password>
</com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl>
```
