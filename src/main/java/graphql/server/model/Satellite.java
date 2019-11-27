package graphql.server.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Satellite {
    @Id
    private String id;
    private Integer satelliteNumber;
    private String name;
    private String intlDesignator;
    private Integer category; // This would normally be SatelliteCategory Enum
}
