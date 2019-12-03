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

### toturials
The following tutorials were used as a basis for this demo project
- (graphql-java Spring Boot Tutorial)[https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/#schema]
- (SpringFramework Guru Tutorial)[https://springframework.guru/using-graphql-in-a-spring-boot-application/]


### Useful Links
- (GraphQL.org)[https://graphql.org/]
- (GraphQL Java)[https://www.graphql-java.com/)
- (How To GraphQL)[https://www.howtographql.com/]
- (GraphQL Playground)[https://github.com/prisma-labs/graphql-playground]
- (GraphQL Editor - commercial)[https://graphqleditor.com/]
