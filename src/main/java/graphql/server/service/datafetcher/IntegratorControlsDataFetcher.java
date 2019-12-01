package graphql.server.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.IcApplication;
import graphql.server.model.IntegratorControls;
import graphql.server.model.Satellite;
import graphql.server.repository.IntegratorControlsRepository;
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
public class IntegratorControlsDataFetcher implements DataFetcher<List<IntegratorControls>> {
    private IntegratorControlsRepository integratorControlsRepository;

    @Autowired
    public IntegratorControlsDataFetcher(IntegratorControlsRepository repos) {
        this.integratorControlsRepository = repos;
    }

    @Override
    public List<IntegratorControls> get(DataFetchingEnvironment environment) throws Exception {
        Satellite satellite = environment.getSource();
        List<String> applicationStrings = environment.getArgument("applications");
        List<IcApplication> applications = null;
        if (applicationStrings != null){
            applications = IcApplication.getValues(applicationStrings);
        }
        if(satellite == null){
            log.error("Satellite must be specified as query source when getting integrator controls");
            return null;
        } else if(applications == null){
            return integratorControlsRepository.findBySatelliteId(satellite.getId());
        } else {
            return integratorControlsRepository.findBySatelliteIdAndApplicationIn(satellite.getId(), applications);
        }
    }
}
