import { isLoggedIn } from "./auth.js";
import { addProductToCart,displayItemCountInCart } from "./addToCart.js";

const pageNumbers = document.querySelector(".page-numbers");
const prevBtn = document.querySelector(".prev-page");
const nextBtn = document.querySelector(".next-page");
const pageSize = 5;
export function renderProducts(data, callback, isFetchLike = true){

    const maxPages = Math.min(data.totalPages, 5);
    const currentPage = data.page;
    const starting = Math.max(1, currentPage-1);

    pageNumbers.innerHTML = "";

    for(let i = starting; i <= starting+maxPages && i <= data.totalPages; i++){
        const li = document.createElement("li");
        const a = document.createElement("a");
        a.textContent = i;
        a.classList.add("page-number");
        if(i === data.page + 1) {
            a.classList.add("current-page");
        }
        a.addEventListener("click", function(){
            callback(i-1, pageSize)
        })
        a.href = "#";
        li.append(a);
        pageNumbers.append(li);
    }

    prevBtn.disabled = data.first;
    nextBtn.disabled = data.last;
    prevBtn.onclick = ()=> callback(currentPage-1, pageSize);
    nextBtn.onclick = () => callback(currentPage+1, pageSize);
    if(data.content.length == 0){

        document.querySelector(".products-container").innerHTML = "<h3><center>no product found</center> </h3>"
        return;
    }
    let productCards = "";
    data.content.forEach(product => {
        // console.log()
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
                    <img src="/images/icons/stroke_heart.svg" class="invert-img"  alt="">
                </button>
            </div>
        </div>`;
    })
    if(isLoggedIn() && isFetchLike){
        const productIds = data.content.map(product => product.productId)

        fetch("/api/users/me/likes/check", {
            method:"POST",
            body:JSON.stringify(productIds),
            headers: {
        	"Content-Type": "application/json",
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        })
        .then(re => re.json())
        .then(re => {
            document.querySelectorAll(".like-product-btn").forEach(btn => {
                if(re[btn.dataset.id] == true){
                    btn.querySelector("img").src = "/images/icons/fill_heart.svg";
                }
            });
        })
        .catch(err => console.log(err))
    }
    document.querySelector(".products-container").innerHTML = productCards;
    document.querySelectorAll(".add-to-cart").forEach(ele => ele.addEventListener("click", function(){
        addProductToCart(ele.dataset.id, ele.dataset.variant,1,ele);
    }))
}