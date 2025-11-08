import { isLoggedIn } from "./auth.js";

const shipingInput = document.querySelectorAll(".shipping-option-input");
const checkoutForm = document.querySelector(".checkout-form");
const placeOrderBtn = document.querySelector(".place-order-btn");
let productTotalPrice = 0, shippingPrice = 0, tax = 0;

(function(){
    if(isLoggedIn()){
        let user = isLoggedIn();
        console.log(user)
        document.querySelector("#email").value = user.email;

    }
})()

shipingInput.forEach((ele)=> ele.addEventListener("click", function(){
    if(ele.checked){
        document.querySelector(".shipping-price-display").innerHTML = ele.dataset.price;
        shippingPrice = Number(ele.dataset.price);
        showTotalPrice()
    }
}))
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
    if(data == null || data.length == 0){
        showAlert("Please Add Product First! ", "error");
        return;
    }
    let rowHtml = ""
    data.forEach((item) => {
        let totalPrice = item.quantity * item.priceAtAddition;
        productTotalPrice += totalPrice;
        rowHtml += `
            <div class="order-item flex">
                <div class="image">
                    <img src="/uploads/${item.image}" alt="">
                </div>
                <div >
                    <h3>${item.productName}</h3>
                    <div class="light-text">
                        <span>₹${item.priceAtAddition}</span>
                        <span class="seperator">.</span>
                        <span>Quantity : ${item.quantity}</span>
                        <span class="seperator">.</span>
                        <span>Total : ₹${totalPrice} </span>
                    </div>
                </div>
            </div>
        `
    });
    tax = productTotalPrice * 0.10;
    document.querySelector(".order-items").innerHTML = rowHtml;
    document.querySelector(".sub-total").textContent = productTotalPrice;
    document.querySelector(".tax-amount").textContent = tax;
    showTotalPrice();
}

function showTotalPrice(){
    document.querySelector(".total").textContent = tax + shippingPrice + productTotalPrice;
}
fetchCarts();

checkoutForm.addEventListener("submit", function (e) {
    e.preventDefault();
    if(productTotalPrice == 0){
        showAlert("Please Add Product First! ", "error");
        return;
    }
    const formData = new FormData(e.target);
    const data = {}

    for (let [key, value] of formData.entries()) {
       data[key] = value;
    }


    placeOrderBtn.disabled = true;
    fetch("/api/orders", {
        method: "POST",
        headers: {
            "Content-Type" : "application/json",
            "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').content
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        placeOrderBtn.disabled = false;
        if (!response.ok) {
            throw new Error("Failed to submit order.");
        }
        return response.text();
    })
    .then(data => {
        showAlert("Order submitted successfully");
        console.log("Order submitted successfully", data);

    })
    .catch(error => {
        placeOrderBtn.disabled = false;
        showAlert("Some error occurred", "error");
        console.error("Error submitting order:", error.message);
    });
});
