import groovy.xml.XmlUtil

def onlyLast = false
def inFile = null

this.args.each { arg ->
	if ('--onlylast'.equals(arg)) {
		onlyLast = true
	} else if (inFile == null) {
		inFile = arg
	} else {
		println("Unknown argument ${arg}")
		println('Usage: split_tcx.groovy [in file] [--onlylast]')
		System.exit(-1)
	}
}

println "Reading ${inFile}..."

def root = new XmlParser(false, false).parse(new File(inFile))
def activitiesNode = root.children().find {it -> it.name().equals('Activities')}
def activities = activitiesNode.children().findAll{it.name().equals('Activity')}

println "Found ${activities.size()} activites."

activities = activities.sort {activity ->
	activity.find {c -> c.name().equals('Lap')}.@StartTime
}

def outputSet = onlyLast ? [activities[-1]] : activities

outputSet.forEach {activity ->
	activitiesNode.children().clear()
	activitiesNode.children().add(activity)

	def sanitizedId = activity.Id.text().replaceAll(/\W+/, '_')
	def sport = activity.@Sport
	def fileName = "${sanitizedId}_${sport}.tcx"

	File outFile = new File(fileName)
    outFile.write(new XmlUtil().serialize(root))
    println "\tWrote ${fileName}"
}
