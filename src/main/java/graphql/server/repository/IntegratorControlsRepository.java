package graphql.server.repository;

import graphql.server.model.IcApplication;
import graphql.server.model.IntegratorControls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntegratorControlsRepository extends JpaRepository<IntegratorControls, String> {
    // A satellite can have multiple IntegratorControls
    public List<IntegratorControls> findBySatelliteId(String satelliteId);

    List<IntegratorControls> findByApplicationIn(List<IcApplication> applications);

    List<IntegratorControls> findBySatelliteIdAndApplicationIn(String id, List<IcApplication> applications);
}
