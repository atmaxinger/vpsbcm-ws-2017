param(
        [string]$space = "xvsm://localhost:9876"
)

$Host.UI.RawUI.WindowTitle = "Space Based Monitoring Robot"

mvn compile exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots.SpaceMonitoringRobot" -D"exec.args"="$space"