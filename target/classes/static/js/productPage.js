import { addProductToCart } from "./addToCart.js";

const imagePreviewSlider = document.querySelector(".side-image-preview");
const imageSlider = document.querySelector("#image-slider");
const quantityDisplay = document.querySelector(".qty-display");
const increaseQtyBtn = document.querySelector(".increase-qty");
const decreaseQtyBtn = document.querySelector(".decrease-qty");
const addToCartBtn = document.querySelector(".add-to-cart-btn");
const orderNow = document.querySelector(".secondary-btn.order-now");
let imageSliderImage;
let numberOfImage;
let selectedVariant = 0;
window.addEventListener("DOMContentLoaded", async function(){
    await renderProduct();
    numberOfImage = product["variants"][selectedVariant]["images"].length + 2;

});

function updateQuantity(){
    let totalCost = quantityDisplay.value * product["variants"][selectedVariant].discountPrice;
    document.querySelector(".sub-total").innerText = quantityDisplay.value + " X " + "₹"+ product["variants"][selectedVariant].discountPrice + " = ₹"+totalCost  ;
     quantityDisplay.value = Math.min(1,product["variants"][selectedVariant].stock);
     quantityDisplay.disabled = quantityDisplay.value == 0;
     decreaseQtyBtn.disabled = (quantityDisplay.value == Math.min(1,product["variants"][selectedVariant].stock));
     increaseQtyBtn.disabled = (quantityDisplay.value == product["variants"][selectedVariant].stock)
}
updateQuantity();
async function renderProduct(){

    try{
        if(product == null){
            document.querySelector(".product-not-found").classList.remove("hide");
            throw new Error("product with given id not found ");
        }
    }
    catch(error){
        console.log(error);
        return;
    }

    document.querySelector(".product-name").textContent = product.productName;
    document.querySelector(".product-brand").textContent = product.brand;
    const bp = product.variants[selectedVariant].basePrice;
    const dp = product.variants[selectedVariant].discountPrice;
    document.querySelector(".current-price").innerHTML = "<span> &#8377; </span>" + bp;
    if(dp < bp){
        document.querySelector(".old-price").innerHTML = "<span> &#8377; </span>"  + dp;
        const dst = Math.floor(dp / bp) * 100 ;
        if(dst > 0)document.querySelector(".discount").textContent = dst + "%";
    }
    if(product.variants.length < 2){
        document.querySelector(".variants-container").classList.add("hide");
    }

    let variants = "";
    product["variants"].forEach(function(variant, index){
        if(index != selectedVariant){
            let atr = "";
            variant["attributes"].forEach(function(attribute){

               atr += `<p class="variant-attribute">${attribute["name"]} : ${attribute["value"]}</p>`

            })
            variants += `<a class="variant"  data-index="${index}" >
                             <div class="variant-image">
                                <img src=/uploads/${variant['images'][0]} />
                             </div>
                             <div class="variant-content">
                                 <p class="variant-name">${variant.name}</p>
                                 ${atr}
                             </div>
                         </a>`
        }
        else{
            createImages(variant);
        }

    })
    document.querySelector(".variants-container .variants").innerHTML = variants;
    document.querySelectorAll(".variant").forEach(ele => ele.addEventListener("click", changeVariant))
    let isOutOfStock = product["variants"][selectedVariant].stock === 0
    orderNow.disabled = isOutOfStock;
    addToCartBtn.disabled = isOutOfStock;
    document.querySelector(".out-of-stock-chip").classList.toggle("hide", !isOutOfStock);
}

function changeVariant(event){
    selectedVariant = event.currentTarget.dataset.index;
    updateQuantity();
    renderProduct();
}
function createImages(variant){
    const ec = document.querySelector(".elipses");
    imageSlider.innerHTML = "";
    imagePreviewSlider.innerHTML = "";
    ec.innerHTML = "";
    variant["images"].forEach(function(image, index){
        const li = document.createElement("li");
        const div = document.createElement("div");
        const img = document.createElement("img");
        li.className = "image-li";
        div.className = "div-image";
        img.src = "/uploads/"+image;
        img.alt = "product image " + index;
        div.append(img)
        li.append(div)
        imageSlider.append(li);

        const imgBtn = document.createElement("button");
        const imgBtnImage = document.createElement("img");
        if(index == 0) imgBtn.classList.add("active");
        imgBtn.classList.add("preview-image");
        imgBtnImage.src = "/uploads/"+image;
        imgBtnImage.alt = "product image " + index;
        imgBtn.append(imgBtnImage);
        imagePreviewSlider.append(imgBtn);

        const els = document.createElement("span");
        els.className = "elips";
        ec.append(els);
    })
    imageSliderImage = document.querySelector(".image-li");
    const firstImage = imageSlider.lastElementChild.cloneNode(true);
    const lastImage =  imageSlider.firstElementChild.cloneNode(true);
    firstImage.dataset.isCopy = "1";
    lastImage.dataset.isCopy = "1";
    imageSlider.prepend(firstImage);
    imageSlider.append(lastImage);

    moveSliderForward();
    document.querySelector(".elips").classList.add("active");

}
imagePreviewSlider.addEventListener("click", function(e){
    if(e.target.className == e.currentTarget.className) return;
    let previewImage = e.target;
    if(e.target.classList.contains("preview-image")){
        previewImage = e.target.querySelector("img");
    }
    const images = Array.from(imageSlider.querySelectorAll(".image-li:not([data-is-copy='1']) img"));
    for(let image of images){
        imageSlider.scrollLeft = image.offsetLeft;
        if(image.src == previewImage.src){
            break;
        }
    }
});


