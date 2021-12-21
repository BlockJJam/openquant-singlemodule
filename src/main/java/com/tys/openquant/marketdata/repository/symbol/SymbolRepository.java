package com.tys.openquant.marketdata.repository.symbol;

import com.tys.openquant.domain.symbol.Symbols;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SymbolRepository extends JpaRepository<Symbols, Integer> {
    List<Symbols> findAllByUpdateAtNotNull();
    // 수정이력: 2021.11.13 (이유: sybollist 수정)
    List<Symbols> findAllByStateAndUpdateAtNotNull(Integer state);
    Optional<Symbols> findByCode(String code);
}
