
export async function addProductToCart(productId, variantId, quantity, btn, callback = null){

    if(btn)
        btn.disabled = true;
    const formData = new FormData();
    formData.append("productId", productId);
    formData.append("variantId", variantId);
    formData.append("quantity", quantity);


    try{
         const re = await fetch("/api/carts/add", {
            method:"POST",
            headers:{
                "X-CSRF-TOKEN":  document.querySelector('meta[name="_csrf"]').content
            },
            body:formData
         });
        if(btn) btn.disabled = false;
        if(re.ok){
            showAlert("product added to cart");
            displayItemCountInCart();
            if(callback != null) callback();
        }
        else{
            throw new Error("request failed : add to cart");
        }
    }
    catch(e){
        console.log(e);
    }
}
export function displayItemCountInCart(){

    fetch("/api/carts/count")
    .then(re => {
        if(re.ok) return re.text();
        throw new Error();
    })
    .then(re => {
        const qnt = (Number(re) > 9 ? "9+" : re);
        document.querySelector(".cart-btn").dataset.quantity = qnt;
    })
    .catch(err => {
        console.log(err)
        document.querySelector(".cart-btn").dataset.quantity = 0;
    })
}

