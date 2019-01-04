import hudson.model.*
import hudson.triggers.*

def cleanBuildTriggers = { Actionable job -> 
  if( job instanceof AbstractProject && ! job.getTriggers().isEmpty() ) {
    	println "job: " + job.getClass().getName() + " " + job.name
    	job.getTriggers().each { trig -> 
          	Trigger trigger = trig.value
      		println "  - " + trigger.getClass().getName() + " - " + trigger.getDescriptor().getDisplayName()
          	job.removeTrigger(trigger.getDescriptor())
    	}
  }
}  

Hudson.instance.items.each { cleanBuildTriggers( it ) }

return