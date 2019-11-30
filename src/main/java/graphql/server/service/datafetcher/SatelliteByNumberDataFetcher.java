package graphql.server.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.Satellite;
import graphql.server.repository.SatelliteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SatelliteByNumberDataFetcher  implements DataFetcher<List<Satellite>> {
    private SatelliteRepository satelliteRepository;

    @Autowired
    SatelliteByNumberDataFetcher(SatelliteRepository repos){
        this.satelliteRepository = repos;
    }

    @Override
    public List<Satellite> get(DataFetchingEnvironment environment) throws Exception {
        log.debug("Fetching the data for Satellite {}", environment.getArgument("satelliteNumber").toString());
        Integer satNo = environment.getArgument("satelliteNumber");
        if(satNo == null){
            log.error("Satellite Number may not be null for SatelliteByNumber query.");
            return null;
        }
        return satelliteRepository.getBySatelliteNumber(satNo);
    }
}
