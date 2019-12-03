package graphql.server.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.Satellite;
import graphql.server.repository.SatelliteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SatellitesByIdDataFetcher implements DataFetcher<Satellite> {
    private SatelliteRepository satelliteRepository;

    @Autowired
    SatellitesByIdDataFetcher(SatelliteRepository repos){
        this.satelliteRepository = repos;
    }

    @Override
    public Satellite get(DataFetchingEnvironment environment) throws Exception {
        log.debug("Fetching the data for Satellite with ID {}.", environment.getArgument("id").toString());

        return satelliteRepository.findById(environment.getArgument("id")).orElse(null);
    }
}
