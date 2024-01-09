# PlaylegendPermissions

PlaylegendPermissions ist ein Bukkit-Plugin, das entwickelt wurde, um Berechtigungen und Gruppen auf Minecraft-Servern
zu verwalten. Es bietet eine flexible Lösung für die Zuweisung von Gruppen, Berechtigungen und speziellen Funktionen für
Spieler.

## Hauptmerkmale

- **Gruppenmanagement**: Erstellen, bearbeiten und verwalten von Benutzergruppen.
- **Berechtigungsverwaltung**: Zuweisen von spezifischen Berechtigungen zu verschiedenen Gruppen.
- **Spielerzuweisung**: Zuweisen von Spielern zu bestimmten Gruppen und Verwalten dieser Zuweisungen.
- **Automatisierte Berechtigungsprüfung**: Ein Scheduler überprüft regelmäßig die Berechtigungen und
  Gruppenzugehörigkeiten.
- **Intuitive Befehle**: Einfache Befehle für die Verwaltung von Gruppen und Berechtigungen.
- **Lokalisierung**: Unterstützung für mehrere Sprachen.

## Voraussetzungen

Um PlaylegendPermissions zu verwenden, muss Ihr Minecraft-Server folgende Anforderungen erfüllen:

- Minecraft Server (Spigot, Paper o.ä.)
- Java-Version: Java 11 oder höher
- MySQL Datenbank

## Hilfe zum Erstellen einer MySQL DB über Docker

Speichere die Datei in eine `docker-compose.yml` und starte den container mit `sudo docker compose up -d`
die hier hinterlegten credentials sind die default credentials die in die config geschrieben worden beim initialen
Starten.

```
version: '3.1'

services:

db:
image: mysql
restart: always
ports:
- 3306:3306
environment:
MYSQL_ROOT_PASSWORD: asdlkj3lkjasd43
MYSQL_DATABASE: playlegend
MYSQL_USER: dbo_permission
MYSQL_PASSWORD: ASdlkjflkj3lkjsldf

adminer:
image: adminer
restart: always
ports:
- 8080:8080
```

## Installation

1. Laden Sie die neueste Version von PlaylegendPermissions
   von [GitHub Releases](https://github.com/argi001/Minecraft-Permissions/releases/) herunter.
2. Platzieren Sie die heruntergeladene `.jar`-Datei in den `plugins`-Ordner Ihres Minecraft-Servers.
3. Starten Sie den Server neu, um das Plugin zu laden.
4. Öffnen Sie die Konfigurationsdatei und passen Sie die Datenbank zugangsdaten an.

## Konfiguration

Nach dem ersten Start des Plugins wird eine Standardkonfigurationsdatei erstellt. Sie können diese Datei bearbeiten, um
das Plugin nach Ihren Wünschen anzupassen.

## Befehle und Berechtigungen

- `/pper info`: Zeigt Informationen zur aktuellen Gruppe des Spielers.
- `/pper sign`: Generiert dem ausführenden Spieler ein Schild mit seiner aktuellen Gruppe.
- `/pper user <Benutzername> setGroup <Gruppenname>`: Weist einen Spieler einer Gruppe permanent zu.
- `/pper user <Benutzername> setGroup <Gruppenname> <TimeStamp(1d 1h 1m 1d)`: Weist einen Spieler einer Gruppe temporär
  für den angegegeben Zeitraum zu.
- `/pper creategroup <Gruppenname> <Prefix>`: Erstellt eine neue Gruppe. mit Prefix
- `/pper group <Gruppenname> setprefix`: Setzt das Prefix für eine Gruppe
- `/pper group <Gruppenname> listplayer`: Zeigt die aktuellen Spieler in dieser Gruppe

Berechtigungen:

- `pper.administration`: Erlaubt Zugriff auf alle administrativen Befehle des Plugins.

## Lokalisierung

PlaylegendPermissions unterstützt Mehrsprachigkeit. Sie können die bevorzugte Sprache in der Konfigurationsdatei
festlegen.

- Default: de (deutsch)

## Mitwirkende

- Panagiotis Argiros

---
