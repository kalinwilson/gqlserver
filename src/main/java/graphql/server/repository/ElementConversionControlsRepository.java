package graphql.server.repository;

import graphql.server.model.ElementConversionControls;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElementConversionControlsRepository extends JpaRepository<ElementConversionControls, String> {
}
