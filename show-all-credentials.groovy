import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*

def showRow = { credentialType, secretId, username = null, password = null, description = null ->
  println("${credentialType} : ".padLeft(20) + secretId?.padRight(38)+" | " +username?.padRight(20)+" | " +password?.padRight(40) + " | " +description)
}

// set Credentials domain name (null means is it global)
domainName = null

credentialsStore = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]?.getStore()
domain = new Domain(domainName, null, Collections.<DomainSpecification>emptyList())

credentialsStore?.getCredentials(domain).each{
  switch(it.class.name) {
    case "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl":
      println it.password.class.name
      showRow("user/password", it.id, it?.username, it?.password?.plainText, it.description)
      break
    case "com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey":
      showRow("ssh priv key", it.id, it.privateKeySource?.privateKey?.plainText, it.passphrase?.decrypt()?:"", it.description)
      break
    case "com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl":
      showRow("aws", it.id, it.accessKey, it.secretKey?.plainText, it.description)
      break
    case "org.jenkinsci.plugins.plaincredentials.StringCredentials":
      showRow("secret text", it.id, it.secret?.plainText, '', it.description)
      break
    case "org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl":
      showRow("secret file", it.id, it.content?.text, '', it.description)
      break
    case "com.microsoft.azure.util.AzureCredentials":
      showRow("azur", it.id, it?.subscriptionId, "${it?.clientId}:${hudson.util.Secret.decrypt(it?.clientSecret)}", it.description)
      break
    case "org.jenkinsci.plugins.docker.commons.credentials.DockerServerCredentials":      
      showRow("docker", it.id, it.clientCertificate, it?.clientKey, it.description)
      break
    default:
      showRow("something else", it.id, it.class.name, '', it.description)
      break
  }
}

return
