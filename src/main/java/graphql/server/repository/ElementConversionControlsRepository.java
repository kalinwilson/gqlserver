package graphql.server.repository;

import graphql.server.model.ElementConversionControls;
import graphql.server.model.ApplicationEnum;
import graphql.server.model.Satellite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElementConversionControlsRepository extends JpaRepository<ElementConversionControls, String> {

    // A satellite can have multiple IntegratorControls
    public List<ElementConversionControls> findBySatelliteId(String satelliteId);

    List<ElementConversionControls> findByApplicationIn(List<ApplicationEnum> applications);

    List<ElementConversionControls> findBySatelliteIdAndApplicationIn(String satelliteId, List<ApplicationEnum> applications);
}
