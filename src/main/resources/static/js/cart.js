import { isLoggedIn } from "./auth.js";


const conformationDialog = document.querySelector(".conformation-dialog");
const cartEmptyMessage = document.querySelector(".cart-empty-message");
let quantityDisplay = document.querySelectorAll(".qty-display");
let increaseQtyBtn = document.querySelectorAll(".increase-qty");
let decreaseQtyBtn = document.querySelectorAll(".decrease-qty");
const subTotalDisplay = document.querySelector(".sub-total");
const totalDisplay = document.querySelector(".total-amount");
const taxDisplay = document.querySelector(".tax-amount");

let totalProductCost = 0, totalTaxCost = 0;
function debounce(func, delay){
    let timeoutId;
    return function(...args){
        if(timeoutId){
            clearTimeout(timeoutId);
        }
        timeoutId = setTimeout(() => {
            func.apply(this, args);
        }, delay);
    }
}

const debounceUpdateTotal = debounce(updateSubTotal, 400);

function updateSubTotal(index, quantity, price, id){

    fetch(`/api/carts/${id}`, {
        method:"POST",
        headers:{
            "Content-Type" : "application/json",
            "X-CSRF-TOKEN":document.querySelector('meta[name="_csrf"]').content
        },
        body:JSON.stringify({"quantity" : quantityDisplay[index].value})
    })
    .then(re => {
        if(!re.ok){
            throw new Error();
        }
    })
    .catch(err => console.log(err));

    const value = Number(quantityDisplay[index].value);
    quantityDisplay[index].style.width = Math.min(quantityDisplay[index].value.length  , 7 )+ "ch";
    document.querySelectorAll(".sub-total")[index].innerText = price * value;
    decreaseQtyBtn[index].disabled = (value == 1);
    increaseQtyBtn[index].disabled = (value == quantity)

}

function fetchCarts(){
    fetch("/api/carts/")
    .then(re => {
        if(!re.ok){
            throw new Error("No Cart Found");
        }
        return re.json();
    })
    .then(re => {
        renderCartItems(re);
    })
    .catch(error => console.log(error))
}
function renderCartItems(data){
    if(data?.length == 0) return;
    const cartProducts = document.querySelector(".cart-products");
    let cartItemsHtml = "";
    data.forEach((ele) => {
        totalProductCost += ele.priceAtAddition * ele.quantity;
        totalTaxCost += ele.priceAtAddition * ele.quantity * 0.04;
        cartItemsHtml += `
            <div class="product flex" data-id="${ele.id}" data-price="${ele.priceAtAddition}" data-qty="${ele.stock}">
                <div class="product-image flex-center">
                    <img src="/uploads/${ele.image}" alt="product-image">
                </div>
                <div class="product-info flex-column">
                    <div>
                        <h2 class="product-name">${ele.productName}</h2>
                        <p class="flex product-brand-category">${ele.brandName}<span class="seperator">.</span> ${ele.category}</p>
                        <p>₹${ele.priceAtAddition}</p>
                    </div>
                    <div class="flex quantity-selector-container">
                        <div class="flex inc-dec-container">
                            <button class="increase-qty flex-center">
                                <img src="/images/icons/add.svg" class="invert-img">
                            </button>
                            <div class="qty-display-container">
                                <input class="qty-display" value="${ele.quantity}" type="number"  />
                            </div>
                            <button class="decrease-qty flex-center" ${ele.quantity <= 1 ? 'disabled' : ''}>
                                <img src="/images/icons/minus.svg" alt="" class="invert-img">
                            </button>
                        </div>
                        <hr class="divider">
                        <div class="sub-total-display flex-center">
                            Sub Total :  ₹<span class="sub-total">${ele.quantity * ele.priceAtAddition}</span>
                        </div>
                    </div>
                    <button class="remove-product-btn flex-center" data-id="${ele.id}">
                        remove from cart
                        <img src="/images/icons/delete.svg" .
                         alt="">
                    </button>
                </div>
            </div>
        `;
    });
    cartEmptyMessage.classList.add("hide")
    cartProducts.innerHTML = cartItemsHtml;
    subTotalDisplay.innerText = totalProductCost;
    taxDisplay.innerText = totalTaxCost;
    totalDisplay.innerText = totalProductCost + totalTaxCost;

    quantityDisplay = document.querySelectorAll(".qty-display");
    increaseQtyBtn = document.querySelectorAll(".increase-qty");
    decreaseQtyBtn = document.querySelectorAll(".decrease-qty");
    quantityDisplay.forEach((e,i) => e.addEventListener("input", function(event){
        const parent = event.target.closest(".product");
        const quantity = parent.dataset.qty;

        if(Number(quantityDisplay[i].value) > quantity){
            quantityDisplay[i].value = quantity;

        }
        else if(Number(quantityDisplay[i].value) < 1){
            quantityDisplay[i].value = 1;
        }

        debounceUpdateTotal(i,parent.dataset.qty, parent.dataset.price, parent.dataset.id);
    }))

    increaseQtyBtn.forEach((e,i) => e.addEventListener("click", function(event){
        const parent = event.target.closest(".product");
        const quantity = parent.dataset.qty;

        const value = Number(quantityDisplay[i].value) + 1;
        quantityDisplay[i].value = value > quantity ? quantity : value;
        debounceUpdateTotal(i,parent.dataset.qty, parent.dataset.price, parent.dataset.id);
    }))
    decreaseQtyBtn.forEach((e,i) => e.addEventListener("click", function(event){
        const parent = event.target.closest(".product");
        const quantity = parent.dataset.qty;
        const value = Number(quantityDisplay[i].value) - 1;
        quantityDisplay[i].value = value < 1 ? 1 : value;
        debounceUpdateTotal(i,parent.dataset.qty, parent.dataset.price, parent.dataset.id);
    }))

    document.querySelectorAll(".remove-product-btn").forEach((e) => e.addEventListener("click", function(event){

        showConfirmationDialog(event.currentTarget.dataset.id, function(id){
            fetch(`/api/carts/${id}`,{
                method:"DELETE",
                headers:{
                    "X-CSRF-TOKEN":document.querySelector('meta[name="_csrf"]').content
                }
            })
            .then(re => {
                if(!re.ok){
                    throw new Error();
                }
                re.text();
            })
            .then(re => {
                showAlert("Product removed");
                event.target.closest(".product")?.remove();
                document.querySelector(".cart-btn").dataset.quantity = cartProducts.children.length;
                if(cartProducts.children.length == 0){
                    cartEmptyMessage.classList.remove("hide")
                }
            })
            .catch(err => console.log(err))

        });
    }))
}

function showConfirmationDialog(id, callback){
    conformationDialog.showModal();
    const cancelBtn = document.querySelector("#confirmationDialogCancelBtn");
    const removeBtn = document.querySelector("#confirmationDialogRemoveBtn");
    cancelBtn.addEventListener("click", function(){
        conformationDialog.close();
    }, {once : true});
    removeBtn.addEventListener("click", function(){
        conformationDialog.close();
        callback(id);
    }, {once : true});
}

fetchCarts()

document.querySelector(".checkout-btn").addEventListener("click", function(){
    console.log(isLoggedIn())
    if(!isLoggedIn()){
        showAlert("Please Login First!", "error");
        return;
    }
    window.location =  "/shop/checkout";
})