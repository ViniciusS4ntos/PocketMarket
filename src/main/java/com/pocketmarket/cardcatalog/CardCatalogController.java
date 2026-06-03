package com.pocketmarket.cardcatalog;

import com.pocketmarket.cardcatalog.dto.CardCatalogResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/card-catalog")
@RequiredArgsConstructor
@Validated
public class CardCatalogController {

    private final CardCatalogService cardCatalogService;

    @GetMapping
    public Page<CardCatalogResponse> findCards(@PageableDefault(size = 20) Pageable pageable) {
        return cardCatalogService.findCards(pageable);
    }

    @GetMapping("/search")
    public List<CardCatalogResponse> searchByName(@RequestParam String name) {
        return cardCatalogService.searchByName(name);
    }

    @GetMapping("/{externalId}")
    public CardCatalogResponse findByExternalId(@PathVariable @NotBlank String externalId) {
        return cardCatalogService.findByExternalId(externalId);
    }
}
