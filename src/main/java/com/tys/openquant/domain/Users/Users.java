package com.tys.openquant.domain.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name="users", schema = "openquant")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
// @수정이력: 2021.11.24 (이유: 현재는 db의 sequence를 연결해주지 않지만, 배포버전에서는 연결해주는 부분이 남아있음)
public class Users {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userSeq;

    @Column(name="user_id", length = 50,  unique=true, nullable = false)
    private String id;

    @Column(name= "password",length = 100, nullable = false)
    private String pwd;

    @Column(name= "email", length = 320, nullable = false)
    private String email;
    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private String modifiedBy;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private Boolean delNy;
    private LocalDateTime deletedAt;

    @ManyToMany
    @JoinTable(
            name = "users_auth",
            schema ="openquant",
            joinColumns = {@JoinColumn(name="userSeq", referencedColumnName = "userSeq")},
            inverseJoinColumns = {@JoinColumn(name="authName", referencedColumnName = "authName")} // name= Entity Class의 필드명, referencedColumnName은 DB 테이블의 필드명
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Set<Authority> authoritySet;

    public void setDeletedInfo(Boolean delNy, LocalDateTime deletedAt) {
        this.delNy = delNy;
        this.deletedAt = deletedAt;
    }

}
