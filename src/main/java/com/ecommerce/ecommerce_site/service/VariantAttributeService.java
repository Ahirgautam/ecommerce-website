package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.dto.VariantAttributeDTO;
import com.ecommerce.ecommerce_site.model.ProductVariants;
import com.ecommerce.ecommerce_site.model.VariantAttribute;
import com.ecommerce.ecommerce_site.repo.VariantAttributeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VariantAttributeService {

    @Autowired
    private VariantAttributeRepo variantAttributeRepo;

    public void setVariantAttribute(List<VariantAttributeDTO> variantAttributeDTOS, ProductVariants productVariant) {

        for(VariantAttributeDTO variantAttributeDto : variantAttributeDTOS){
            VariantAttribute variantAttribute = new VariantAttribute();
            variantAttribute.setAttribute_name(variantAttributeDto.getName());
            variantAttribute.setAttribute_value(variantAttributeDto.getValue());
            variantAttribute.setProductVariant(productVariant);

            variantAttributeRepo.save(variantAttribute);
        }
        ResponseEntity.ok("done");
    }
}
