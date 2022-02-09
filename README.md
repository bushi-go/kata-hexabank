# HexaBank

Hexagonal bank kata
[Requirements can be foud here](https://gitlab.com/exalt-it-dojo/katas-java/-/tree/main/BankAccount)

## How to launch you dev environment

## Required software

Java 17 ([eclipse temurin JDK](https://projects.eclipse.org/projects/adoptium.temurin/downloads))

A java compatible ide of your choosing

### CLI

Create a .env file in cli/src/main/resources (follow the example provided)
then run the following commands from the root of the project
````
$ mvn clean package -pl core,cli
$ ./cli-<version>.jar 
````
Then follow the menu ;-p

By default the cli will use the example file in example/cli

### Rest API

Create an .env file following the example in rest/src/main/resources,
and the .env-* files in the deployment/dev-env-rest folder 
then you have two options : 

 - Run as a docker container
````
docker-compose -f deployments/dev-env-rest/docker-compose.yml up --build
````
- Run locally with the data stores up
````
$ mvn clean package -pl core,rest
$ docker-compose -f deployments/dev-env-rest/docker-compose.yml up --build mongodb elastic
$ ./rest/target/rest-<version>.jar
````

The API embeds a swagger accessible at localhost:8080/api/docs/ui by default
