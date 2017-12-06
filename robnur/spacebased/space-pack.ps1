param(
        [Parameter(Mandatory=$true)][int]$id,
        [string]$space = "xvsm://localhost:9876"
)

$Host.UI.RawUI.WindowTitle = "Space Based Packing Robot " + $id

mvn compile exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots.SpacePackRobot" -D"exec.args"="pa$id $space"