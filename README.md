# DBPDel2
Del 2 av prosjektet i TDT 4145

For oppsett av av JDBC:(UNØDVENDIG)
1. Last ned https://dev.mysql.com/downloads/file/?id=468318
2. Legg mysql-connector-jar i en egen libs mappe
3. Legg til jar-filen som dependency i prosjektet. 

Skal være ok å pulle direkte, siden jar-filen nå ligger ved libs mappen her på github. 

For bruk av programmet: 

1. Ved bruk utenfor NTNU sitt nettverk, bruk VPN for å for å koble opp mot databasen, er ikke nok med PuTTy eller lignende SSH-clienter.
2. Må manuelt skrive inn passordet for databasen i stringen jdbc:mysql://mysql.stud.ntnu.no/renatbec_trening?user=renatbec_dbprosj&password="


