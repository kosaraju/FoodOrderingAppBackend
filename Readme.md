
### Instructions to setup the project are as follows:

fork https://github.com/kosaraju/FoodOrderingAppBackend

open project in intellij and chose folder FoodOrderingAppBackend

start postgress and create db with name "restaurantdb"
make db user name as "postgress" and password as "postgres"

FoodOrderingAppBackend/FoodOrderingApp-db/src/main/resources/config/localhost.properties
should have server.port=54321 server.host=localhost database.name=restaurantdb database.username=postgres database.password=posgres

on terminal or as run configuration in FoodOrderingAppBackend/FoodOrderingApp-db/

run mvn command -  clean install -Psetup -DskipTest

it will create tables in db

FoodOrderingAppBackend/FoodOrderingApp-api/src/main/resources/application.yaml should have
driverClassName: org.postgresql.Driver url: jdbc:postgresql://localhost:54321/restaurantdb username: postgres password: postgres

I had to comment test classes so that the build can be success
on intellij terminal go to FoodOrderingAppBackend and run mvn clean install -DskipTests
Run  Application
FoodOrderingAppBackend/FoodOrderingApp-api/src/main/java/com/upgrad/FoodOrderingApp/api/FoodOrderingAppApiApplication.java

open url http://localhost:8080/api/swagger-ui.html
