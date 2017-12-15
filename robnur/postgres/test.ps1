param(
        [int]$count=3
)

start powershell .\pg-gui.ps1
start powershell .\pg-monitoring.ps1

for($i=1; $i -le $count; $i++) {
    start powershell ".\pg-plantharvest.ps1 -id $i"
}

for($i=1; $i -le $count; $i++) {
    start powershell ".\pg-pack.ps1 -id $i"
}

for($i=1; $i -le $count; $i++) {
    start powershell ".\pg-research.ps1 -id $i"
}
