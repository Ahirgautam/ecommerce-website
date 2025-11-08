import { isLoggedIn } from "./auth.js";
import { addProductToCart,displayItemCountInCart } from "./addToCart.js";

fetchProducts();

function fetchProducts(){
    fetch("/api/products/favorite")
    .then(re => {
        if(!re.ok){
            throw new Error();
        }
        return re.json();
    })
    .then(re => {
        renderProducts(re,);
    })

}

function renderProducts(data){

    if(data.length == 0){
        document.querySelector(".products-container").innerHTML = "<h3><center>no product found</center> </h3>"
        return;
    }
    let productCards = "";
    data.forEach(product => {

        let discount = "";
        if(product.variants[0].basePrice != product.variants[0].discountedPrice){
            discount = `<span class="product-original-price">product.variants[0].discountedPrice</span>
                        <span class="product-discount">10% off</span>`;
        }
        productCards +=
        `<div class="product-card flex">
            <a href="/product/${product.productId}" class="product-image">
                <img src="/uploads/${product.variants[0].images[0]}" alt="Product Image" >
            </a>
            <div class="product-details">
                <a href="/product/${product.productId}" class="product-card-link">
                    <h2 class="product-title">${product.productName}</h2>
                    <h3 class="product-brand">${product.brand}</h3>
                    <div class="price-container">
                        <span class="product-price">₹${product.variants[0].basePrice}</span>


                    </div>
                </a>
                <button class="primary-btn add-to-cart" data-id="${product.productId}" data-variant="${product.variants[0].id}">
                    <img src="/images/icons/add_shopping_cart.svg" alt="">
                    Add to Cart
                </button>
                <button class="like-product-btn" title="like product"  data-id="${product.productId}">
                    <img src="/images/icons/fill_heart.svg"  alt="">
                </button>
            </div>
        </div>`;
    })

    document.querySelector(".products-container").innerHTML = productCards;
    document.querySelectorAll(".add-to-cart").forEach(ele => ele.addEventListener("click", function(){
        addProductToCart(ele.dataset.id, ele.dataset.variant,1,ele);
    }))
}

function likeProduct(btn){
    if(!isLoggedIn()){
        showAlert("please login first !", "error");
        return;
    }
    btn.classList.add("processing");
    fetch(`/api/products/${btn.dataset.id}/like`)
    .then(response => {
        if(response.ok){
            return response.json();
        }
    })
    .then(response => {
        if(response.liked){
            btn.querySelector("img").src = "/images/icons/fill_heart.svg";
        }
        else{
            btn.querySelector("img").src = "/images/icons/stroke_heart.svg";
        }
        btn.classList.remove("processing");
    })
    .catch(err => {
        showAlert("request failed!", "error");
        btn.classList.remove("processing");
    })
}

document.querySelector(".products-container").addEventListener("click", function(e){
    let btn = null;
    if(e.target.classList.contains("like-product-btn")
       ){
        btn = e.target;
    }
    else if(e.target.closest(".like-product-btn")){
        btn = e.target.closest(".like-product-btn");
    }
    else{
        return;
    }
    likeProduct(btn);
})