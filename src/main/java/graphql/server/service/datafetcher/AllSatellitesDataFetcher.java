package graphql.server.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.Book;
import graphql.server.model.Satellite;
import graphql.server.repository.SatelliteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AllSatellitesDataFetcher implements DataFetcher<List<Satellite>> {

    private SatelliteRepository satelliteRepository;

    @Autowired
    AllSatellitesDataFetcher(SatelliteRepository repos){

        this.satelliteRepository = repos;
    }

    @Override
    public List<Satellite> get(DataFetchingEnvironment dataFetchingEnvironment) {
        log.debug("Fetching the data for all Satellites. DataFecthing Env is  {}", dataFetchingEnvironment.toString());
        return satelliteRepository.findAll();
    }
}
