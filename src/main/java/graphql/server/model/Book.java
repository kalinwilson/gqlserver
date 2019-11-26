package graphql.server.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Book {
    @Id
    private String isn;
    private String title;
    private String publisher;
    private String publishedDate;
    private String[] author;

}
