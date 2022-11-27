import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*

def showRow = { credentialType, domain, secretId, username = null, password = null, description = null ->
  println("${credentialType} : ".padLeft(20) + (domain?:'global')?.padRight(20) + "|" + secretId?.padRight(38)+" | " +username?.padRight(20)+" | " +password?.padRight(40) + " | " +description)
}

def credentialsProvider = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]
credentialsProvider.domainCredentials.each { domainCredentials ->
  credentialsProvider.getCredentials(domainCredentials.domain).each {
  	switch(it.class.name) {
      case "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl":
        showRow("user/password", domainCredentials.domain.name, it.id, it?.username, it?.password?.plainText, it.description)
        break
      case "com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey":
        showRow("ssh priv key", domainCredentials.domain.name, it.id, it.privateKeySource?.privateKey?.plainText, it.passphrase?.decrypt()?:"", it.description)
        break
      case "com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl":
        showRow("aws", domainCredentials.domain.name, it.id, it.accessKey, it.secretKey?.plainText, it.description)
        break
      case "org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl":
        showRow("secret text", domainCredentials.domain.name, it.id, it.secret?.plainText, '', it.description)
        break
      case "org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl":
        showRow("secret file", domainCredentials.domain.name, it.id, it.content?.text, '', it.description)
        break
      case "com.microsoft.azure.util.AzureCredentials":
        showRow("azur", domainCredentials.domain.name, it.id, it?.subscriptionId, "${it?.clientId}:${hudson.util.Secret.decrypt(it?.clientSecret)}", it.description)
        break
      case "org.jenkinsci.plugins.docker.commons.credentials.DockerServerCredentials":      
        showRow("docker", domainCredentials.domain.name, it.id, it.clientCertificate, it?.clientKey, it.description)
        break
      case "com.dabsquared.gitlabjenkins.connection.GitLabApiTokenImpl":
        showRow("gitlab", domainCredentials.domain.name, it.id, it.apiToken?.plainText, '' , it.description)
        break      
      default:
        showRow("something else", domainCredentials.domain.name, it.id, it.class.name, '', it.description)
  	}         
  }
}
