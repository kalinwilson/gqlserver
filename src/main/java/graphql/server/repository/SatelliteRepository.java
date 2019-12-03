package graphql.server.repository;

import graphql.server.model.Satellite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * A Satellite Repository. At some point this should be a PagingAndSortingRepository
 */
public interface SatelliteRepository extends PagingAndSortingRepository<Satellite, String> {
    public List<Satellite> getBySatelliteNumber(Integer satNo);
//    public List<Satellite> getBySatelliteNumberInAndCategory(List<Integer> satelliteNumbers, Integer category);
//    public List<Satellite> getByCategory(Integer category);
    public Page<Satellite> getByCategoryIn(List<Integer> categories, Pageable request);
    public Page<Satellite> getBySatelliteNumberInAndCategoryIn(List<Integer> satelliteNumbers, List<Integer> categories, Pageable request);
    public Page<Satellite> findBySatelliteNumberIn(List<Integer> satelliteNumbers, Pageable request);
}
