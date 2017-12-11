#!/bin/bash

numberOfRobots=3

sh run-script-mac.sh "pwsh pg-gui.ps1"
sh run-script-mac.sh "pwsh pg-monitoring.ps1"

# start plant and harvest robots
for i in $(seq 1 $numberOfRobots);
do
    sh run-script-mac.sh "pwsh pg-plantharvest.ps1 -id $i"
done

# start packing robots
for i in $(seq 1 $numberOfRobots);
do
    sh run-script-mac.sh "pwsh pg-pack.ps1 -id $i"
done

# start research robots
for i in $(seq 1 $numberOfRobots);
do
    sh run-script-mac.sh "pwsh pg-research.ps1 -id $i"
done
