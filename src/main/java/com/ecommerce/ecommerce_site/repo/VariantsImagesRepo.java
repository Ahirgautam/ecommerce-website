package com.ecommerce.ecommerce_site.repo;

import com.ecommerce.ecommerce_site.model.VariantImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantsImagesRepo extends JpaRepository<VariantImages, Long> {

}
