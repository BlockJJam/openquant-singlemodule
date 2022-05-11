package com.tys.openquant.historical.repositories;

import com.tys.openquant.domain.symbol.Symbols;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveSymbolsRepository extends JpaRepository<Symbols, Integer> {
    List<Symbols> findAllByLive(Boolean live);
}
