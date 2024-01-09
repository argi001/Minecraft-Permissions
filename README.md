# PlaylegendPermissions

PlaylegendPermissions ist ein Bukkit-Plugin, das entwickelt wurde, um Berechtigungen und Gruppen auf Minecraft-Servern zu verwalten. Es bietet eine flexible Lösung für die Zuweisung von Gruppen, Berechtigungen und speziellen Funktionen für Spieler.

## Hauptmerkmale

- **Gruppenmanagement**: Erstellen, bearbeiten und verwalten von Benutzergruppen.
- **Berechtigungsverwaltung**: Zuweisen von spezifischen Berechtigungen zu verschiedenen Gruppen.
- **Spielerzuweisung**: Zuweisen von Spielern zu bestimmten Gruppen und Verwalten dieser Zuweisungen.
- **Automatisierte Berechtigungsprüfung**: Ein Scheduler überprüft regelmäßig die Berechtigungen und Gruppenzugehörigkeiten.
- **Intuitive Befehle**: Einfache Befehle für die Verwaltung von Gruppen und Berechtigungen.
- **Lokalisierung**: Unterstützung für mehrere Sprachen.

## Voraussetzungen

Um PlaylegendPermissions zu verwenden, muss Ihr Minecraft-Server folgende Anforderungen erfüllen:

- Minecraft Server (Spigot, Paper o.ä.)
- Java-Version: Java 11 oder höher

## Installation

1. Laden Sie die neueste Version von PlaylegendPermissions von [GitHub Releases](https://github.com/argi001/Minecraft-Permission-Manager/releases/) herunter.
2. Platzieren Sie die heruntergeladene `.jar`-Datei in den `plugins`-Ordner Ihres Minecraft-Servers.
3. Starten Sie den Server neu, um das Plugin zu laden.

## Konfiguration

Nach dem ersten Start des Plugins wird eine Standardkonfigurationsdatei erstellt. Sie können diese Datei bearbeiten, um das Plugin nach Ihren Wünschen anzupassen.

## Befehle und Berechtigungen

- `/pper info`: Zeigt Informationen zur aktuellen Gruppe des Spielers.
- `/pper user <Benutzername> setGroup <Gruppenname>`: Weist einen Spieler einer Gruppe zu.
- `/pper group create <Gruppenname>`: Erstellt eine neue Gruppe.

Berechtigungen:

- `pper.administration`: Erlaubt Zugriff auf alle administrativen Befehle des Plugins.

## Lokalisierung

PlaylegendPermissions unterstützt Mehrsprachigkeit. Sie können die bevorzugte Sprache in der Konfigurationsdatei festlegen.

## Mitwirkende

- Panagiotis Argiros

---
