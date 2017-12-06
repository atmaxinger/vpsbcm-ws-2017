param(
        [Parameter(Mandatory=$true)][int]$id,
        [int]$plantTimeout = 120*1000,
        [int]$harvestTimeout = 5000
)

$Host.UI.RawUI.WindowTitle = "Postgres Plant and Harvest Robot " + $id

mvn compile exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots.PostgresPlantAndHarvestRobot" -D"exec.args"="ph$id $plantTimeout $harvestTimeout"