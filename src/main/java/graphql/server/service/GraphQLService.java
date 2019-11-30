package graphql.server.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import graphql.server.model.Book;
import graphql.server.model.Satellite;
import graphql.server.repository.BookRepository;
import graphql.server.repository.SatelliteRepository;
import graphql.server.service.datafetcher.*;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.io.Resources;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class GraphQLService {
    private static Logger logger = LoggerFactory.getLogger(GraphQLService.class);
    private BookRepository bookRepository;
    private SatelliteRepository satelliteRepository;
    private AllBooksDataFetcher allBooksDataFetcher;
    private BookDataFetcher bookDataFetcher;
    private AllSatellitesDataFetcher allSatellitesDataFetcher;
    private SatellitesByIdDataFetcher satelliteByIdDataFetcher;
    private SatelliteByNumberDataFetcher satelliteByNumberDataFetcher;
    private SatellitesByNumberAndCategoryDataFetcher satellitesByNumberAndCategoryDataFetcher;

    private GraphQL graphQL;

    @Autowired
    public GraphQLService(BookRepository bookRepository,
                          SatelliteRepository satelliteRepository,
                          AllBooksDataFetcher allBooksDataFetcher,
                          BookDataFetcher bookDataFetcher,
                          AllSatellitesDataFetcher allSatelliteDataFetcher,
                          SatellitesByIdDataFetcher satelliteByIdDataFetcher,
                          SatelliteByNumberDataFetcher satelliteByNumberDataFetcher,
                          SatellitesByNumberAndCategoryDataFetcher satellitesByNumberAndCategoryDataFetcher) {
        this.bookRepository = bookRepository;
        this.satelliteRepository = satelliteRepository;
        this.allBooksDataFetcher = allBooksDataFetcher;
        this.bookDataFetcher = bookDataFetcher;
        this.allSatellitesDataFetcher = allSatelliteDataFetcher;
        this.satelliteByIdDataFetcher = satelliteByIdDataFetcher;
        this.satelliteByNumberDataFetcher = satelliteByNumberDataFetcher;
        this.satellitesByNumberAndCategoryDataFetcher = satellitesByNumberAndCategoryDataFetcher;
    }

    @PostConstruct
    public void init() throws IOException {
//       This is an example of how to load multiple schema files
        URL satelliteUrl = Resources.getResource("satellites.graphql");
        URL booksUrl = Resources.getResource("books.graphql");
        List<String> schemas = Lists.newArrayList();
        String satelliteSchema = Resources.toString(satelliteUrl, UTF_8);
        schemas.add(satelliteSchema);
        String bookSchema = Resources.toString(booksUrl, UTF_8);
        schemas.add(bookSchema);
        GraphQLSchema graphQLSchema = buildSchema(schemas);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        loadDataIntoHSQL();
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
        return RuntimeWiring.newRuntimeWiring()
                .type("ConsolidatedQuery", typeWiring -> typeWiring
                        .dataFetcher("allSatellites", allSatellitesDataFetcher)
                        .dataFetcher("satelliteById", satelliteByIdDataFetcher)
                        .dataFetcher("satelliteByNumber", satelliteByNumberDataFetcher)
                        .dataFetcher("satellitesByNumberAndCategory", satellitesByNumberAndCategoryDataFetcher)
                        .dataFetcher("allBooks", allBooksDataFetcher)
                        .dataFetcher("book", bookDataFetcher))
                .build();
    }

    //    private RuntimeWiring buildRuntimeWiring() {
//        return RuntimeWiring.newRuntimeWiring()
//                .type("Query", typeWiring -> typeWiring
//                        .dataFetcher("allBooks", allBooksDataFetcher)
//                        .dataFetcher("book", bookDataFetcher))
//                .build();
//    }




    private void loadDataIntoHSQL() {
        // Load book data
        Stream.of(
                new Book("1001", "The C Programming Language", "PHI Learning", "1978",
                        new String[]{
                                "Brian W. Kernighan (Contributor)",
                                "Dennis M. Ritchie"
                        }),
                new Book("1002", "Your Guide To Scrivener", "MakeUseOf.com", " April 21st 2013",
                        new String[]{
                                "Nicole Dionisio (Goodreads Author)"
                        }),
                new Book("1003", "Beyond the Inbox: The Power User Guide to Gmail", " Kindle Edition", "November 19th 2012",
                        new String[]{
                                "Shay Shaked"
                                , "Justin Pot"
                                , "Angela Randall (Goodreads Author)"
                        }),
                new Book("1004", "Scratch 2.0 Programming", "Smashwords Edition", "February 5th 2015",
                        new String[]{
                                "Denis Golikov (Goodreads Author)"
                        }),
                new Book("1005", "Pro Git", "by Apress (first published 2009)", "2014",
                        new String[]{
                                "Scott Chacon"
                        })
        ).forEach(book -> {
            bookRepository.save(book);
        });

        // Load Satellite Data
        URL satelliteDataURL = Resources.getResource("data/satellite.csv");
        BufferedReader reader = null;
        try {
            CharSource charSource = Resources.asCharSource(satelliteDataURL, UTF_8);
            reader = charSource.openBufferedStream();
            reader.lines().map(mapToSatellite).forEach(satellite -> {
                satelliteRepository.save(satellite);
            });

        } catch (IOException e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("Error closing stream.", e);
                }
            }
        }
    }

    private Function<String, Satellite> mapToSatellite = (line) -> {
        List<String> satFields = Splitter.on(',').trimResults().splitToList(line);
        try {
            Satellite sat = new Satellite(satFields.get(0), Integer.parseInt(satFields.get(1)), satFields.get(2), satFields.get(3), Integer.parseInt(satFields.get(4)));
            return sat;
        } catch (NumberFormatException e) {
            logger.error("Error converting csv line to Satellite.", e);
        }
        return null;
    };


    @Bean
    public GraphQL getGraphQL() {
        return graphQL;
    }
}
