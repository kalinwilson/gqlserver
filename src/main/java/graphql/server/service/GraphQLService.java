package graphql.server.service;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.server.service.datafetcher.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class GraphQLService {
    private static Logger logger = LoggerFactory.getLogger(GraphQLService.class);


    @Autowired private SatellitesByIdDataFetcher satelliteByIdDataFetcher;
    @Autowired private SatellitesByNumberAndCategoryDataFetcher satellitesByNumberAndCategoryDataFetcher;
    @Autowired private IntegratorControlsByIdDataFetcher integratorControlsByIdDataFetcher;
    @Autowired private IntegratorControlsBySatelliteIdAndApplicationsDataFetcher integratorControlsBySatelliteIdAndApplicationDataFetcher;
    @Autowired private IntegratorControlsBySatelliteNumberAndApplicationsDataFetcher integratorControlsBySatelliteNumberAndApplicationDataFetcher;
    @Autowired private SatelliteIntegratorControlsDataFetcher satelliteIntegratorControlsDataFetcher;
    @Autowired private SatelliteElementConversionControlsDataFetcher satelliteElementConversionControlsDataFetcher;

    @Autowired DataLoader loader;

    private GraphQL graphQL;


    @PostConstruct
    public void init() throws IOException {
//       This is an example of how to load multiple schema files
        URL satelliteUrl = Resources.getResource("satellites.graphql");
        URL integratorControlsUrl = Resources.getResource("integratorControls.graphql");
        URL elementConversionControlsUrl = Resources.getResource("elementConversionControls.graphql");
        List<String> schemas = Lists.newArrayList();
        String satelliteSchema = Resources.toString(satelliteUrl, UTF_8);
        schemas.add(satelliteSchema);
        String integratorControlsSchema = Resources.toString(integratorControlsUrl, UTF_8);
        schemas.add(integratorControlsSchema);
        String elementConversionControlsSchema = Resources.toString(elementConversionControlsUrl, UTF_8);
        schemas.add(elementConversionControlsSchema);

        // Turn on instrumentation for query tracing in GraphQL Playground, etc.
        GraphQLSchema graphQLSchema = buildSchema(schemas);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema)
                .instrumentation(new TracingInstrumentation())
                .build();


        loader.loadDataIntoHSQL();
    }

    private GraphQLSchema buildSchema(List<String> schemas) {
        SchemaParser parser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        for(String schema : schemas){
            typeRegistry.merge(parser.parse(schema));
        }

        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildRuntimeWiring() {
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("ConsolidatedQuery")
                        .dataFetcher("satelliteById", satelliteByIdDataFetcher)
                        .dataFetcher("satellites", satellitesByNumberAndCategoryDataFetcher)
                        .dataFetcher("integratorControlsById", integratorControlsByIdDataFetcher)
                        .dataFetcher("integratorControlsBySatelliteIdAndApplications", integratorControlsBySatelliteIdAndApplicationDataFetcher)
                        .dataFetcher("integratorControlsBySatelliteNumberAndApplications", integratorControlsBySatelliteNumberAndApplicationDataFetcher)
                        )
                .type(newTypeWiring("Satellite")
                        .dataFetcher("integratorControls", satelliteIntegratorControlsDataFetcher)
                        .dataFetcher("elementConversionControls", satelliteElementConversionControlsDataFetcher))
                .build();
        return  wiring;
    }

    @Bean
    public GraphQL getGraphQL() {
        return graphQL;
    }
}
