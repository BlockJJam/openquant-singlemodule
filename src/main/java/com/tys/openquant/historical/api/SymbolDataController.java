package com.tys.openquant.historical.api;

import com.tys.openquant.historical.dto.SymbolDataDto;
import com.tys.openquant.historical.service.SymbolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@Slf4j
@RestController
@RequestMapping("/api/historical")
@RequiredArgsConstructor
public class SymbolDataController {
    private final SymbolService symbolService;

    @GetMapping("/symbol/live")
    public ResponseEntity<SymbolDataDto.LiveList> reserveLiveDataSymbolList(){
        return ResponseEntity.ok(symbolService.getLiveSymbolList());
    }
}
