import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl
import org.jenkinsci.plugins.plaincredentials.StringCredentials
import org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl

def showRow = { credentialType, secretId, username = null, password = null, description = null ->
  println("${credentialType} : ".padLeft(20) + secretId?.padRight(38)+" | " +username?.padRight(20)+" | " +password?.padRight(40) + " | " +description)
}

// set Credentials domain name (null means is it global)
domainName = null

credentialsStore = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]?.getStore()
domain = new Domain(domainName, null, Collections.<DomainSpecification>emptyList())

credentialsStore?.getCredentials(domain).each{
  if(it instanceof UsernamePasswordCredentialsImpl)
    showRow("user/password", it.id, it.username, it.password?.getPlainText(), it.description)
  else if(it instanceof BasicSSHUserPrivateKey)
    showRow("ssh priv key", it.id, it.passphrase?.getPlainText(), it.privateKeySource?.getPrivateKey()?.getPlainText(), it.description)
  else if(it instanceof AWSCredentialsImpl)
    showRow("aws", it.id, it.accessKey, it.secretKey?.getPlainText(), it.description)
  else if(it instanceof StringCredentials)
    showRow("secret text", it.id, it.secret?.getPlainText(), '', it.description)
  else if(it instanceof FileCredentialsImpl)
    showRow("secret file", it.id, it.content?.text, '', it.description)
  else
    showRow("something else", it.id, '', '', '')
}

return
