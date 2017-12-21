param(
        [Parameter(Mandatory=$true)][int]$id
)

$Host.UI.RawUI.WindowTitle = "Postgres Customer " + $id

mvn compile exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.postgres.PostgresCustomerGUI" -D"exec.args"="$id"