
function fetchOrders(){
    fetch("/api/orders")
    .then(re => {
        if(!re.ok) throw new Error("some error occured");
        return re.json();
    })
    .then(data => {
        renderData(data)
    })
    .catch(error => {
        console.log(error)
    })
}
fetchOrders();

function renderData(data){
    if(data == null || data.length == 0) return;
    let orders = "";
    data.forEach(order => {
        let products = "";
        order.orderItemDTOS.forEach(item => {
            products += `
                <div class="order-product flex">
                    <div class="order-product-image">
                        <img src="/uploads/${item.image}" alt="">
                    </div>
                    <div class="order-product-info">
                        <span class="order-product-name">${item.productName}</span>
                        <div class="product-category-brand-container">
                            <span class="order-product-brand">${item.brand}</span>
                            <span class="seperator">/</span>
                            <span class="order-product-category">${item.category}</span>
                        </div>
                    </div>
                </div>
            `
        });
        const orderStatus = `<span class="order-status  ${order.orderStatus} ordered">${order.orderStatus}</span>`
        const date = new Date(order.createdAt).toLocaleString("en-US", {
            year: "numeric", month: "long", day: "numeric"
        });
        const orderTimeLine = getOrderTimeLineHtml(order.orderStatus);
       orders += `
            <div class="order-info">
                <div class="order-info-header flex">
                    <h2>#ORD-2025-${order.orderId}</h2>
                    <span class="order-created-date">${date}</span>
                    <span class="order-total-price">₹${order.total}</span>
                </div>
                <div class="order-products flex">
                    ${products}
                </div>
                <div class="order-progress-bar flex" data-status="${order.orderStatus}">
                    ${orderTimeLine}
                </div>
                <div class="order-info-footer flex">
                    ${orderStatus}

                    <button class="primary-btn">View details</button>
                </div>
            </div>
       `;
    });

    document.querySelector(".order-container").innerHTML = orders;

}
function getOrderTimeLineHtml(status) {
  const steps = ["order placed", "processing", "shipped", "delivered"];

  const statusOrder = {
    PENDING: 0,
    PROCESSING: 1,
    SHIPPED: 2,
    DELIVERED: 3,
  };

  const currentIndex = statusOrder[status] ?? -1;

  return steps
    .map((step, index) => {
      const isComplete = index <= currentIndex;
      return `
        <div class="progress-point ${isComplete ? "complete" : ""}">
          <span class="progress-point-circle"></span>
          <span class="progress-point-name">${step}</span>
        </div>
      `;
    })
    .join("");
}
