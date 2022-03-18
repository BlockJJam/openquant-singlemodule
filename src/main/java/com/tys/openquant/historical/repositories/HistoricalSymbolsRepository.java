package com.tys.openquant.historical.repositories;

import com.tys.openquant.domain.symbol.Symbols;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricalSymbolsRepository extends JpaRepository<Symbols, String> {
    Symbols findSymbolsByCode(String symbolCode);
}
