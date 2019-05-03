# BarTi - Barcode Ticket Generator
Dieses Projekt soll das Ausstellen von [VDV](http://www.eticket-deutschland.de/)-konformen [2D-Barcode](https://de.wikipedia.org/wiki/Aztec-Code)-Tickets mit statischer Berechtigung vereinfachen.
Dazu stellt es einen Web-Service (Modul barti-web) zur Verfügung, der mit einer minimalen Konfiguration genutzt werden kann.
Dieser Dienst kann dann über eine [REST](https://de.wikipedia.org/wiki/Representational_State_Transfer)-Schnittstelle genutzt werden.
Dazu sind lediglich die unter **Nutzung des Web-Service** angegebenen Informationen zu übertragen.
Das generierte und vom SAM signierte Ticket wird als Bild zurückgegeben.

Als dieser Text geschrieben wurde, hat sich der statische produktspezifische Teil des Tickets (vgl. Tabelle 5-81 der KA NM Spec) in der angegebenen Form noch nicht ausreichend etabliert, so dass das erzeugte Ticket an dieser Stelle Freitext enthält.
Jedes Lesegerät, das diesen Umstand erkennen kann und die enthaltenen Informationen nicht weiter zu verarbeiten versucht, sollte in der Lage sein, die mit diesem Projekt erstellten Tickets zu prüfen.

Der Server benötigt eine laufende PostgreSQL-Datenbank, die auch zur Konfiguration der fachlichen Informationen verwendet wird.
Die nötigen Tabellen sind in [V0_0_1__initial.sql](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql) beschrieben und werden zur Compile-Zeit in der Datenbank erstellt.
Zusätzlich finden sich als Einstiegs-Hilfe einige Informationen in [V0_0_2__dummy_data.sql](barti-db/src/main/resources/db/migration/V0_0_2__dummy_data.sql), die die eigentliche Konfiguration demonstrieren sollen.
Diese Informationen sind auf den konkreten Anwendungsfall anzupassen -- dafür muss nicht zwingend ein Migrations-Skript genutzt werden.

Der primäre Anwendungsfall für dieses Projekt besteht in der Bereitstellung eines Web-Dienstes zur Erstellung von VDV-KA Tickets für Dritte (hier "Partner" genannt).
Der Fokus liegt auf der einfachen Nutzbarkeit des Dienstes für die Partner.
Die Partner nutzen für sie erstellte API-Tokens, um die bei der Ticketerstellung zu nutzende fachliche Konfiguration zu identifizieren.
Zusätzlich müssen sie in den Anfragen nur den Gültigkeitszeitraum des Tickets sowie den oben erwähnten Freitext übertragen.

Für den Betreiber des Dienstes existiert eine zusätzliche Schnittstelle, mit deren Hilfe er ein Protokoll über die erzeugten Tickets gruppiert nach Monat und Produktverantwortlichem abrufen kann.
Dieses Protokoll enthält alle relevanten Informationen, die für die Ticketerstellung genutzt wurden; inklusive für welchen Partner ein Ticket erstellt wurde.

Zur Erhöhung der Dienst-Verfügbarkeit wird die Möglichkeit geboten, mehrere Instanzen (deployments) parallel zu betreiben.
Da jedes Ticket eine Ticketnummer enthält, die zusammen mit der OrgID des KVPs global eindeutig sein muss, wird der verfügbare Wertebereich überlappungsfrei auf die Deployments verteilt, so dass diese autark Tickets erstellen können.
Die Datenbanken der verschiedenen Deployments sind synchron zu halten, um den Protokollabruf an einem beliebigen Deployment zu erlauben.
Um beim Protokollabruf sicher sein zu können, dass die Datenbanken synchron sind, wird eine Art täglicher Heartbeat via Datenbank genutzt.
Für einen erfolgreichem Protokollabruf darf der letzte Heartbeat keines Systems älter als das Ende des Protokollzeitraums sein.

## Konfiguration des Web-Service

### Web-spezifische Parameter

| Option | Bedeutung | Standardwert
| ------ | --------- | ------------
| `server.host` | Hostname oder IP des Servers | 127.0.0.1
| `server.gzip.enabled` | Aktivierung der Gzip-Kompression | false
| `http.enabled` | Aktivierung des HTTP-Connectors | true
| `http.port` | HTTP-Port | 8080
| `https.enabled` | Aktivierung des HTTPS-Connectors | false
| `https.port` | HTTPS-Port | 8443
| `keystore.path` | Pfad zum Keystore | n/a
| `keystore.password` | Password des Keystores | n/a

### Datenbank-Verbindung-spezifische Parameter

| Option | Bedeutung
| ------ | ---------
| `db.host` | Hostname oder IP des Servers
| `db.port` | zu verwendende Port-Nummer
| `db.dbname` | Name der Datenbank
| `db.schema` | Name das Schemas
| `db.user` | Nutzername
| `db.password` | Nutzerpasswort

### Fachliche Konfiguration

Für die zu nutzenden SAMs sind die zugehörigen Betreiberschlüssel in Tabelle [betreiber_key](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L1-8) zu hinterlegen.

| Spalte | Bedeutung
| ------ | ---------
| `betreiber_key.chr` | Certificate Holder Reference des Betreiberschlüssels
| `betreiber_key.private_exponent` | Privater Exponent des Betreiberschlüssels
| `betreiber_key.modulus` | Modulus des Betreiberschlüssels

Die KA-relevanten Organisationen sind in Tabelle [organisation](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L10-L15) und die Partner in Tabelle [partner](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L26-31) anzulegen.
Die den Tickets zugrundeliegenden Produkte sind in Tabelle [product](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L17-24) zu hinterlegen.
Pro Partner sind Ticket-API-Token zu "erstellen" und in Tabelle [ticket_api_token](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L33-45) zu hinterlegen, wobei auch KVP, PV und ProduktID mit anzugeben sind.

In die Tickets einzubringende, ggf. vom Deployment abhängige Transaktionsdaten sind in Tabelle [transaction_data](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L56-69) einzutragen.

| Spalte | Bedeutung | Referenz in KA NM Spec
| ------ | --------- | ----------------------
| `transaction_data.transaction_data_id` | nicht-fachlicher Primärschlüssel | n/a
| `transaction_data.transaction_operator_id` | Identifikationsnummer des Operators der Transaktion | Tabelle 2-5, logTransaktionsOperator_ID
| `transaction_data.terminal_number` | Nummer des ausstellenden Terminals | Tabelle 5-3, terminalNummer
| `transaction_data.terminal_org_id` | Organisations-Identifikationsnummer des ausstellenden Terminals | Tabelle 5-3, Organisation_ID.organisationsNummer
| `transaction_data.location_number` | Nummer des Orts der Transaktion | Tabelle 5-11, ortNummer
| `transaction_data.location_org_id` | Zum Ort der Transaktion gehörende Organisations-Identifikationsnummer | Tabelle 5-11, Organisation_ID.organisationsNummer

Die Identifier der vorgesehenen Deployments sind in der Tabelle [deployment](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L71-75) einzutragen.

Die Verknüpfung von Produkt und Deployment mit den Transaktionsdaten geschieht über [deployment_product_to_transaction_data](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L77-L87).
Soll für alle Produkte und Deployments die selben Transaktionsdaten genutzt werden, kann für die Verknüpfung die Hilfs-Funktion [use_transaction_data_for_all_products_and_deployments](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L182-203) genutzt werden.

Für die (interne) Sicherstellung, dass die Kombination aus KVP Org ID und Ticket-Nummer (vgl KA NM Spec Tabelle 5-5) stets eindeutig ist, muss für jedes Deployment ein gültiger Wertebereich (Tabelle [sequence_information](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L89-L99)) für die Ticketnummern definiert werden, der für die Deployments überlappungsfrei zu halten ist (dabei kann die Methode [initialize_deployments_and_sequence_information](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L148-L180) helfen).

Abschließend gilt es, Tokens für den Zugriff auf die Protokolldaten zu erstellen und in die Tabelle [log_api_token](barti-db/src/main/resources/db/migration/V0_0_1__initial.sql#L47-54) einzupflegen.


## Start des Web-Service
Falls Sie nicht bereits ein jar-File zur Verfügung gestellt bekommen haben, lässt sich ein solches über den Befehl
`mvn clean package -P fatJar`
erzeugen und im Ordner barti-web/target finden.
Der Server kann dann mittels
`java -Ddeployment=1 -jar barti-web/target/barti-web-*.jar`
gestartet werden.
Werden mehrere Deployments genutzt, ist die deployment id beim Start entsprechend anzugeben.

## Nutzung des Ticketing-Web-Service
Der Ticketing-Web-Service wird als REST-Schnittstelle auf Port 8080 unter dem Pfad [/ticket](http://localhost:8080/ticket) angeboten und ist via POST mit JSON-kodierten Informationen anzusprechen.
Das erwartete Format der Eingabe ist wie folgt:
```
{
    "apiToken": "TICKET_API_TOKEN_3_STRING",
    "begin": "2016-01-01T06:00:00",
    "end": "2016-01-01T22:00:00",
    "freitext": "vom Prüfgerät ohne weitere Verarbeitung anzuzeigender Text",
    "iata": "aktuell ignoriertes Feld"
}
```
Das `apiToken` identifiziert auf Serverseite die restlichen, unveränderlichen Informationen zum Produkt und wird mit diesen in der Datenbank verknüpft.
Zusätzlich muss der `Authorization-Key` als HTTP-Header angegeben werden. Dieser ist im [Code](barti-web/src/main/java/de/rwth/idsg/barti/web/Constants.java) standardmäßig auf den Wert `46fd1c14-a985-4053-bc22-708f45b7d971` fixiert.
Die Rückgabe besteht aus dem zugehörigen Aztec-Barcode im png-Format.
Falls die statische Berechtigung erfolgreich durch das SAM signiert werden konnte, repräsentiert das zurückgegebene Bild die signierte, statische Berechtigung.
Falls das Ticket nicht erstellt oder nicht signiert werden konnte (z.B. weil kein SAM gefunden werden konnte oder die Authentisierung nicht erfolgreich durchgeführt werden konnte), wird dies durch einen HTTP-Status, der von 200 (OK) verschieden ist, angezeigt.


## Nutzung der Protokollierungsschnittstelle
Die Schnittstelle zum Protokollabruf ist ebensfalls REST-basiert auf Port 8080 unter dem Pfad [/log](http://localhost:8080/log) erreichbar und ist via POST mit JSON-kodierten Informationen anzusprechen.
Das erwartete Format der Eingabe ist wie folgt:
```
{
    "apiToken": "LOG_API_TOKEN_1_STRING",
    "yearMonth": "2016-07"
}
```
Auch hier muss der oben genannte `Authorization-Key` als HTTP-Header angegeben werden.
Die Antwort besteht aus einer tabellarischen Darstellung des Protokolls im CSV-Format mit Header-Zeile, inhaltlich eingeschränkt auf den angegebenen Monat des angegebenen Jahres sowie auf die Produkte des Produktverantwortlichen, zu dem das Token gehört.
Passend dazu wird der Content-Type auf `text/csv` gesetzt und der Header Content-Disposition auf `attachment; filename=tickets-2016-07.csv` (bzw. passend zur Anfrage).
Der Abruf kann nur erfolgreich durchgeführt werden für bereits vergangene Monate.
Zudem schlägt der Abruf fehl, falls der jüngste Heartbeat eines der Deployments nicht aus der Zeit nach dem fraglichen Monat stammt, da dann nicht sichergestellt werden kann, dass alle Ticket-Erstellungen dieses Deployments mit beauskunftet würden.

## Hinweise zum Deployment auf einem RaspberryPi mit Ubuntu Mate
Zusätzlich zu einer Java-Laufzeitumgebung (und ggf. Maven) müssen die Bibliotheken zur Kommunikation mit der Smartcard installiert sein, zB mittels:
<br/>`apt-get install opensc`

Stellen Sie mit Hilfe des folgenden Befehls sicher, dass die Java-Laufzeitumgebung die PC/SC Bibliothek findet:
<br/>`ln -s /lib/arm-linux-gnueabihf/libpcsclite.so.1 /usr/lib/libpcsclite.so`

## Hinweise zum Deployment in einer virtuellen Maschine auf VMware ESXi 
Grundsätzlich ist der Betrieb von BarTi in einer virtuellen Maschine auf Basis von VMware ESXi durch durchreichen des entsprechend Smartcardlesegeräts möglich. Zustätzlich ist die Verwendung eines Daemons der zusätzliche Entropie bereitstellt nötig, z.B. [haveged](http://www.issihosts.com/haveged/), welches unter Ubuntu/Debian mit `apt-get install haveged` installiert werden kann.

Der Betrieb unter VMware ESXi 6.0U2 ist empfohlen. Version 6.5 enthält zwei Änderungen, die eine zusätzliche Konfiguration nötig machen:

- Unterstützung von Smartcards als Zugangsberechtigung.

Damit ein CCID kompatibles Smartcardlesegerät an eine VM durchgereicht werden kann (passthrough), muss folgende Zeile in der Konfigurationsdatei (.vmx) der VM hinzugefügt werden.
<br/>`usb.generic.allowCCID = "TRUE"`

- Neue Treiberarchitektur bzw. neuer USB Treiber.

Die mit VMware ESXi Version 6.5 eingeführten nativen Treiber sind sehr problematisch und können zu Verbindungsabrüchen von USB-Verbindung führen. Es muss auf den ursprünglichen Linux USB Treiber durch Ausführen des folgenden Befehls auf der ESXi Konsole gewechselt werden (Neustart erforderlich):
<br/>`esxcli system module set -m=vmkusb -e=FALSE`

Weitere Informationen: [hier](https://kb.vmware.com/selfservice/microsites/search.do?language=en_US&cmd=displayKC&externalId=2147650) und [hier](https://kb.vmware.com/selfservice/microsites/search.do?language=en_US&cmd=displayKC&externalId=2147565)
