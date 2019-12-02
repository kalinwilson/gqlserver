package graphql.server.repository;

import graphql.server.model.ApplicationEnum;
import graphql.server.model.IntegratorControls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntegratorControlsRepository extends JpaRepository<IntegratorControls, String> {
    // A satellite can have multiple IntegratorControls
    public List<IntegratorControls> findBySatelliteId(String satelliteId);

    List<IntegratorControls> findByApplicationIn(List<ApplicationEnum> applications);

    List<IntegratorControls> findBySatelliteIdAndApplicationIn(String id, List<ApplicationEnum> applications);
}
