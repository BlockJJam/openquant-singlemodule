package com.tys.openquant.domain.Users;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="authority", schema="openquant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {
    @Id
    @Column( length= 50)
    private String authName;
}
