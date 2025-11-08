import { isLoggedIn } from "./auth.js";
import { renderProducts } from "./renderProducts.js";
import { fetchProduct } from "./fetchProduct.js";


const urlParams = new URLSearchParams(window.location.search);
const url = new URL(window.location);
const pageSize = 5;
const minRange = document.getElementById("minRange");
const maxRange = document.getElementById("maxRange");
const minOutput = document.getElementById("minOutput");
const maxOutput = document.getElementById("maxOutput");
const currentTrack = document.querySelector(".range-selected-track");
let priceRange;
const pageNumbers = document.querySelector(".page-numbers");
const prevBtn = document.querySelector(".prev-page");
const nextBtn = document.querySelector(".next-page");

document.querySelectorAll(".price-range").forEach((ele) => ele.addEventListener("input", updateValues));


(function(){
    if(urlParams.get("categories") != null){
         const searchParameters = new URLSearchParams(window.location.search).toString();
         fetchFilteredProducts(0,pageSize,searchParameters);
    }
    else{
        fetchProduct(urlParams.get("page") ?? 0,5);
    }
    fetch("/api/products/minMaxPrice")
    .then(result => {

        if(result.ok)
            return result.json();
        throw new Error("unexpected Error");
    })
    .then(re => {
        priceRange = re;
        minRange.min = re["minimum"];
        minRange.max = re["maximum"];
        minRange.value = re["minimum"];
        maxRange.min = re["minimum"];
        maxRange.max = re["maximum"];
        maxRange.value = re["maximum"];
        updateValues()
    })
    .catch(err => console.error(err))
})()


function updateValues() {
    let minVal = parseInt(minRange.value);
    let maxVal = parseInt(maxRange.value);
    let val = 0;
    // Ensure min is never greater than max - 1
    if (minVal > maxVal) {
        val = Math.min(minVal+1, priceRange.maximum);
        maxRange.value = val;
        maxVal = val;
    }
    if(minVal == maxVal){
        val = Math.max(priceRange.minimum, maxVal-1);
        minRange.value = val;
        minVal = val;
    }

    const percentMin = (minVal / minRange.max) * 100;
    const percentMax = (maxVal / maxRange.max) * 100;
    currentTrack.style.left = percentMin + "%";
    currentTrack.style.width = (percentMax - percentMin) + "%";
    minOutput.value = minVal;
    maxOutput.value = maxVal;
}



const FilterToggle = document.querySelector(".toggle-filter-sidebar-btn");
const filterMenuCloseBtn = document.querySelector(".filter-menu-close-btn");
FilterToggle.addEventListener("click", () => {
    document.querySelector(".filters-menu").classList.toggle("hide");
});
filterMenuCloseBtn.addEventListener("click", () => {
    document.querySelector(".filters-menu").classList.add("hide");
});








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


let categories;
const categoryContainer = document.querySelector(".category-ul");
function loadCategories(){
    fetch("/api/categories/parent")
    .then(result => result.ok ? result.json() : Promise.reject("bad response"))
    .then(result => {
        categories = result;
        createCategoryCheckBox(categoryContainer,categories);
    })
    .catch(err => {
        showAlert("unknown Error", "error");
        console.error(err)
    });
}
function createCategoryCheckBox(container,data){
    let ul = "";
    data.forEach(c => {
        ul += `
            <li class="category-li">
                <div class="category-checkbox-container ">
                    <input type="checkbox" name="categories" id="${c.id}" value="${c.id}" class="custom-checkbox category-checkbox">
                    <label for="${c.id}" >${c.name}</label>
                </div>
                <ul class="sub-category-container flex-column"></ul>
            </li>
        `
    })
    container.innerHTML = ul;
}

categoryContainer.addEventListener("click", function(e){

    if(e.target.classList.contains("category-checkbox")){
        const checkbox = e.target;
        if(checkbox.checked){
            const parent = findCategoryById(Number(checkbox.value), categories);
            if(parent && parent.children.length > 0){
                createCategoryCheckBox(checkbox.closest("li").querySelector("ul"), parent.children);
            }
        }
        else{
            checkbox.closest("li").querySelector("ul").innerHTML = "";
        }
    }
});
function findCategoryById(id, nodes) {
    for (let node of nodes) {
        if (node.id === id) return node;
        if (node.children?.length) {
            const found = findCategoryById(id, node.children);
            if (found) return found;
        }
    }
    return null;
}
loadCategories();

const filterForm = document.querySelector(".filter-form");
filterForm.addEventListener("submit", function(e){
    e.preventDefault();
    fetchFilteredProducts();
})

function fetchFilteredProducts(page = 0, pSize = 5,searchParameters = ""){



    if(searchParameters == ""){
        const formData = new FormData(filterForm);
        const allCheckedCategories = formData.getAll("categories");
        const allCheckedBrands = formData.getAll("brands");

        searchParameters = new URLSearchParams({
            "categories":allCheckedCategories,
            "brands":allCheckedBrands,
            "minPrice":formData.get("minPrice"),
            "maxPrice":formData.get("maxPrice"),
            "page":page,
            "size":pageSize
        }).toString();
    }

    const curUrl = new URL(window.location);
    curUrl.search = searchParameters;
    window.history.pushState({}, "", curUrl);

    fetch(`/api/product/filter?${searchParameters}`)
    .then(result => result.json())
    .then(re => renderProducts(re,fetchFilteredProducts))
    .catch(err => console.error(err))
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



