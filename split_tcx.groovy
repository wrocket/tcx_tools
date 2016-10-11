import groovy.xml.XmlUtil

def inFile = this.args[0]

println "Reading ${inFile}..."

def root = new XmlParser(false, false).parse(new File(inFile))
def activitiesNode = root.children().find {it -> it.name().equals('Activities')}
def activites = activitiesNode.children().findAll{it.name().equals('Activity')}

println "Found ${activites.size()} activites."

activites.forEach {activity ->
	activitiesNode.children().clear()
	activitiesNode.children().add(activity)

	def sanitizedId = activity.Id.text().replace(':', '_').replace('.', '_')
	def sport = activity.@Sport
	def fileName = "${sanitizedId}_${sport}.tcx"

	def newXml = new XmlUtil().serialize(root)
	File file = new File(fileName)
    file.write newXml
    println "\tWrote ${fileName}"
}
