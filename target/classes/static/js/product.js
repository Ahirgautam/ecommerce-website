let isLast = false;
let currentPage = 0;
let pageSize = 1;
window.addEventListener("DOMContentLoaded", function(){
    const param = new URLSearchParams(window.location.search);

    if(param.get("product-added") === "true"){
        showAlert("Product Added SuccessFully");
        removeAttributeFromUrl();
    }
});
function removeAttributeFromUrl(){
    const newUrl = window.location.origin + window.location.pathname;
    history.replaceState({}, document.title, newUrl);
}


async function fetchProducts(search = "", categoryId = null){
    const params = new URLSearchParams();
    if(search) params.append("search",search);
    if(categoryId) params.append("categoryId", categoryId);
    params.append("page", currentPage);
    params.append("size", pageSize);

    const response = await fetch(`/api/products?${params.toString()}`);
    if(!response.ok){
        showAlert("Failed to load product", "error");
        throw new Error("Failed to load product");
    }

    const data = await response.json();
    return data;
}

function renderProducts(data, reset = false){

    if(!data) return;
    const tbody = document.querySelector(".product-table tbody");
    if(reset)
        tbody.innerHTML = "";

    data.content.forEach(product => {
      const productRow = document.createElement("tr");
      productRow.setAttribute("data-productid", product.productId);
      productRow.innerHTML = `
        <td>
          <button class="toggle-inner-table-btn" data-productid="${product.productId}" onclick="toggleInnerTable(event)">
            <img src="/images/icons/arrow_down.svg" alt="" class="invert-img">
            ${product.productName}
          </button>
        </td>
        <td>${product.category}</td>
        <td>${product.brand}</td>
        <td>
          <div class="flex">
            <button class="action-btn" onclick="window.location.href='/products/edit/${product.productId}'">
              <img src="/images/icons/Edit.svg" alt="" class="invert-img">
            </button>
            <button class="action-btn" onclick="deleteProduct(${product.productId},'${product.productName}')">
              <img src="/images/icons/Trash.svg" class="invert1" alt="" class="invert-img">
            </button>
          </div>
        </td>
      `;
      tbody.appendChild(productRow);


      const innerRow = document.createElement("tr");
      innerRow.setAttribute("data-productid", product.productId);
      innerRow.classList.add("inner-table-row", "hide"); // hidden by default

      let variantsHtml = "";
      product.variants.forEach(v => {
        let attrs = v.attributes.map(a => `${a.name}: ${a.value}`).join(", ");

        variantsHtml += `
          <tr data-variantid=${v.id}>
            <td class="product-name">
                <img src="/uploads/${v.images[0]}" class="circle-product-image" onclick="showImageInModal(event)" alt="">
                ${v.name ?? product.productName}
            </td>
            <td>${v.sku}</td>
            <td>${v.stock}</td>
            <td>$${v.basePrice}</td>
            <td>$${v.discountPrice}</td>
            <td>${attrs}</td>
            <td>
              <div class="flex">
                <button class="action-btn" onclick="window.location.href='/products/edit/${product.productId}'">
                  <img src="/images/icons/Edit.svg" class="invert-img" alt="">
                </button>
                <button class="action-btn" onclick="deleteVariant(${v.id},'${v.name}')">
                  <img src="/images/icons/delete.svg" class="invert-img" alt="">
                </button>
              </div>
            </td>
          </tr>
        `;
      });

      innerRow.innerHTML = `
        <td colspan="4">
          <table class="inner-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>SKU</th>
                <th>Stock</th>
                <th>Base Price</th>
                <th>Discounted Price</th>
                <th>Attributes</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              ${variantsHtml}
            </tbody>
          </table>
        </td>
      `;
      tbody.appendChild(innerRow);
    });
}
async function searchProduct(event){
    const query = event.target.value.trim();
    currentPage = 0;
    isLast = false;
    const data = await fetchProducts(query, null);
    isLast = data.last;
    renderProducts(data, true);
}
async function findProductByCategory(event){
    const id = event.target.value;
    currentPage = 0;
    isLast = false;
    const data = await fetchProducts("", id);
    isLast = data.last;
    renderProducts(data, true);
}
function debounce(func, d){
    let timer;

    return function(...args){
        if(timer) return;
        timer = true;
        setTimeout(function(){
            timer = false;
            func.apply(this, args);
        },d);
    }
}

const debounceSearch = debounce(searchProduct, 500)



function showImageInModal(event){
    const imageDialog = document.querySelector(".image-dialog");
    imageDialog.querySelector("img").src = event.target.src;
    imageDialog.showModal();
}

document.querySelector("#close-model").addEventListener("click", function() {
    const dialog = document.querySelector(".image-dialog");
    dialog.close();
});

async function loadMore(){
    if(isLast){
        showAlert("No More Products", "error");
           return;
    }

    currentPage++;
    const data = await fetchProducts('',null);
    renderProducts(data);

    isLast = data.last;
}
function toggleInnerTable(event){
  const productId = (event.currentTarget.dataset.productid);
  const tabelTr = document.querySelector(`.product-table tr[data-productid='${productId}'].inner-table-row`);
  tabelTr?.classList.toggle("hide");
  event.currentTarget?.querySelector("img").classList.toggle("rotate180");
}
const modal = document.querySelector("#modal1");
let variantId = -1;
function closeModal(){
  modal.close();
}
function showConformationModal(id){
  modal.showModal();

}
function deleteVariantFunc(id){
  modal.close();
  fetch(`http://localhost:8080/api/variant/${encodeURIComponent(id)}`,{
    method:"DELETE",
    headers: {
      'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
    }
  }).then(re => {
    if(!re.ok) throw new Error("something went wrong");
    showAlert("Product Variant Deleted")
    document.querySelector(`.inner-table tr[data-variantid='${id}']`)?.remove();
  }).catch(err => {
    showAlert(err,"error")
  })
}
function deleteVariant(id,name){
    modal.querySelector(".modal-title").innerText = "Delete Variant";
    modal.querySelector(".modal-message").innerHTML = `this will delete <strong>${name}</strong> product variant `;
    document.querySelector("#accept-btn").setAttribute("onclick", `deleteVariantFunc(${id})`);
    modal.showModal();
}
function deleteProduct(id, name){
    modal.querySelector(".modal-title").innerText = "Delete Product";
    modal.querySelector(".modal-message").innerHTML = `this will delete <strong>${name}</strong> product `;
    document.querySelector("#accept-btn").setAttribute("onclick", `deleteProductFunc(${id})`);
    modal.showModal();
}

function deleteProductFunc(id){
    modal.close();
    fetch(`http://localhost:8080/api/product/${encodeURIComponent(id)}`,{
        method:"DELETE",
        headers: {
           'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    }).then(re => {
        if(!re.ok) throw new Error("something went wrong");
        showAlert("Product Deleted")
        Array.from(document.querySelectorAll(`.product-table tr[data-productid='${id}']`))
             .forEach(ele => ele.remove());
    }).catch(err => {
        showAlert(err.message,"error")
    })
}
(async function(){
    const data = await fetchProducts();
    isLast = data.last;
    renderProducts(data, true);
})()

