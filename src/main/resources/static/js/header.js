import { fetchProduct } from "./fetchProduct.js"
const searchField = document.querySelectorAll(".search-field");
const searchSuggestion = document.querySelectorAll(".search-suggestion");

async function fetchProducts(search = "", categoryId = null){
    const params = new URLSearchParams();
    if(search) params.append("search",search);
    if(categoryId) params.append("categoryId", categoryId);
    params.append("page", 0);
    params.append("size", 10);

    const response = await fetch(`/api/products?${params.toString()}`);
    if(!response.ok){
        showAlert("Failed to load product", "error");
        throw new Error("Failed to load product");
    }

    const data = await response.json();
    return data;
}

function debounce(func, delay){
    let timer = null;
    return function(...args){
        if(timer) clearTimeout(timer);
        timer = setTimeout(function(){
            func.apply(this,args);
        }, delay);
    }
}

const debounceSearch = debounce(search, 500);

async function search(event){
    const searchQuery = event.target.value.trim();
    if(searchQuery == "")return;
    const data = await fetchProducts(searchQuery,null);


    if(!data || data.content.length == 0){
        searchSuggestion[event.target.dataset.index].innerHTML = "<p class=not-found>NO PRODUCT FOUND</p>";
        return;
    }
    let html = "";
    data.content.forEach(ele => {
           ele.variants?.forEach(variant => {
                html += `<a class="suggestion" href="/product/${ele.productId}">
                            ${ele.productName}
                        </a>`;

           });
    });


    searchSuggestion[event.target.dataset.index].innerHTML = html;
}



searchField.forEach((ele, index) => ele.addEventListener("input", debounceSearch));
searchField.forEach((ele, index) => ele.addEventListener("focus", function(e){
    searchSuggestion[index].classList.remove("hide");
    if(e.target.value == ""){
         searchSuggestion[event.target.dataset.index].innerHTML = "";
    }
}));
searchField.forEach((ele, index) => ele.addEventListener("blur", function(){
    setTimeout(function(){
        searchSuggestion[index].classList.add("hide");
    },250);
}));
searchField.forEach((ele, index) => ele.addEventListener("keypress", function(e){
    if(e.charCode == 13 && e.target.value != ""){
        fetchProduct(0,5,e.target.value);
    }
}));

searchSuggestion.forEach((ele) => ele.addEventListener("focus", function(){
    console.log("focus");
}))

