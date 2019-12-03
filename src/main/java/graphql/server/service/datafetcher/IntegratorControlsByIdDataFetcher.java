package graphql.server.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.server.model.IntegratorControls;
import graphql.server.repository.IntegratorControlsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IntegratorControlsByIdDataFetcher implements DataFetcher<IntegratorControls> {
    private IntegratorControlsRepository integratorControlsRepository;

    @Autowired
    public IntegratorControlsByIdDataFetcher(IntegratorControlsRepository repos) {
        this.integratorControlsRepository = repos;
    }

    @Override
    public IntegratorControls get(DataFetchingEnvironment environment) throws Exception {
        return integratorControlsRepository.findById(environment.getArgument("id")).orElse(null);
    }
}
