#!/bin/bash

numberOfRobots=3

sh run-script-mac.sh "pwsh space-gui.ps1"
sh run-script-mac.sh "pwsh space-monitoring.ps1"

# start plant and harvest robots
for i in $(seq 1 $numberOfRobots);
do
    sh run-script-mac.sh "pwsh space-plantharvest.ps1 -id $i"
done

# start packing robots
for i in $(seq 1 $numberOfRobots);
do
    sh run-script-mac.sh "pwsh space-pack.ps1 -id $i"
done

# start research robots
for i in $(seq 1 $numberOfRobots);
do
    sh run-script-mac.sh "pwsh space-research.ps1 -id $i"
done
