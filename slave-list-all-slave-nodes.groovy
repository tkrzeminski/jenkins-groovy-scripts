import hudson.model.*
import hudson.plugins.ec2.*

def printInstanceInfo = { Slave item ->

  computer = item.computer

  if(item instanceof EC2AbstractSlave) {
    i = computer.describeInstance()

    println computer.name.padRight(30).replaceAll("\\(.*\\)","") + " " + i.getPrivateIpAddress().padRight(15) + " ${i.getInstanceId()} ${i.getImageId()} " + i.getInstanceType().padLeft(10) +" ${i.getLaunchTime().format('YYYY-MM-dd HH:mm')}" +  (computer.offline ? " (OFFLINE)" : "")
  } else {
    println computer.name.padLeft(30)+" ${computer.hostName}"  + (computer.offline ? " (OFFLINE)" : "")
  }

}


Hudson.instance.slaves.each { printInstanceInfo(it) }

return
