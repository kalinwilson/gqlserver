package graphql.server.service.datafetcher;

import com.google.common.collect.Lists;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.ApplicationEnum;
import graphql.server.model.IntegratorControls;
import graphql.server.model.Satellite;
import graphql.server.repository.IntegratorControlsRepository;
import graphql.server.repository.SatelliteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class IntegratorControlsBySatelliteNumberAndApplicationsDataFetcher implements DataFetcher<List<IntegratorControls>> {
    private IntegratorControlsRepository integratorControlsRepository;
    private SatelliteRepository satelliteRepository;

    @Autowired
    public IntegratorControlsBySatelliteNumberAndApplicationsDataFetcher(IntegratorControlsRepository repos, SatelliteRepository satRepos) {
        this.integratorControlsRepository = repos;
        this.satelliteRepository = satRepos;
    }


    @Override
    public List<IntegratorControls> get(DataFetchingEnvironment environment) throws Exception {
        Integer satelliteNumber = environment.getArgument("satelliteNumber");
        Satellite satellite = null;
        if(satelliteNumber != null) {
            Page<Satellite> satellites = satelliteRepository.getBySatelliteNumberInAndCategoryIn(Lists.newArrayList(satelliteNumber), Lists.newArrayList(1), null);
            satellite = satellites.getContent().get(0);
        }

        List<String> applicationStrings = environment.getArgument("applications");
        List<ApplicationEnum> applications = null;
        if (applicationStrings != null){
            applications = ApplicationEnum.getValues(applicationStrings);
        }

        // default is NotAssociated
//        if(applications == null){
//            applications = Lists.newArrayList();
//            applications.add(IcApplication.NotAssociated);
//        }

        if(applications == null && satellite == null){
            return integratorControlsRepository.findAll();
        } else if (applications != null && satellite == null) {
            // get controls of the desired app type for all satellites
            return integratorControlsRepository.findByApplicationIn(applications);
        } else if (satellite != null && applications == null) {
            // Get all of the controls for the given satellite
            return integratorControlsRepository.findBySatelliteId(satellite.getId());  // probably not correct
        } else {
            return integratorControlsRepository.findBySatelliteIdAndApplicationIn(satellite.getId(), applications);
        }
    }
}
