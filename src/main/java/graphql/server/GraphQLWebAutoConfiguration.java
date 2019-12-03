package graphql.server;

import graphql.spring.web.servlet.components.GraphQLController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnClass(GraphQLController.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@ConfigurationProperties(prefix = "graphql.web")
public class GraphQLWebAutoConfiguration {

    public static final String GRAPHQL_MAPPING = "/graphql";

    private String mapping;


    @Bean
    @ConditionalOnClass(CorsFilter.class)
    @ConditionalOnProperty(value = "graphql.web.corsEnabled", havingValue = "true", matchIfMissing = true)
    public CorsFilter corsConfigurer() {
        log.info("Configuring Cross Domain Origin (CORS) support.");
        Map<String, CorsConfiguration> corsConfigurations = new LinkedHashMap<>(1);
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfigurations.put(getCorsMapping(), corsConfiguration);

        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.setCorsConfigurations(corsConfigurations);
        configurationSource.setAlwaysUseFullPath(true);
        CorsFilter corsFilter = new CorsFilter(configurationSource);
        log.debug("Created CORS Filter: {}", corsFilter.toString());
        return corsFilter;
    }

    public String getMapping() {
        return mapping != null ? mapping : GRAPHQL_MAPPING;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    private boolean mappingIsServletWildcard() {
        return getMapping().endsWith("/*");
    }

    private boolean mappingIsAntWildcard() {
        return getMapping().endsWith("/**");
    }

    /**
     * @return the servlet mapping, coercing into an appropriate wildcard for servlets (ending in /*)
     */
    public String getServletMapping() {
        final String mapping = getMapping();
        if (mappingIsAntWildcard()) {
            return mapping.replaceAll("\\*$", "");
        } else if (mappingIsServletWildcard()) {
            return mapping;
        } else {
            return mapping.endsWith("/") ? mapping + "*" : mapping + "/*";
        }
    }

    /**
     * @return the servlet mapping, coercing into an appropriate wildcard for CORS, which uses ant matchers (ending in
     * /**)
     */
    public String getCorsMapping() {
        final String mapping = getMapping();
        if (mappingIsAntWildcard()) {
            return mapping;
        } else if (mappingIsServletWildcard()) {
            return mapping + "*";
        } else {
            return mapping.endsWith("/") ? mapping + "**" : mapping + "/**";
        }
    }
}
