package com.ecommerce.ecommerce_site.service;

import com.ecommerce.ecommerce_site.model.LikedProducts;
import com.ecommerce.ecommerce_site.model.Products;
import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.repo.LikedProductsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LikeProductService {
    @Autowired
    private LikedProductsRepo likeRepository;

    public boolean toggleLike(Users user, Products product) {
        Optional<LikedProducts> existing = likeRepository.findByUsersAndProducts(user, product);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false;
        } else {
            LikedProducts like = new LikedProducts();
            like.setUsers(user);
            like.setProducts(product);
            likeRepository.save(like);
            return true;
        }
    }

    public Map<Long, Boolean> checkLikedProducts(Users user, List<Long> productIds) {
        List<LikedProducts> liked = likeRepository.findByUsersAndProducts_ProductIdIn(user, productIds);

        Set<Long> likedIds = liked.stream()
                .map(lp -> lp.getProducts().getProductId())
                .collect(Collectors.toSet());

        Map<Long, Boolean> result = new HashMap<>();
        for (Long id : productIds) {
            result.put(id, likedIds.contains(id));
        }
        return result;
    }
}
