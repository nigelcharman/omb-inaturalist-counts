# Old Man's Beard iNaturalist counts

A Groovy script to derive monthly counts of Old Man's Beard patches reported and controlled from a CSV extract of iNaturalist data.

It uses the `Is the pest controlled?` and `Date controlled` observation fields to determine if the patch is controlled. 
You'll need to have a workflow that updates these fields. The `Date controlled` seems to have a number of potential date formats, so the script does some manipulation of this field.




