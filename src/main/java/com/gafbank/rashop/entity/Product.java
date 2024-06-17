package com.gafbank.rashop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Random;
import java.util.UUID;


@Entity
@Table(name = "products")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private Long productId;
    private String photoId;
    private String title;
    private String description;
    private Long price;
    private String nameCategory;
    private Boolean isInCreating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @PrePersist
    public void generatorId(){
        String characters = "0123456789";
        StringBuilder productNumber = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            productNumber.append(characters.charAt(index));
        }
        productId = Long.valueOf(productNumber.toString());

    }


}
