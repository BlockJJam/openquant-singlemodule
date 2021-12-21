package com.tys.openquant.wiki.api;

import com.tys.openquant.wiki.dto.WikiDto;
import com.tys.openquant.wiki.service.WikiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/wiki/public")
public class WikiController {
    private final WikiService wikiService;

    /**
     * Wiki의 모든 카테고리와 그 서브메뉴를 Index기준으로 목록을 만들어 조회하는 API
     * @return WikiDto.MenuTree
     * @author Jaemin.Joo
     */
    @GetMapping("/menu-tree")
    public ResponseEntity<WikiDto.MenuTree> getMenuTree(){
        return ResponseEntity.ok(wikiService.findPublicMenuList());
    }

    /**
     * 매개변수로 넘어온 MenuTree에 저장된 Article id로 해당 Article 정보를 조회하는 API
     * @return WikiDto.MenuTree
     * @author Jaemin.Joo
     */
    @GetMapping("/article")
    public ResponseEntity<WikiDto.ArticleInfo> getArticleById(@RequestParam Long id){
        return ResponseEntity.ok(wikiService.findArticleById(id));
    }
}
