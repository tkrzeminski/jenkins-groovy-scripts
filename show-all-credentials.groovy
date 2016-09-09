import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl

def showRow = { credentialType, secretId, username = null, password = null, description = null ->
  println("${credentialType} : ".padLeft(20) + secretId?.padRight(38)+" | " +username?.padRight(20)+" | " +password?.padRight(40) + " | " +description)
}


credentialsStore = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]?.getStore()

credentialsStore?.getCredentials(Domain.global()).each{
  if(it instanceof UsernamePasswordCredentialsImpl)
    showRow("user/password", it.id, it.username, it.password.getPlainText(),it.description)
  else if(it instanceof BasicSSHUserPrivateKey)
    showRow("ssh priv key", it.id, it.passphrase.getPlainText(), it.privateKeySource.getPrivateKey(), it.description )
  else if(it instanceof AWSCredentialsImpl)
    showRow("aws", it.id, it.accessKey, it.secretKey.getPlainText(),it.description )
  else
    showRow("something else", id.id)
}

return