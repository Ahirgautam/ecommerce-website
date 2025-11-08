import { customConformationDialog } from "./customConformationDialog.js";
const orderInfoDialog = document.querySelector(".order-info-dialog");
const orderUpdateDialog = document.querySelector("#orderUpdateDialog");
let orders = [];

function fetchData(){
    fetch("/api/orders")
    .then(re => {

        if(!re.ok) throw new Error();
        return re.json();
    })
    .then(re => {
        renderData(re);
    })
    .catch(err => {
        console.log(err);
    })
}

function renderData(data){
    orders = data;
    let row = "";
    data.forEach((re) => {
        const date = new Date(re.createdAt).toLocaleString("en-US", {
                    year: "numeric", month: "long", day: "numeric"
                });
        row += `
            <tr data-id="${re.orderId}">
                <td>#ORD-${re.orderId}</td>
                <td>${re.fullName}</td>
                <td>${date}</td>
                <td>₹${re.total}</td>
                <td data-orderStatus="1">${re.orderStatus}</td>
                <td data-paymentStatus="1">${re.paymentStatus}</td>
                <td>
                    <div class="flex">
                        <button class="action-btn action-btn-view" title="more info" data-id="${re.orderId}">
                            <img src="/images/icons/Eye.svg" class="invert-img" alt="">
                        </button>
                        <button class="action-btn action-btn-edit" title="edit order" data-id="${re.orderId}">
                            <img src="/images/icons/Edit.svg" class="invert-img" alt="">
                        </button>
                        <button class="action-btn action-btn-delete" data-id="${re.orderId}" title="delete order" >
                            <img src="/images/icons/delete.svg" class="invert-img"  alt="">
                        </button>
                    </div>
                </td>
            </tr>
        `
    });
    document.querySelector(".table tbody").innerHTML = row;
    document.querySelectorAll(".action-btn-edit").forEach((ele) => ele.addEventListener("click", showOrderUpdateForm));
    document.querySelectorAll(".action-btn-view").forEach((ele) => ele.addEventListener("click", showOrderInfoInDialog ));
    document.querySelectorAll(".action-btn-delete").forEach((ele) => ele.addEventListener("click", function(e){
        const id = e.currentTarget.dataset.id;
        customConformationDialog.show("Delete User", "Do you want to delete user ?", function(){

           fetch(`/api/orders/${id}`,{
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
               showAlert("Order removed");
               document.querySelector(`.table tr[data-id="${id}"]`)?.remove();
           })
           .catch(err => {
                console.log(err);
                showAlert("something went wrong","error");
           })

        });
    }));
}
fetchData();

function showOrderUpdateForm(e){
    const id = e.currentTarget.dataset.id;
    const currentOrder = orders.filter((order) => order.orderId == id)[0];

    document.getElementsByName("orderId")[0].value = id;
    document.getElementsByName("customerName")[0].value = currentOrder.fullName;
    document.getElementsByName("OrderDate")[0].value = new Date(currentOrder.createdAt).toLocaleString("en-US", {
        year: "numeric", month: "long", day: "numeric"
    });
    document.getElementsByName("paymentMethod")[0].value = currentOrder.paymentMethod;
    const orderStatus = document.getElementsByName("orderStatus")[0];
    const paymentStatus = document.getElementsByName("paymentStatus")[0];
    orderStatus.selectedIndex = [...orderStatus.options].filter((ele) => {
        if(ele.value == currentOrder.orderStatus) return true;
    })[0].index;
    paymentStatus.selectedIndex = [...paymentStatus.options].filter((ele) => {
        if(ele.value == currentOrder.paymentStatus) return true;
    })[0].index;
    orderUpdateDialog.showPopover();

}
function showOrderInfoInDialog(e){
    orderInfoDialog.showPopover();
    const currentOrder = orders.filter((ele) => ele.orderId == e.currentTarget.dataset.id)[0];
    if(!Boolean(currentOrder)) return;
    document.querySelector(".order-id").innerText = currentOrder.orderId;
    document.querySelector(".customer-full-name").innerText = currentOrder.fullName;
    document.querySelector(".customer-email").innerText = currentOrder.email;
    document.querySelector(".customer-number").innerText = currentOrder.number;
    document.querySelector(".shipping-address").innerText = currentOrder.address;
    document.querySelector(".shipping-address-city").innerText = currentOrder.city;
    document.querySelector(".shipping-address-state").innerText = currentOrder.state;

    let orderItems = "";

    currentOrder.orderItemDTOS.forEach((product) => {
        orderItems += `
            <div class="order-item flex">
                <div class="order-item-image">
                    <img src="/uploads/${product.image}" alt="">
                </div>
                <div class="order-item-info">
                    <div class="order-item-name">${product.productName}</div>
                    <div class="flex justify-between text-light">
                        <span>Qnt:${product.quantity}</span>
                        <span>₹${product.price} Each</span>
                    </div>
                    <div class="flex justify-between text-light">
                        <span>${product.category}</span>
                        <span>₹${product.price * product.quantity}</span>
                    </div>
                </div>
            </div>
        `;
    });
    document.querySelector(".order-items").innerHTML = orderItems;
}

const updateOrderInfoBtn = document.getElementById("updateOrderInfoBtn");
document.querySelector(".order-update-form").addEventListener("submit", function(e){
    e.preventDefault();
    updateOrderInfoBtn.disabled = true;

    const data = {
        orderStatus : e.target["orderStatus"].value,
        paymentStatus : e.target["paymentStatus"].value
    }
    const id =  e.target["orderId"].value;
    fetch(`/api/orders/${id}`,{
           method:"PUT",
           headers:{
               "Content-Type" : "application/json",
               "X-CSRF-TOKEN":document.querySelector('meta[name="_csrf"]').content
           },
           body:JSON.stringify(data)
       })
       .then(re => {
           updateOrderInfoBtn.disabled = false;
           if(!re.ok){
               throw new Error();
           }
           re.text();
       })
       .then(re => {
           showAlert("Order updated");
           orderUpdateDialog.hidePopover();
           const row = document.querySelector(`.table tr[data-id="${id}"]`);
           row.querySelector("td[data-orderstatus='1']").innerText = data.orderStatus;
           row.querySelector("td[data-paymentstatus='1']").innerText = data.paymentStatus;
       })
       .catch(err => {
            updateOrderInfoBtn.disabled = false;
            console.log(err);
            showAlert("something went wrong","error");
       })
})