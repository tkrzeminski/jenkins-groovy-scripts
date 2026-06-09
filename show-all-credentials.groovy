import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*

/**
 * Jenkins Credentials Script
 *
 * This script retrieves and displays credentials from various credential providers in Jenkins.
 * It supports System, User, and Folder credential providers across all domains.
 *
 * Supported credential types:
 * - Username/Password
 * - SSH Private Key
 * - AWS Credentials
 * - Secret Text
 * - Secret File
 * - Azure Credentials
 * - Docker Server Credentials
 * - GitLab API Token
 *
 * Usage:
 *   retrieveCredentials()                           // Returns all global credentials
 *   retrieveCredentials('credId_1')                 // Returns specific credential
 *   retrieveCredentials('credId_1','credId_2')      // Returns multiple specific credentials
 *
 * WARNING: This script exposes sensitive credential information including passwords and secrets.
 * Use with caution and ensure proper access controls are in place.
 */

// When called without paramters will returns all global credentials
retrieveCredentials()
// To retrieve specific credentials, just pass their ids as parameter.
// For instance to retrieve credentials credId_1 and credId_2, just perform
// such call:
// retrieveCredentials('credId_1','credId_2')

/**
 * Retrieves and displays credentials from all available credential providers.
 *
 * @param credIds Variable number of credential IDs to filter. If empty, all credentials are displayed.
 *                If provided, only credentials matching the given IDs will be shown.
 * @return null to prevent console output
 */
def retrieveCredentials(String... credIds) {
  def crendentialsProviders = [:]

  // Collect all credential providers from different sources

  // Get system credentials provider
  crendentialsProviders['System'] = Jenkins.instanceOrNull.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]

  // Get User credentials provider
  crendentialsProviders['User'] = User.current()?.properties.find { k,v ->
    k instanceof com.cloudbees.plugins.credentials.UserCredentialsProvider$UserCredentialsProperty$DescriptorImpl
  }?.value

  // Get Folder credentials providers from all folder items in Jenkins
  Jenkins.instance.allItems().findAll { item ->
    item.properties.find { property ->
      property.getClass().name == 'com.cloudbees.hudson.plugins.folder.properties.FolderCredentialsProvider$FolderCredentialsProperty'
    }
  }.each { folder ->
    crendentialsProviders[(folder.fullName)]=folder.properties.find { property ->
        property.getClass().name == 'com.cloudbees.hudson.plugins.folder.properties.FolderCredentialsProvider$FolderCredentialsProperty'
      }
  }

  // Iterate through all credential providers and display matching credentials
  crendentialsProviders.each { providerId, credentialsProvider ->
    // Parse all domain credentials within each provider
    credentialsProvider?.domainCredentialsMap.each { domainCredentials,credentials ->
      // Filter credentials: if credIds provided, match them; otherwise return all
      credentials.findAll{ credential ->
          credIds.size() == 0 || credential.id in credIds
      }.each { credential ->
          displayCredential("${providerId}:${domainCredentials?.name?:'global'}",credential)
      }
    }
  }

  // Prevent any returns on console script
  null

}

/**
 * Displays credential details in a formatted table output.
 *
 * @param domain The domain name where the credential is stored (e.g., "System:global", "Folder:domain")
 * @param cred The credential object to display
 */
def displayCredential(def domain, def cred) {
  // Helper closure to format and display credential information in a tabular format
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
      showRow("azure", domain, id, subscriptionId, "${clientId}:${clientSecret}", description)
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
