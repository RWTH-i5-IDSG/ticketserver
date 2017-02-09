curl -X POST --data '{apiToken:"TICKET_API_TOKEN_3_STRING", begin:"2017-01-08T19:37:20", end:"2018-01-08T19:37:40", name:"Beispielfreitext namens Name", iata:"CGN"}' -H "Content-Type:application/json" -H "Authorization-Key:46fd1c14-a985-4053-bc22-708f45b7d971" -D headers.txt http://localhost:8080/ticket > ticket.png

