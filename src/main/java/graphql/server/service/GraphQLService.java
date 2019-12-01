package graphql.server.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import graphql.server.model.*;
import graphql.server.repository.BookRepository;
import graphql.server.repository.IntegratorControlsRepository;
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
    private IntegratorControlsRepository integratorControlsRepository;


    private AllBooksDataFetcher allBooksDataFetcher;
    private BookDataFetcher bookDataFetcher;
    private AllSatellitesDataFetcher allSatellitesDataFetcher;
    private SatellitesByIdDataFetcher satelliteByIdDataFetcher;
    private SatelliteByNumberDataFetcher satelliteByNumberDataFetcher;
    private SatellitesByNumberAndCategoryDataFetcher satellitesByNumberAndCategoryDataFetcher;
    private IntegratorControlsByIdDataFetcher integratorControlsByIdDataFetcher;
    private IntegratorControlsBySatelliteIdAndApplicationsDataFetcher integratorControlsBySatelliteIdAndApplicationDataFetcher;
    private IntegratorControlsBySatelliteNumberAndApplicationsDataFetcher integratorControlsBySatelliteNumberAndApplicationDataFetcher;
    private IntegratorControlsDataFetcher integratorControlsDataFetcher;

    private GraphQL graphQL;

    @Autowired
    public GraphQLService(BookRepository bookRepository,
                          SatelliteRepository satelliteRepository,
                          IntegratorControlsRepository integratorControlsRepository,
                          AllBooksDataFetcher allBooksDataFetcher,
                          BookDataFetcher bookDataFetcher,
                          AllSatellitesDataFetcher allSatelliteDataFetcher,
                          SatellitesByIdDataFetcher satelliteByIdDataFetcher,
                          SatelliteByNumberDataFetcher satelliteByNumberDataFetcher,
                          SatellitesByNumberAndCategoryDataFetcher satellitesByNumberAndCategoryDataFetcher,
                          IntegratorControlsByIdDataFetcher integratorControlsByIdDataFetcher,
                          IntegratorControlsBySatelliteIdAndApplicationsDataFetcher integratorControlsBySatelliteIdAndApplicationDataFetcher,
                          IntegratorControlsBySatelliteNumberAndApplicationsDataFetcher integratorControlsBySatelliteNumberAndApplicationDataFetcher,
                          IntegratorControlsDataFetcher integratorControlsDataFetcher) {
        this.bookRepository = bookRepository;
        this.satelliteRepository = satelliteRepository;
        this.integratorControlsRepository = integratorControlsRepository;
        this.allBooksDataFetcher = allBooksDataFetcher;
        this.bookDataFetcher = bookDataFetcher;
        this.allSatellitesDataFetcher = allSatelliteDataFetcher;
        this.satelliteByIdDataFetcher = satelliteByIdDataFetcher;
        this.satelliteByNumberDataFetcher = satelliteByNumberDataFetcher;
        this.satellitesByNumberAndCategoryDataFetcher = satellitesByNumberAndCategoryDataFetcher;
        this.integratorControlsByIdDataFetcher = integratorControlsByIdDataFetcher;
        this.integratorControlsBySatelliteIdAndApplicationDataFetcher = integratorControlsBySatelliteIdAndApplicationDataFetcher;
        this.integratorControlsBySatelliteNumberAndApplicationDataFetcher = integratorControlsBySatelliteNumberAndApplicationDataFetcher;
        this.integratorControlsDataFetcher = integratorControlsDataFetcher;
    }

    @PostConstruct
    public void init() throws IOException {
//       This is an example of how to load multiple schema files
        URL satelliteUrl = Resources.getResource("satellites.graphql");
        URL integratorControlsUrl = Resources.getResource("integratorControls.graphql");
        URL booksUrl = Resources.getResource("books.graphql");
        List<String> schemas = Lists.newArrayList();
        String satelliteSchema = Resources.toString(satelliteUrl, UTF_8);
        schemas.add(satelliteSchema);
        String integratorControlsSchema = Resources.toString(integratorControlsUrl, UTF_8);
        schemas.add(integratorControlsSchema);
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
                        .dataFetcher("integratorControlsById", integratorControlsByIdDataFetcher)
                        .dataFetcher("integratorControlsBySatelliteIdAndApplications", integratorControlsBySatelliteIdAndApplicationDataFetcher)
                        .dataFetcher("integratorControlsBySatelliteNumberAndApplications", integratorControlsBySatelliteNumberAndApplicationDataFetcher)
                        .dataFetcher("integratorControls", integratorControlsDataFetcher)
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
        logger.debug(("Loading data into the embedded test database..."));
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
            logger.debug("        Saving book {}", book.getIsn());
            bookRepository.save(book);
        });

        logger.debug("    Loading Satellite data...");
        // Load Satellite Data
        URL satelliteDataURL = Resources.getResource("data/satellite.csv");
        // Load IntegratorControls Data
        URL icURL = Resources.getResource("data/integratorControls.csv");
        BufferedReader reader = null;
        try {
            CharSource charSource = Resources.asCharSource(satelliteDataURL, UTF_8);
            reader = charSource.openBufferedStream();
            reader.lines().map(mapToSatellite).forEach(satellite -> {
                logger.debug("        Saving Satellite {}", satellite.getId());
                satelliteRepository.save(satellite);
            });
            reader.close();

            logger.debug("    Loading IntegratorControls data...");
            charSource = Resources.asCharSource(icURL, UTF_8);
            reader = charSource.openBufferedStream();
            reader.lines().map(mapToIntegratorControls).forEach(ic -> {
                logger.debug("        Saving IntegratorControls {}", ic.getId());
                integratorControlsRepository.save(ic);
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

    private Function<String, IntegratorControls> mapToIntegratorControls = (line) -> {
        List<String> fields = Splitter.on(',').trimResults().splitToList(line);
        try {
            IntegratorControls ic = new IntegratorControls(
                    fields.get(0),
                    fields.get(1),
                    IcApplication.getByIntValue(Integer.valueOf(fields.get(2))),
                    IcCoordinateSystem.getByIntValue(Integer.valueOf(fields.get(3))),
                    Double.valueOf(fields.get(4)),
                    Double.valueOf(fields.get(5)),
                    PartialDerivatives.getByIntValue(Integer.valueOf(fields.get(6))),
                    Boolean.valueOf(fields.get(7)),
                    Propagator.getByIntValue(Integer.valueOf(fields.get(8))),
                    Boolean.valueOf(fields.get(9)),
                    StepMode.getByIntValue(Integer.valueOf(fields.get(10))),
                    StepSizeMethod.getByIntValue(Integer.valueOf(fields.get(11))),
                    StepSizeSource.getByIntValue(Integer.valueOf(fields.get(12)))
                    );
            return ic;
        } catch (NumberFormatException e) {
            logger.error("Error converting csv line to IntegratorControls.", e);
        }
        return null;
    };


    @Bean
    public GraphQL getGraphQL() {
        return graphQL;
    }
}
