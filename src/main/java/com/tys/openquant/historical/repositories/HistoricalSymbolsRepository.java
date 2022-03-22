package com.tys.openquant.historical.repositories;

import com.tys.openquant.domain.symbol.Symbols;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoricalSymbolsRepository extends JpaRepository<Symbols, String> {
    Symbols findSymbolsByCode(String symbolCode);
    Optional<Symbols> findByCode(String code);

}
