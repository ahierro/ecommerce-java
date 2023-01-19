package com.iron.tec.labs.ecommercejava.services;

import com.iron.tec.labs.ecommercejava.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CategoryService {
    Mono<CategoryDTO> getById(UUID id);

    Mono<CategoryDTO> createCategory(CategoryCreationDTO categoryCreationDTO);

    Mono<CategoryDTO> updateCategory(String id, CategoryUpdateDTO categoryCreationDTO);

    Flux<CategoryDTO> getAll();
    Mono<PageResponseDTO<CategoryDTO>> getCategoryPage(ProductPageRequestDTO pageRequest);

    Mono<Void> deleteCategory(String id);
}
