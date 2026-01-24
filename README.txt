Conversion rate app

Technologies used :-
Spring boot,java,h2 db,posgres db,mvn,docker,swagger,intellij IDE.

Project contains API for below services.
	1.Get a list of all available currencies
						-/api/v1/currencies
    2.Get all EUR-FX exchange rates at all available dates as a collection
						-/api/v1/conversion-rates
    3.Get the EUR-FX exchange rate at particular day
						-/api/v1/conversion-rates/date/{date}						
	4.Get a foreign exchange amount for a given currency converted to EUR on a particular day
						-/api/v1/conversion-rates/currency/{currencyCode}/date/{date}


Steps for execution :

1.Clone the repository from below URL :
			https://github.com/Aditee261192/java-coding-challenge.git
			
2.Navigate to folder containing docker-compose.yaml in terminal.
3. run "docker-compose up -- build. --This should build and start services.
	Once service started,Bundesbank Daily Exchange Rates service will be called and will load data in databse in few minutes.

3.swagger UI can be viewed on urls as follows :-
http://localhost:8080/swagger-ui/index.html#/


Databse for RedirectUrl can be accessed as below  :
Connection details :
JDBC URL :  jdbc:postgresql://localhost:5432/currencydb
usename : postgres
password : postgres


