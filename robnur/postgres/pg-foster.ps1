param(
        [Parameter(Mandatory=$true)][int]$id
)

$Host.UI.RawUI.WindowTitle = "Postgres Foster Robot " + $id

mvn compile exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots.PostgresFosterRobot" -D"exec.args"="fo$id"