package com.iron.tec.labs.ecommercejava.db.entities;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("PRODUCT")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends AuditableEntity {

    private String name;
    private String description;
    private Integer stock;
    @DecimalMin("1.0")
    @EqualsAndHashCode.Exclude
    private BigDecimal price;
    private String smallImageUrl;
    private String bigImageUrl;


    @EqualsAndHashCode.Include
    private BigDecimal priceWithoutTrailingZeros() {
        return price != null ? price.stripTrailingZeros() : null;
    }

}
