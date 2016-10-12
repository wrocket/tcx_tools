// The MIT License (MIT)

// Copyright (c) 2016 Brian Wray (brian@wrocket.org)

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

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

println "Done."
