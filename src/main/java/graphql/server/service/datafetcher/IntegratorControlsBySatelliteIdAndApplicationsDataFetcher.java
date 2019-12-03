package graphql.server.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.ApplicationEnum;
import graphql.server.model.IntegratorControls;
import graphql.server.model.Satellite;
import graphql.server.repository.IntegratorControlsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class IntegratorControlsBySatelliteIdAndApplicationsDataFetcher implements DataFetcher<List<IntegratorControls>> {
    private IntegratorControlsRepository integratorControlsRepository;

    @Autowired
    public IntegratorControlsBySatelliteIdAndApplicationsDataFetcher(IntegratorControlsRepository repos) {
        this.integratorControlsRepository = repos;
    }


    @Override
    public List<IntegratorControls> get(DataFetchingEnvironment environment) throws Exception {
        Satellite satellite = environment.getSource();
        List<ApplicationEnum> applications = environment.getArgument("applications");

        // default is NotAssociated
//        if(applications == null){
//            applications = Lists.newArrayList();
//            applications.add(IcApplication.NotAssociated);
//        }

        if(satellite == null){
            // get controls of the desired app type for all satellites
            return integratorControlsRepository.findByApplicationIn(applications);
        } else if(satellite != null && applications == null){
            // Get all of the controls for the given satellite
            return integratorControlsRepository.findBySatelliteId(satellite.getId());
        } else {
            return integratorControlsRepository.findBySatelliteIdAndApplicationIn(satellite.getId(), applications);
        }
    }
}
