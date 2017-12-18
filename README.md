# Roboter Gärtnerei
Dies ist eine Implementierung einer Roboter Gärtnerei

 - master: [![Build Status](https://travis-ci.org/atmaxinger/vpsbcm-ws-2017.svg?branch=master)](https://travis-ci.org/atmaxinger/vpsbcm-ws-2017)
 - development: [![Build Status](https://travis-ci.org/atmaxinger/vpsbcm-ws-2017.svg?branch=development)](https://travis-ci.org/atmaxinger/vpsbcm-ws-2017)

## Prequesites
 - JDK 8
 - Maven
 - Powershell

## Ausführen
 - Ins ```robnur``` Verzeichnis wechseln
 - ```mvn install``` ausführen
 ### Space basierende Implementierung
 - Ins ```spacebased``` Verzeichnis wechseln
    - Hier finden sich 6 Powershell Skripte
        - ```space-server.ps1``` erstellt den Space. Muss als erstes ausgeführt werden!   
        - ```space-gui.ps1``` führt die GUI aus
        - ```space-monitoring.ps1``` führt den Monitoring Roboter aus
        - ```space-plantharvest.ps1``` führt einen Pflanz- und Ernteroboter aus.
        - ```space-research.ps1``` führt einen Forschungsroboter aus
        - ```space-pack.ps1``` führt einen Packroboter aus
    - Bei allen Skripten kann mit dem ```space``` Parameter die URI zum Space geändert werden, falls notwendig
    - Bei Pflanz-, Forschungs- und Packroboter muss zusätzlich noch eine ID angegeben werden.
### Postgress basierende Implementierung
 - ```postgress.properites.sample``` nach ```~/robnur/postgress.properties``` kopieren und ausfüllen.
 - Ins ```postgress``` Verzeichnis wechseln
    - Hier finden sich 6 Powershell Skripte
        - ```pg-init.ps1``` initialisiert die Datenbank. Muss als erstes ausgeführt werden!   
        - ```pg-gui.ps1``` führt die GUI aus
        - ```pg-monitoring.ps1``` führt den Monitoring Roboter aus
        - ```pg-plantharvest.ps1``` führt einen Pflanz- und Ernteroboter aus.
        - ```pg-research.ps1``` führt einen Forschungsroboter aus
        - ```pg-pack.ps1``` führt einen Packroboter aus
    - Bei Pflanz-, Forschungs- und Packroboter muss zusätzlich noch eine ID angegeben werden.
