package com.gafbank.rashop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    private Long chatId;
    @Enumerated(EnumType.STRING)
    private Action action;
    private String username;

    @ManyToMany
    @JoinTable(
            name = "box",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @ManyToMany
    @JoinTable(
            name = "sellProducts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> sellProducts;
    private String selectedCategory;
    public void addProduct(Product product){
        if(products == null){
            products = new ArrayList<>();
        }
        products.add(product);
    }
    public void addSellProduct(Product product){
        if(sellProducts == null){
            sellProducts = new ArrayList<>();
        }
        sellProducts.add(product);
    }

}
