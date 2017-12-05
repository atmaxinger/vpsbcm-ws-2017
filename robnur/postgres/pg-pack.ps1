param(
        [Parameter(Mandatory=$true)][int]$id
)

$Host.UI.RawUI.WindowTitle = "Postgres Packing Robot " + $id

mvn compile exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots.PostgresPackRobot" -D"exec.args"="pa$id"