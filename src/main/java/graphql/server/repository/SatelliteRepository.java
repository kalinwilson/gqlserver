package graphql.server.repository;

import graphql.server.model.Satellite;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A Satellite Repository. At some point this should be a PagingAndSortingRepository
 */
public interface SatelliteRepository extends JpaRepository<Satellite, String> {
}