document.querySelector("#slider-back-btn").addEventListener("click", function(e){
        moveSliderBackword();
})
document.querySelector("#slider-forward-btn").addEventListener("click", function(e){
        moveSliderForward();
})

let initx = 0;
imageSlider.addEventListener("dragstart", function(e){

    const img = new Image();
        img.src =
          "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' width='1' height='1'></svg>";
        e.dataTransfer.setDragImage(img, 0, 0);
    initx = e.screenX;
})
imageSlider.addEventListener("dragend", function(e){
    if(Math.abs(initx-e.offsetX) > imageSliderImage.clientWidth/2){
        if(initx - e.offsetX > 0)
            moveSliderForward();
        else
            moveSliderBackword();
    }
})

imageSlider.addEventListener("touchstart", function(e) {

    initx = e.touches[0].screenX;
}, { passive: true });

imageSlider.addEventListener("touchend", function(e) {
    let endX = e.changedTouches[0].screenX;

    if (Math.abs(initx - endX) > 50) {
        if (initx - endX > 0) {
            moveSliderForward();
        } else {
            moveSliderBackword();
        }
    }
}, { passive: true });

function moveSliderForward(){
    imageSlider.scrollLeft += Math.floor(imageSliderImage.clientWidth);
}

function moveSliderBackword(){
    imageSlider.scrollLeft -= Math.floor(imageSliderImage.clientWidth);
}

function updateElips(){
    document.querySelector(".elips.active")?.classList.remove("active")
    document.querySelector(".preview-image.active")?.classList.remove("active")
    let index = Math.round(imageSlider.scrollLeft / imageSliderImage.clientWidth) - 1;
    index = Math.max(0, index);
    document.querySelectorAll(".elips")[index]?.classList.add("active")
    document.querySelectorAll(".preview-image")[index]?.classList.add("active");
}

imageSlider.addEventListener("scrollend", function(){
    if(Math.ceil(imageSlider.scrollLeft) + 10 >= Math.floor(imageSliderImage.clientWidth * (numberOfImage-1)) ){
        imageSlider.classList.remove("scroll-smooth");
        imageSlider.scrollLeft = imageSliderImage.clientWidth;
        imageSlider.classList.add("scroll-smooth");
    }
    if(Math.floor(imageSlider.scrollLeft) == 0){

        imageSlider.classList.remove("scroll-smooth");
        imageSlider.scrollLeft = imageSlider.scrollWidth- imageSliderImage.clientWidth * 2;
        imageSlider.classList.add("scroll-smooth");
    }
    updateElips();
})



addToCartBtn.addEventListener("click", function(e){
    addProductToCart(product.productId, product["variants"][selectedVariant].id, Number(quantityDisplay.value), e.target);
})

orderNow.addEventListener("click", function(e){
    addProductToCart(product.productId, product["variants"][selectedVariant].id, Number(quantityDisplay.value), e.target, function(){
        window.location = "/shop/cart";
    });

})


quantityDisplay.addEventListener("input", function(e){

    let minValue = Math.min(1,product["variants"][selectedVariant].stock);
    if(Number(quantityDisplay.value) > product["variants"][selectedVariant].stock){
        quantityDisplay.value = product["variants"][selectedVariant].stock;

    }
    else if(Number(quantityDisplay.value) < minValue){
        quantityDisplay.value = minValue;
    }
    updateSubTotal()
})

increaseQtyBtn.addEventListener("click", function(e){

    const value = Number(quantityDisplay.value) + 1;
    quantityDisplay.value = value > product["variants"][selectedVariant].stock ? product["variants"][selectedVariant].stock : value;
    updateSubTotal()
})
decreaseQtyBtn.addEventListener("click", function(e){

    const value = Number(quantityDisplay.value) - 1;
    quantityDisplay.value = value < 1 ? Math.min(1,product["variants"][selectedVariant].stock ) : value;
    updateSubTotal()
})

function updateSubTotal(){
    const value = Number(quantityDisplay.value);
    quantityDisplay.style.width = quantityDisplay.value.length  + "ch";
    let totalCost = product["variants"][selectedVariant].discountPrice * value
    document.querySelector(".sub-total").innerText = value + " X " + "₹"+ product["variants"][selectedVariant].discountPrice + " = ₹"+totalCost  ;

    decreaseQtyBtn.disabled = (value == Math.min(1,product["variants"][selectedVariant].stock));
    increaseQtyBtn.disabled = (value == product["variants"][selectedVariant].stock)

}