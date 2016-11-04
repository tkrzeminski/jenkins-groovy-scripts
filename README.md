# jenkins-groovy-scripts

# Show all credentials

## Run script which shows all credentials:

```bash
java -jar /var/lib/jenkins/.cli/jenkins-cli.jar -s http://localhost:8080 groovy /opt/groovyScripts/show-all-credentials.groovy
```

## Show selected credentials as xml

```bash
java -jar /var/lib/jenkins/.cli/jenkins-cli.jar -s http://localhost:8080 get-credentials-as-xml "SystemCredentialsProvider::SystemContextResolver::jenkins" "(global)" CREDENTIAL ID
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

# Configuring CSP (Content Security Policy)
- Quick reference guide: https://content-security-policy.com/
- CSP evaluator: https://csp-evaluator.withgoogle.com/
- Jenkins Wiki: https://wiki.jenkins-ci.org/display/JENKINS/Configuring+Content+Security+Policy

## Set CSP
```groovy
System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "sandbox allow-same-origin allow-scripts allow-top-navigation;script-src 'unsafe-inline' 'self';default-src 'self'; img-src self data: http: https:; style-src self unsafe-inline; child-src 'self'; frame-src 'self';")
```
## Show CSP value
```groovy
System.getProperty("hudson.model.DirectoryBrowserSupport.CSP")
```
