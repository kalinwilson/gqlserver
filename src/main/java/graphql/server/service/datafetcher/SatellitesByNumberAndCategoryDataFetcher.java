package graphql.server.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.Satellite;
import graphql.server.repository.SatelliteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SatellitesByNumberAndCategoryDataFetcher implements DataFetcher<List<Satellite>> {
    private SatelliteRepository satelliteRepository;

    @Autowired
    SatellitesByNumberAndCategoryDataFetcher(SatelliteRepository repos){
        this.satelliteRepository = repos;
    }

    @Override
    public List<Satellite> get(DataFetchingEnvironment environment) throws Exception {
        List<Integer> satelliteNumbers = environment.getArgument("satelliteNumbers");
        List<Integer> categories = environment.getArgument("categories");
        if(categories == null){
            log.info("satellitesByNumberAndCategory: categories argument is null. Using default category=1.");
            categories = new ArrayList<>();
            categories.add(1);
        }
        if(satelliteNumbers == null){
            log.debug("Fetching the data for all satellites in categories {}.", categories);
            return satelliteRepository.getByCategoryIn(categories);
        } else {
            log.debug("Fetching the data for Satellite numbers {} and categories {}.", satelliteNumbers, categories);
            return satelliteRepository.getBySatelliteNumberInAndCategoryIn(satelliteNumbers, categories);
        }
    }
}
