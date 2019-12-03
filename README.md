# Java Based GraphQL Server Demo

This is a demo application which serves data through a GraphQL query interface over HTTP.

This project uses Spring Boot, Spring Data JPA, HSQLDB, and Graphql-Java. 

## Build and Run
- Build: `./gradlew docker` will compile the project and produce a docker image named 'graphql.server.demo/gqlserver' in your local docker image cache. (This assumes you have Docker installed)
- Build: `./gradlew build' will compile and produce an uber jar at build/libs which can be run with `java -jar gqlserver-0.0.1-SNAPSHOT.jar`
- Run: `docker run -rm -d -p 9002:9002 graphql.server.demo/gqlserver`

## Querying the Server
- GraphQL Playground is a nice app you can install, https://github.com/prisma-labs/graphql-playground, Assuming you ran with the above command, the server is at http://localhost:9002/graphql
- The server has a built in query browser at http://localhost:9002/graphiql

### Example Query
see the file exampleQueries.txt

query WorkingSatellitesForDisplay {
  satellites(categories: [1], orderBy: satelliteNumber_ASC) {
    id
    name
    satelliteNumber    
  }
}

### Schema
The graphql schemas are in src/main/resources   
- satellite.graphql
- integrationControls.graphql
- elementConversionControls.graphql

