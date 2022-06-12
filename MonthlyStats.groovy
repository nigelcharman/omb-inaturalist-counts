@Grab('com.xlson.groovycsv:groovycsv:1.3')
import static com.xlson.groovycsv.CsvParser.parseCsv
import java.time.LocalDate

def csv = '//Users/charmann/Downloads/observations-243393.csv' as File

def data = parseCsv(csv.text)
def dateReported
Map<String, Counts> countsByMonth = [:].withDefault {new Counts(0,0)}

@groovy.transform.Canonical
class Counts {
    int reported
    int controlled
}
for(line in data) {
    def observedOn = line.'observed_on'
    if (observedOn) {
        dateReported = LocalDate.parse(observedOn, "yyyy-MM-dd")
    } // else use previous dateReported value
    countsByMonth[yearMonth(dateReported)].reported++

    def isControlled = line.'field:is the pest controlled?'
    def dateControlled = line.'field:date controlled'

    if (isControlled == 'yes' && !dateControlled) {
        countsByMonth['0000-00'].controlled++
    } else if (dateControlled) {
        LocalDate date = toDate(dateControlled)
        countsByMonth[yearMonth(date)].controlled++
    }
}

countsByMonth = countsByMonth.sort()

println "Month\tReported\tControlled\tTotal Reported\tTotal Controlled\tPercent C0ntrolled"

Counts total = new Counts()
countsByMonth.each {
    total.reported += it.value.reported
    total.controlled += it.value.controlled
    Number percentControlled = total.reported ? ((total.controlled / total.reported) * 100).round(1) : 0
    println "$it.key\t$it.value.reported\t$it.value.controlled\t$total.reported\t$total.controlled\t$percentControlled"
}
println()

private String yearMonth(dateReported) {
    return dateReported.toString().substring(0, 7)
}

private LocalDate toDate(dateControlled) {
    def date
    if (dateControlled ==~ /\d\d?\/\d\d?\/\d\d/) {
        date = LocalDate.parse(dateControlled, "d/M/yy")
    } else if (dateControlled ==~ /\d\d?\/\d\d\/\d\d\d\d/) {
        date = LocalDate.parse(dateControlled, "d/MM/yyyy")
    } else if (dateControlled ==~ /\d\d\d\d\/\d\d\/\d\d/) {
        date = LocalDate.parse(dateControlled, "yyyy/MM/dd")
    } else if (dateControlled ==~ /\d\d\d\d-\d\d-\d\d/) {
        date = LocalDate.parse(dateControlled, "yyyy-MM-dd")
    } else {
        date = LocalDate.parse(dateControlled.toString().substring(0, 11), "dd MMM yyyy")
    }
    return date
}


