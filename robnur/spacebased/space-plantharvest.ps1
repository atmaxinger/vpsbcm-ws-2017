param(
        [Parameter(Mandatory=$true)][int]$id,
        [string]$space = "xvsm://localhost:9876"
)

$Host.UI.RawUI.WindowTitle = "Space Based Plant and Harvest Robot " + $id

mvn compile exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots.SpacePlantAndHarvestRobot" -D"exec.args"="ph$id $space"