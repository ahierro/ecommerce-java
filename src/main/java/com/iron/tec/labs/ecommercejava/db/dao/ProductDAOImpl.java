package com.iron.tec.labs.ecommercejava.db.dao;

import com.iron.tec.labs.ecommercejava.db.entities.Product;
import com.iron.tec.labs.ecommercejava.db.entities.ProductView;
import com.iron.tec.labs.ecommercejava.db.repository.CustomProductRepository;
import com.iron.tec.labs.ecommercejava.db.repository.ProductRepository;
import com.iron.tec.labs.ecommercejava.db.repository.ProductViewRepository;
import com.iron.tec.labs.ecommercejava.dto.ProductDTO;
import com.iron.tec.labs.ecommercejava.exceptions.Conflict;
import com.iron.tec.labs.ecommercejava.exceptions.NotFound;
import com.iron.tec.labs.ecommercejava.services.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.iron.tec.labs.ecommercejava.constants.Constants.*;

@Repository
@AllArgsConstructor
@Transactional
@Log4j2
public class ProductDAOImpl implements ProductDAO {
    private final ProductRepository productRepository;
    private final MessageService messageService;
    private final CustomProductRepository customProductRepository;

    private final ProductViewRepository productViewRepository;

    @Override
    public Mono<PageImpl<Product>> getProductPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return this.productRepository.findBy(pageRequest).collectList()
                .zipWith(this.productRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
    }
    @Override
    public Mono<PageImpl<ProductView>> getProductViewPage(int page, int size) {
//        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
//                .withMatcher("PRODUCT_ID", ExampleMatcher.GenericPropertyMatcher)
//                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
//                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        PageRequest pageRequest = PageRequest.of(page, size);
        return this.productViewRepository.findBy(pageRequest).collectList()
                .zipWith(this.productViewRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
    }

    @Override
    public Mono<ProductDTO> findById(UUID id) {
        return this.customProductRepository.findById(id).switchIfEmpty(Mono.defer(() -> {
            throw new NotFound(messageService.getRequestLocalizedMessage(ERROR_PRODUCT, NOT_FOUND, id.toString()));
        }));
    }

    @Override
    public Mono<Product> create(Product product) {
        return productRepository.save(product).doOnError(DataIntegrityViolationException.class, e -> {
            throw new Conflict(messageService.getRequestLocalizedMessage(ERROR_PRODUCT,
                    ALREADY_EXISTS, product.getId().toString()));
        });
    }

    @Override
    public Mono<Product> update(Product product) {
        return productRepository.save(product).doOnError(TransientDataAccessResourceException.class, e -> {
            throw new NotFound(messageService.getRequestLocalizedMessage(ERROR_PRODUCT, NOT_FOUND,
                    product.getId().toString()));
        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return this.productRepository.deleteProductById(UUID.fromString(id)).flatMap(numberOfDeletedRows -> {
            if (numberOfDeletedRows == 0)
                return Mono.error(new NotFound(messageService.getRequestLocalizedMessage(ERROR_PRODUCT, NOT_FOUND, id)));
            return Mono.empty();
        });
    }
}
