function indexOnloadFunctions() {
    checkSessionCart();
    loadProductData();

}



async function loadProductData() {

    const popup = new Notification();
    const response = await fetch("LoadHomeData");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            console.log(json);
            loadCategory(json);
            loadNewArrivals(json);
        } else {
            popup.error(
                    {
                        message: "Something went wrong ! Try again shortly"
                    }

            );
        }


    } else {
        popup.error(
                {
                    message: "Something went wrong ! Try again shortly"
                }

        );
    }



}

function loadCategory(json) {
    const foodItem_category_container = document.getElementById("foodiItem-category-container");
    let foodItem_category_card = document.getElementById("foodItem-category-card");


    foodItem_category_container.innerHTML = "";

    json.categoryList.forEach(item => {

        let foodItem_category_card_clone = foodItem_category_card.cloneNode(true);

        if (item.name === "Appetizers") {
            foodItem_category_card_clone.querySelector("#category-icon").innerHTML = "🍤";
        } else if (item.name === "Main Course") {
            foodItem_category_card_clone.querySelector("#category-icon").innerHTML = "🍝";
        } else if (item.name === "Desserts") {
            foodItem_category_card_clone.querySelector("#category-icon").innerHTML = "🍰";
        } else if (item.name === "Beverages") {
            foodItem_category_card_clone.querySelector("#category-icon").innerHTML = "🥤";
        } else if (item.name === "Snacks") {
            foodItem_category_card_clone.querySelector("#category-icon").innerHTML = "🥪";
        } else if (item.name === "Soups") {
            foodItem_category_card_clone.querySelector("#category-icon").innerHTML = "🥘";
        } else if (item.name === "Salads") {
            foodItem_category_card_clone.querySelector("#category-icon").innerHTML = "🥗";
        } else {
            foodItem_category_card_clone.querySelector("#category-icon").innerHTML = "🥡";
        }

        foodItem_category_card_clone.querySelector("#category-card-link").href="advancedSearch.html";

        foodItem_category_card_clone.querySelector("#category-title").innerHTML = item.name;

        foodItem_category_container.appendChild(foodItem_category_card_clone);




    });


}

function loadNewArrivals(json) {

    const product_container = document.getElementById("product-container");

    product_container.innerHTML = "";

    json.foodItemList.forEach(item => {

        let productCard = `
       
       
        <div class="col-lg-4 col-md-6">
            <div class="product-card">
              <div class="position-relative">
               <a href="singleProduct.html?id=${item.id}">
                <img src="product-images\\${item.id}\\image1.png" alt="Pizza" class="product-img">
                </a> 
              </div>
              <div class="p-3">
                <h5>${item.title}</h5>
                <p class="product-card-description">${item.description.length > 60 ? item.description.substring(0,60)+"...":item.description}</p>
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                   <span class="price">Rs.</span> <span class="price"> ${
                new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price)}</span>
                    
                  </div>
                  <button class="cart-btn" onclick="addToCart(${item.id}, 1);"><i class="bi bi-cart-plus"></i> Add to Cart</button>
                </div>
              </div>
            </div>
          </div>

`;

product_container.innerHTML += productCard;
    });


}

async function addToCart(productId, qty) {

    const popup = new Notification();

    const response = await fetch("AddToCart?prId=" + productId + "&qty=" + qty);

    if (response.ok) {

        const json = await response.json();
        if (json.status) {
            popup.success({
                message: json.message
            });
        } else {
            popup.error({
                message: json.message
            });

        }

    } else {

    }



}

async function checkSessionCart(){
    const popup = new Notification();
    const response = await fetch("CheckSessionCart");
    
    if(!response.ok){
        popup.error({
           message : "Something went wrong ! Try again index" 
        });
    }
    
}
