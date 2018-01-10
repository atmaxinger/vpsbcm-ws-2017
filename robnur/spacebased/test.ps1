param(
        [int]$count=3
)

#start powershell .\space-server.ps1
start powershell .\space-gui.ps1
start powershell .\space-monitoring.ps1

for($i=1; $i -le $count; $i++) {
    start powershell ".\space-plantharvest.ps1 -id $i"
}

for($i=1; $i -le $count; $i++) {
    start powershell ".\space-pack.ps1 -id $i"
}

for($i=1; $i -le $count; $i++) {
    start powershell ".\space-research.ps1 -id $i"
}

for($i=1; $i -le $count; $i++) {
    start powershell ".\space-foster.ps1 -id $i"
}
