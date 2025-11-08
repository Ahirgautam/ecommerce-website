package com.ecommerce.ecommerce_site.controller.api;

import com.ecommerce.ecommerce_site.model.Users;
import com.ecommerce.ecommerce_site.service.LikeProductService;
import com.ecommerce.ecommerce_site.utils.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class LikedProductController {

    @Autowired
    private LikeProductService likeProductService;
    @Autowired
    private  UserInfo userInfo;

    @PostMapping("/api/users/me/likes/check")
    public Map<Long, Boolean> checkLikedProducts(
            @RequestBody List<Long> productIds,
            @AuthenticationPrincipal Object pricipal
            ){

        Users user = userInfo.getUser(pricipal);
        return likeProductService.checkLikedProducts(user, productIds);
    }
}
