async function loadData() {
    const popup = Notification();

    const response = await fetch("LoadData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            loadSelect("category", json.categoryList, "name");

            loadOptions("portion", json.portionSizeList, "name");
            loadOptions("spice", json.spiceLevelList, "name");

            loadOptions("quality", json.qualityList, "value");

            updateProductView(json);

        } else {
            popup.error({
                message: "Somthing went wrong"
            });
        }
    } else {
        popup.error({
            message: "Somthing went wrong"
        });
    }


}

function loadSelect(selectId, list, property) {

    const select = document.getElementById(selectId);

    list.forEach(item => {

        const option = document.createElement("option");

        option.value = item.id;
        option.innerHTML = item[property];
        select.appendChild(option);

    });


}



function loadOptions(prefix, dataList, property) {

    let options = document.getElementById(prefix + "-options");
    let li = document.getElementById(prefix + "-li");

    options.innerHTML = "";

    dataList.forEach(item => {

        let li_clone = li.cloneNode(true);
        li_clone.querySelector("#" + prefix + "-a").innerHTML = item[property];

        options.appendChild(li_clone);
    });

    const all_li = document.querySelectorAll("#" + prefix + "-options li");
    all_li.forEach(list => {
        list.addEventListener("click", function () {
            all_li.forEach(y => {
                y.classList.remove("chosen");
            });
            this.classList.add("chosen");
        });

    });
}

async function searchProduct(firstResult) {

    const popup = new Notification();
    
    const searchQuery = document.getElementById("searchQuery").value;

    const category_name = document.getElementById("category").value;



    const portion_name = document.getElementById("portion-options").
            querySelector(".chosen")?.querySelector("a").innerHTML;

    const quality_name = document.getElementById("quality-options").
            querySelector(".chosen")?.querySelector("a").innerHTML;

    const spice_name = document.getElementById("spice-options").
            querySelector(".chosen")?.querySelector("a").innerHTML;

    const sort_value = document.getElementById("sortBy").value;

    const min_price = document.getElementById("minPrice").value;

    const max_price = document.getElementById("maxPrice").value;

    const data = {
        firstResult: firstResult,
        searchQuery:searchQuery,
        category: category_name,
        portionName: portion_name,
        qualityName: quality_name,
        spiceName: spice_name,
        sortValue: sort_value,
        minPrice: min_price,
        maxPrice: max_price

    };

    const dataJson = JSON.stringify(data);

    const response = await fetch("SearchProducts",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: dataJson
            });

    if (response.ok) {

        const json = await response.json();
        if (json.status) {
            console.log(json);
            if (json.allProductCount === 0) {
               
              const noResultDisplay = document.getElementById("noResults");
              noResultDisplay.style.display ="block";
              
              const product_container = document.getElementById("productsGrid");
              product_container.style.display="none";
              
               const st_pagination_container = document.getElementById("st-pagination-container");
               st_pagination_container.style.display = "none";

            }else if(json.message==="Invalid Min Price"){
                popup.error({
                message: "Invalid Min Price"
            });
                
            }else if(json.message==="Invalid Max Price"){
                popup.error({
                message: "Invalid Max Price"
            });
                
            }else {
                updateProductView(json);
            }
        } else {



            popup.error({
                message: "Something went wrong. Please try again later 5"
            });

        }

    } else {
        popup.error({
            message: "Something went wrong. Please try again later"
        });
    }

}


// product card parent node
let st_pagination_button = document.getElementById("st-pagination-button");
let current_page = 0;

function  updateProductView(json) {
    const product_container = document.getElementById("productsGrid");
    product_container.innerHTML = "";

    json.foodItemList.forEach(item => {

        let productCard = `
          <div class="product-card" id="st-product">
                                    <div class="product-image">
                                        <a href="singleProduct.html?id=${item.id}">
                                        <img src="product-images//${item.id}//image1.png" id="st-product-img-1" alt="Grilled Salmon">
                                        </a>
                                    </div>
                                    <div class="product-info">
                                        <h4 class="product-title">${item.title}</h4>
                                        <p class="product-description">
                                           ${item.description.length > 40 ? item.description.substring(0, 40) + "..." : item.description}
                                        </p>
                                        <div class="product-meta">
                                            <span>Rs. <span class="product-price">${new Intl.NumberFormat(
                "en-US",
                {
                    minimumFractionDigits: 2
                })
                .format(item.price)}</span></span>
                                        </div>
                                        
                                        <button class="btn-add-cart" onclick="addToCart(${item.id}, 1);">
                                            <i class="bi bi-cart-plus"></i> Add to Cart
                                        </button>
                                    </div>
                                </div>
`;
        product_container.innerHTML += productCard;
    });

    const st_product = document.getElementById("st-product");

    //////////////////////////////////////////////////////////////////////////
    let st_pagination_container = document.getElementById("st-pagination-container");
    st_pagination_container.innerHTML = "";
    let all_product_count = json.allProductCount;
    // document.getElementById("all-item-count").innerHTML = all_product_count;
    let product_per_page = 6;
    let pages = Math.ceil(all_product_count / product_per_page); // round upper integer 

    //previous-button
    if (current_page !== 0) {
        let st_pagination_button_prev_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_prev_clone.innerHTML = "Prev";
        st_pagination_button_prev_clone.addEventListener(
                "click", (e) => {
            current_page--;
            searchProduct(current_page * product_per_page);
            e.preventDefault();
        });
        st_pagination_container.appendChild(st_pagination_button_prev_clone);
    }


    // pagination-buttons
    for (let i = 0; i < pages; i++) {
        let st_pagination_button_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_clone.innerHTML = i + 1;
        st_pagination_button_clone.addEventListener(
                "click", (e) => {
            current_page = i;
            searchProduct(i * product_per_page);
            e.preventDefault();
        });

        if (i === Number(current_page)) {
            st_pagination_button_clone.className = "axil-btn btn btn-primary btn-lg fw-bold ml--10";
        } else {
            st_pagination_button_clone.className = "axil-btn btn btn-outline-secondary btn-lg ml--10";
        }
        st_pagination_container.appendChild(st_pagination_button_clone);
    }

    // next-button
    if (current_page !== (pages - 1)) {
        let st_pagination_button_next_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_next_clone.innerHTML = "Next";
        st_pagination_button_next_clone.addEventListener(
                "click", (e) => {
            current_page++;
            searchProduct(current_page * product_per_page);
            e.preventDefault();
        });
        st_pagination_container.appendChild(st_pagination_button_next_clone);
    }

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



function clearFilters(){
    
    document.getElementById("searchQuery").value = "";
    document.getElementById("category").value = "0";
    document.getElementById("minPrice").value = "";
    document.getElementById("maxPrice").value = "";
    document.getElementById("sortBy").value = "";
    document.getElementById("quickSort").value = "relevance";
    
    clearFilterTags("portion-options");
    clearFilterTags("spice-options");
    clearFilterTags("quality-options");
    
    loadData();
    
    
}

function clearFilterTags(containerId){
    const container = document.getElementById(containerId);
    if(container){
        const tags = container.querySelectorAll(".filter-tag");
        tags.forEach(tag=>{
           
           tag.classList.remove("chosen");
            
        });
    }
    
}


