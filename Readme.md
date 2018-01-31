# GO

## Installatie

Zorg dat je de Java SDK geïnstalleerd hebt. Als je de Tests wil runnen, moet je zorgen dat JUnit 5 is geïnstalleerd. Zo niet, hoef je de tests niet mee te nemen bij het compilen.


Zorg dat je in de src/ map zit, compile dan alle bestanden met het de volgende commando's:

```
find . | grep .java > sources.txt

javac @sources.txt
```

Run daarna de Server (vanuit de src map) met het commando: 
```
java server/controller/GoServer
```

Of de Client (ook vanuit de src map) met het commando: 
```
java client/controller/GoClient
```

Je hoeft geen command line arguments mee te geven.