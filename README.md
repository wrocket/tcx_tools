# TCX Tools
## Motivation
I use an older Garmin Forerunner GPS watch to record various outdoor adventures. While I like the hardware very much, the related software tools leave a bit to be desired in regards to reliability.

These are a few little software tools I've written to help me manage my data, generally in the TCX file format. I've only tested them on the output of my Forerunner 305, so your mileage will most certainly vary on your device.

All code here is licensed under the MIT license.

## [split_tcx.groovy](split_tcx.groovy)
This little Groovy script splits TCX files composed of several activities into individual activities, keeping all other markup intact. The software I use to export data from my watch to my computer slurps all the activity data as a single, giant TCX file. I usually only need to import the last activity file into Strava (or other platforms), thus this script.

### Example: Split a large file into individual activities
 
    $ groovy split_tcx.groovy some_large_file.tcx 
    Reading some_large_file.tcx...
    Found 12 activities.
	    Wrote 2016_09_22T23_31_05Z_Running.tcx
	    Wrote 2016_09_23T16_21_37Z_Running.tcx
	    Wrote 2016_09_26T17_30_34Z_Running.tcx
	    Wrote 2016_09_27T16_46_19Z_Biking.tcx
	    Wrote 2016_09_30T13_31_00Z_Biking.tcx
	    Wrote 2016_10_02T17_08_26Z_Running.tcx
	    Wrote 2016_10_03T11_55_40Z_Running.tcx
	    Wrote 2016_10_06T16_24_44Z_Running.tcx
	    Wrote 2016_10_07T17_24_40Z_Running.tcx
	    Wrote 2016_10_09T17_12_45Z_Running.tcx
	    Wrote 2016_10_11T13_42_51Z_Running.tcx
	    Wrote 2016_10_11T13_43_16Z_Biking.tcx
	Done.

### Example: Extract only the most recent activity

    $ groovy split_tcx.groovy some_large_file.tcx --onlylast
    Reading some_large_file.tcx...
    Found 12 activities.
	    Wrote 2016_10_11T13_43_16Z_Biking.tcx
    Done.
