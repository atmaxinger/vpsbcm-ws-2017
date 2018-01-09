# Roboter Gärtnerei
Dies ist eine Implementierung einer Roboter Gärtnerei

## Prequesites
 - JDK 8
 - Maven
 - Powershell

## Ausführen
 - Ins ```robnur``` Verzeichnis wechseln
 - ```mvn install -DskipTests=true``` ausführen
 ### Space basierende Implementierung
 - Ins ```spacebased``` Verzeichnis wechseln
    - Hier finden sich 9 Powershell Skripte
        - ```space-server.ps1``` erstellt den Space. Muss als erstes ausgeführt werden!   
        - ```space-gui.ps1``` führt die GUI aus
        - ```space-customer.ps1``` führt eine Kunden GUI aus
        - ```space-monitoring.ps1``` führt den Monitoring Roboter aus
        - ```space-plantharvest.ps1``` führt einen Pflanz- und Ernteroboter aus.
        - ```space-research.ps1``` führt einen Forschungsroboter aus
        - ```space-pack.ps1``` führt einen Packroboter aus
        - ```space-foster.ps1``` führt einen Pflegeroboter aus
        - ```test.ps1``` führt alle Roboter (außer Monitoring) mehrere Male + mehrere Kunden GUIs + die normale GUI aus

    - Bei allen Skripten kann mit dem ```space``` Parameter die URI zum Space geändert werden, falls notwendig
    - Bei Pflanz-, Forschungs-, Pack- und Pflegeroboter muss zusätzlich noch eine ID angegeben werden.
### Postgress basierende Implementierung
 - ```postgress.properites.sample``` nach ```~/robnur/postgress.properties``` kopieren und ausfüllen.
 - Ins ```postgress``` Verzeichnis wechseln
    - Hier finden sich 9 Powershell Skripte
        - ```pg-init.ps1``` initialisiert die Datenbank. Muss als erstes ausgeführt werden!   
        - ```pg-gui.ps1``` führt die GUI aus
        - ```pg-customer.ps1``` führt die GUI aus
        - ```pg-monitoring.ps1``` führt den Monitoring Roboter aus
        - ```pg-plantharvest.ps1``` führt einen Pflanz- und Ernteroboter aus.
        - ```pg-research.ps1``` führt einen Forschungsroboter aus
        - ```pg-pack.ps1``` führt einen Packroboter aus
        - ```pg-foster.ps1``` führt einen Pflegeroboter aus
        - ```test.ps1``` führt alle Roboter (außer Monitoring) mehrere Male + mehrere Kunden GUIs + die normale GUI aus
    - Bei Pflanz-, Forschungs-, Pack- und Pflegeroboter muss zusätzlich noch eine ID angegeben werden.
