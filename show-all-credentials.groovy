import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*

// When called without paramters will returns all global credentials
retrieveCredentials()
// To retrieve specific credentials, just pass their ids as parameter.
// For instance to retrieve credentials credId_1 and credId_2, just perform
// such call:
// retrieveCredentials('credId_1','credId_2')
  
def retrieveCredentials(String... credIds) {
  def crendentialsProviders = [:]

  crendentialsProviders['System'] = Jenkins.instanceOrNull.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]
  
  crendentialsProviders['User'] = User.current().properties.find { k,v ->
    k instanceof com.cloudbees.plugins.credentials.UserCredentialsProvider$UserCredentialsProperty$DescriptorImpl
  }.value

  crendentialsProviders.each { providerId, credentialsProvider ->
    credentialsProvider.domainCredentials.each { domainCredentials ->
      credentialsProvider.getCredentials(domainCredentials.domain).findAll{ credential ->
        credIds.size() == 0 || credential.id in credIds
      }.each { credential ->
        displayCredential("${providerId}:${domainCredentials?.domain?.name?:'global'}",credential)
      }
    }
  }
  
  // Prevent any returns on console script
  null
  
}

def displayCredential(def domain, def cred) {
  // Closure in charge to display credentials details
  def showRow = { credentialType, domainName, secretId, username = null, password = null, description = null ->
    println("${credentialType} : ".padLeft(20) + (domainName?:'global')?.padRight(20) + "|" + secretId?.padRight(38)+" | " +username?.padRight(20)+" | " +password?.padRight(40) + " | " +description)
  }
  
  cred.with {
    switch(it.class.name) {
      case "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl":
      showRow("user/password", domain, id, username, password?.plainText, description)
      break
        case "com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey":
      showRow("ssh priv key", domain, id, privateKeySource?.privateKey?.plainText, passphrase?.decrypt()?:"", description)
      break
        case "com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl":
      showRow("aws", domain, id, accessKey, secretKey?.plainText, description)
      break
        case "org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl":
      showRow("secret text", domain, id, secret?.plainText, '', description)
      break
        case "org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl":
      showRow("secret file", domain, id, content?.text, '', description)
      break
        case "com.microsoft.azure.util.AzureCredentials":
      showRow("azur", domain, id, subscriptionId, "${clientId}:${hudson.util.Secret.decrypt(clientSecret)}", description)
      break
        case "org.jenkinsci.plugins.docker.commons.credentials.DockerServerCredentials":      
      showRow("docker", domain, id, clientCertificate, clientKey, description)
      break
        case "com.dabsquared.gitlabjenkins.connection.GitLabApiTokenImpl":
      showRow("gitlab", domain, id, apiToken?.plainText, '' , description)
      break      
        default:
        showRow("something else", domain, id, it.class.name, '', description)
    }         
  }

}
