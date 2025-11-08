import { renderProducts } from "./renderProducts.js";

export function fetchProduct(page = 0, size = pageSize, searchQuery = ""){
    const url = new URL(window.location);
    url.search = "";
    url.searchParams.set("page", page);
    url.searchParams.set("search", searchQuery);
    window.history.pushState({}, "", url);
    fetch(`/api/products?page=${page}&size=${size}&liked=true&search=${searchQuery}`).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    }).then(data => {
        renderProducts(data, fetchProduct);
    }).catch(error => {
        console.error('There has been a problem with your fetch operation:', error);
    });
}