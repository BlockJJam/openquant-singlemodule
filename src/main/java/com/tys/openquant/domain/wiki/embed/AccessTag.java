package com.tys.openquant.domain.wiki.embed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

//@TODO 지워야 하는 클래스
@Builder
@Embeddable
@Getter
@AllArgsConstructor
public class AccessTag {
    protected AccessTag(){}

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
}
