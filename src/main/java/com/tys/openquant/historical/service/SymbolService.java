package com.tys.openquant.historical.service;

import com.tys.openquant.historical.dto.SymbolDataDto;
import com.tys.openquant.historical.repositories.LiveSymbolsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SymbolService {
    private final LiveSymbolsRepository liveSymbolsRepository;

    public SymbolDataDto.LiveList getLiveSymbolList() {
        SymbolDataDto.LiveList liveList = new SymbolDataDto.LiveList();
        liveList.convertToLiveList(liveSymbolsRepository.findAllByLive(true));
        return liveList;
    }
}
