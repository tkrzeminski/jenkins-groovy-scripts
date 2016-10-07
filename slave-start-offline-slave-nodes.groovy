import hudson.model.*

def startComputer = { Slave item, String computerName = null ->

  computer = item.computer

  print computer.name.padLeft(30)

  if(( computerName == null || computer.name.startsWith( computerName ) ) && computer.offline) {
    print " found OFFLINE - lauching"
    computer.connect(false)
  }
  println ''
}


Hudson.instance.slaves.each{ startComputer(it) }

return
