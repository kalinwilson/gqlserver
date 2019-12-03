package graphql.server.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.Satellite;
import graphql.server.repository.SatelliteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        String orderByString = environment.getArgument("orderBy");
        Integer pageNumber = environment.getArgument("page");
        if(pageNumber == null){
            pageNumber = 0;
        }

        Integer limit = environment.getArgument("limit");
        if(limit == null){
            limit = 30;
        }

        Sort sort = null;
        if(orderByString == null){
            sort = Sort.unsorted();
        } else {
            switch (orderByString){
                case "satelliteNumber_ASC":
                    sort = Sort.by(Sort.Direction.ASC, "satelliteNumber");
                    break;
                case "satelliteNumber_DESC":
                    sort = Sort.by(Sort.Direction.DESC, "satelliteNumber");
                    break;
                case "name_ASC":
                    sort = Sort.by(Sort.Direction.ASC, "name");
                    break;
                case "name_DESC":
                    sort = Sort.by(Sort.Direction.DESC, "name");
                    break;
                default:
                    sort = Sort.unsorted();
            }
        }

        Pageable request = PageRequest.of(pageNumber, limit, sort);


        // default category
//        if(categories == null){
//            log.info("satellitesByNumberAndCategory: categories argument is null. Using default category=1.");
//            categories = new ArrayList<>();
//            categories.add(1);
//        }

        Page<Satellite> satellites = null;
        if(satelliteNumbers == null && categories == null) {
            log.debug("Fetching the data for all satellites in all categories.");
            satellites = satelliteRepository.findAll(request);
        }
        if(satelliteNumbers == null && categories != null){
            log.debug("Fetching the data for all satellites in categories {}.", categories);
            satellites = satelliteRepository.getByCategoryIn(categories, request);
        } else if (categories == null && satelliteNumbers != null) {
            log.debug("Fetching the data for satellites {} in all categories.", satelliteNumbers);
            satellites = satelliteRepository.findBySatelliteNumberIn(satelliteNumbers, request);
        }else {
            log.debug("Fetching the data for Satellite numbers {} and categories {}.", satelliteNumbers, categories);
            satellites = satelliteRepository.getBySatelliteNumberInAndCategoryIn(satelliteNumbers, categories, request);
        }
        return satellites.getContent();
    }
}
