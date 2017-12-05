param(
        [Parameter(Mandatory=$true)][int]$id
)

$Host.UI.RawUI.WindowTitle = "Postgres Research Robot " + $id

mvn compile exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots.PostgresResearchRobot" -D"exec.args"="re$id"