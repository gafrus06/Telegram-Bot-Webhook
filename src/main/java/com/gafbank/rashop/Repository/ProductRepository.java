package com.gafbank.rashop.Repository;

import com.gafbank.rashop.entity.Product;
import com.gafbank.rashop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductByUser(User user);
    Product findProductByUserAndIsInCreating(User user, boolean isInCreating);
    @Query(value = "SELECT * FROM products WHERE name_category = :category ORDER BY RANDOM() LIMIT 1;", nativeQuery = true)
    Product findRandomProductByCategory(@Param("category") String category);
}
