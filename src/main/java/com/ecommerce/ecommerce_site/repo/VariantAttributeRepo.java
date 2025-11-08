package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.VariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantAttributeRepo extends JpaRepository<VariantAttribute, Long> {
}
