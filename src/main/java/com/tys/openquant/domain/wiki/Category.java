package com.tys.openquant.domain.wiki;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "openquant", name="category")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idx;

    @Column(nullable = false, length = 200)
    private String categoryName;

    @Column(nullable = false, length = 500)
    private String categoryIcon;

    @OneToMany(mappedBy = "category")
    @Column(insertable = false, updatable = false)
    private List<Article> articles;

}
