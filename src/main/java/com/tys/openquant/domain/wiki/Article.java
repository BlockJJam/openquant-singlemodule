package com.tys.openquant.domain.wiki;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "openquant", name="article")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idx;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(nullable = false, length = 50000)
    private String contents;

    @Column(length = 75000)
    private String overview;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
    private Boolean delNy;
    
    //Cascade를 쓰려면 Parent-Child 구조에서 Parent의 영향이 Child에게까지 어떻게 영향을 전파할지를 고려해봐야 한다
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @PrePersist
    public void prePersist(){
        this.delNy = this.delNy == null? false: this.delNy;
    }
}
