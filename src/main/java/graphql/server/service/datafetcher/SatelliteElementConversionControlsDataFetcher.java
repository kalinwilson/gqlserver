package graphql.server.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.ElementConversionControls;
import graphql.server.model.ApplicationEnum;
import graphql.server.model.Satellite;
import graphql.server.repository.ElementConversionControlsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
/**
 * This DataFetcher is called when resolving the 'integratorControls' field of a Satellite.
 * The query may optionally specify which application controls to get. If not specified, get them all.
 */
public class SatelliteElementConversionControlsDataFetcher implements DataFetcher<List<ElementConversionControls>> {
    private ElementConversionControlsRepository elementConversionControlsRepository;

    @Autowired
    public SatelliteElementConversionControlsDataFetcher(ElementConversionControlsRepository repos) {
        this.elementConversionControlsRepository = repos;
    }

    @Override
    public List<ElementConversionControls> get(DataFetchingEnvironment environment) throws Exception {
        Satellite satellite = environment.getSource();
        List<String> applicationStrings = environment.getArgument("applications");
        List<ApplicationEnum> applications = null;
        if (applicationStrings != null){
            applications = ApplicationEnum.getValues(applicationStrings);
        }
        if(satellite == null){
            log.error("Satellite must be specified as query source when getting element conversion controls");
            return null;
        } else if(applications == null){
            return elementConversionControlsRepository.findBySatelliteId(satellite.getId());
        } else {
            return elementConversionControlsRepository.findBySatelliteIdAndApplicationIn(satellite.getId(), applications);
        }
    }
}
