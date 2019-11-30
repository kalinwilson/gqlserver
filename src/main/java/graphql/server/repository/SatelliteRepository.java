package graphql.server.repository;

import graphql.server.model.Satellite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * A Satellite Repository. At some point this should be a PagingAndSortingRepository
 */
public interface SatelliteRepository extends JpaRepository<Satellite, String> {
    public List<Satellite> getBySatelliteNumber(Integer satNo);
//    public List<Satellite> getBySatelliteNumberInAndCategory(List<Integer> satelliteNumbers, Integer category);
//    public List<Satellite> getByCategory(Integer category);
    public List<Satellite> getByCategoryIn(List<Integer> categories);
    public List<Satellite> getBySatelliteNumberInAndCategoryIn(List<Integer> satelliteNumbers, List<Integer> categories);
}
