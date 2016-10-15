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

def cli = new CliBuilder(usage: 'split_tcx.groovy [options] [input file]', header: 'Options:')
cli.help('Print this message')
cli.recent(args:1, argName: 'N', 'Only export the N most recent entries')
cli.onlyLast('Equivalent to -recent 1')
def options = cli.parse(args)

int lastNActivities = 0

if (options.help) {
	cli.usage()
	System.exit(-1)
}

if (options.recent) {
	try {
		lastNActivities = Integer.parseInt(options.recent)
	} catch (NumberFormatException) {
		println "Value '${options.recent}' not a valid number, see -help for more information."
		System.exit(-1)
	}

	if (lastNActivities <= 0) {
		println "Last-N-entries must be positive, see -help for more information."
		System.exit(-1)
	}
} 

if (options.onlyLast) {
	lastNActivities = 1
}

if (options.arguments() == null || options.arguments().size() != 1) {
	println "One (and only one) input file must be specified. Try -help for more information."
	System.exit(-1)
}

def inFile = options.arguments()[0]

println "Reading ${inFile}..."

def root = new XmlParser(false, false).parse(new File(inFile))
def activitiesNode = root.children().find { it.name().equals('Activities') }
def activities = activitiesNode.children().findAll{ it.name().equals('Activity') }

println "Found ${activities.size()} activities."

activities = activities.sort {activity ->
	activity.find {c -> c.name().equals('Lap')}.@StartTime
}

def outputSet = lastNActivities > 0 ? activities.takeRight(lastNActivities) : activities

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
