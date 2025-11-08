const forwardBtn = document.getElementById("forwardBtn");
const backwardBtn = document.getElementById("backwardBtn");
const bestsellersCardsContainer = document.getElementsByClassName("bestsellers-cards-scroll")[0];
const bestsellerCard = [...bestsellersCardsContainer.getElementsByClassName("product-card")];
const cardPagination = document.getElementsByClassName("card-pagination")[0];
function moveCardSliderForward(){
    const cardWidth = bestsellerCard[0].clientWidth;
    const scrollLeft = bestsellersCardsContainer.scrollLeft;
    const maxScrollLeft = bestsellersCardsContainer.scrollWidth - bestsellersCardsContainer.clientWidth;

    if (scrollLeft >= maxScrollLeft) {
        bestsellersCardsContainer.style.scrollBehavior =  "unset";
        bestsellersCardsContainer.scrollLeft = 0;
        bestsellersCardsContainer.style.scrollBehavior =  "smooth";
    } else {
        bestsellersCardsContainer.scrollLeft += cardWidth;
    }

}
setInterval(moveCardSliderForward, 3000);
forwardBtn.addEventListener('click', ()=>{
    moveCardSliderForward();
});
backwardBtn.addEventListener('click', ()=>{
    const cardWidth = bestsellerCard[0].clientWidth;
    if(bestsellersCardsContainer.scrollLeft <= 0){
        bestsellersCardsContainer.style.scrollBehavior =  "unset";
        bestsellersCardsContainer.scrollLeft = bestsellersCardsContainer.scrollWidth;
        bestsellersCardsContainer.style.scrollBehavior =  "smooth";
    }
    else{
        bestsellersCardsContainer.scrollLeft -= cardWidth;
    }
});
