param(
        [Parameter(Mandatory=$true)][int]$id,
        [string]$space = "xvsm://localhost:9876"
)

$Host.UI.RawUI.WindowTitle = "Space Based Foster Robot " + $id

mvn exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots.SpaceFosterRobot" -D"exec.args"="fo$id $space"