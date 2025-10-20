function mainLoad() {
    loadData();
    loadQUestions();
}


async function loadData() {
    const searchParams = new URLSearchParams(window.location.search);

    if (searchParams.has("id")) {
        const productId = searchParams.get("id");
        const response = await fetch("LoadSingleProduct?id=" + productId);

        if (response.ok) {
            const json = await response.json();
            if (json.status) {

                console.log(json);

                document.getElementById("main-image").src = "product-images\\" + json.foodItem.id + "\\image1.png";
                document.getElementById("gallery-thumb1").src = "product-images\\" + json.foodItem.id + "\\image1.png";
                document.getElementById("gallery-thumb2").src = "product-images\\" + json.foodItem.id + "\\image2.png";
                document.getElementById("gallery-thumb3").src = "product-images\\" + json.foodItem.id + "\\image3.png";
                //  update the data-image attributes
                document.getElementById("gallery-thumb1").dataset.image = "product-images/" + json.foodItem.id + "/image1.png";
                document.getElementById("gallery-thumb2").dataset.image = "product-images/" + json.foodItem.id + "/image2.png";
                document.getElementById("gallery-thumb3").dataset.image = "product-images/" + json.foodItem.id + "/image3.png";
                document.getElementById("productTitle").innerHTML = json.foodItem.title;
                document.getElementById("publishedOn").innerHTML = json.foodItem.created_at;
                document.getElementById("productPrice").innerHTML = json.foodItem.price;
                  document.getElementById("product-badge").innerHTML = json.foodItem.portionSize.name;
                document.getElementById("productDescription").innerHTML = json.foodItem.description;
                document.getElementById("category").innerHTML = json.foodItem.subCategory.category.name;
                document.getElementById("subCategory").innerHTML = json.foodItem.subCategory.name;
                document.getElementById("quality").innerHTML = json.foodItem.quality.value;
                document.getElementById("status").innerHTML = json.foodItem.status.value;
                document.getElementById("stock").innerHTML = json.foodItem.qty;


                //add to cart main button
                const addToCartMain = document.getElementById("add-to-cart-main");
                addToCartMain.addEventListener(
                        "click", (e) => {
                    addToCart(json.foodItem.id, document.getElementById("add-to-cart-qty").value);
                    e.preventDefault();
                }

                );



                // similar products
                let similar_product_main = document.getElementById("smiler-product-main");
                let productHtml = document.getElementById("similar-product");
                similar_product_main.innerHTML = "";

                json.foodItemList.forEach(item => {

                    let productCloneHtml = productHtml.cloneNode(true);
                    productCloneHtml.querySelector("#similar-product-a1").href = "singleProduct.html?id=" + item.id;
                    productCloneHtml.querySelector("#similar-product-image").src = "product-images\\" + item.id + "\\image1.png";
                    //add to cart button function call
                    productCloneHtml.querySelector("#similar-product-add-to-cart").addEventListener(
                            
                            "click",(e)=>{
                                addToCart(item.id,1);
                        e.preventDefault();
                            }
                            
                            
                            );


                    productCloneHtml.querySelector("#similar-product-sub-category").innerHTML = item.subCategory.name;
                    productCloneHtml.querySelector("#similar-product-a2").href = "singleProduct.html?id=" + item.id;
                    productCloneHtml.querySelector("#similar-product-title").innerHTML = item.title;
                    productCloneHtml.querySelector("#similar-product-price").innerHTML = "Rs. " +
                            new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);
                    similar_product_main.appendChild(productCloneHtml);

                });




            } else {
                window.location = "index.html";
            }


        } else {
            window.location = "index.html";
        }

    }


}

async function sendQuestion() {

    const popup = Notification();

    const searchParams = new URLSearchParams(window.location.search);



    if (searchParams.has("id")) {
        const productId = searchParams.get("id");
        console.log(productId);
        const question = document.getElementById("customerQuestion").value;

        const data = {
            id: productId,
            question: question
        };


        const response = await fetch(
                "SaveQuestion",
                {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data)
                }

        );


        if (response.ok) {
            const json = await response.json();
            if (json.status) {

                popup.success(
                        {
                            message: "Question sent successfully"
                        }

                );

                document.getElementById("customerQuestion").value = "";

            } else {
                popup.error(
                        {
                            message: json.message
                        }
                );
            }

        }
    }

}

async function loadQUestions() {
    const popup = Notification();

    const searchParams = new URLSearchParams(window.location.search);



    if (searchParams.has("id")) {

        const productId = searchParams.get("id");
        const response = await fetch("LoadCustomerQuestions?id=" + productId);

        if (response.ok) {

            const json = await response.json();

            if (json.status) {

                const question_items_container = document.getElementById("question-container");
                question_items_container.innerHTML = "";

                json.questionList.forEach(item => {

                    let questionCard = `
                    
                    
                     <div class="qa-item">
                                <div class="question">
                                    <div class="question-meta">
                                        <strong>${item.user.first_name} ${item.user.last_name}</strong>
                                        <span class="question-date">${item.date}</span>
                                    </div>
                                    <p class="question-text">${item.question}</p>
                                </div>
                                <div class="answer">
                                    <div class="answer-meta">
                                        
                                        <span class="answer-badge">Answer</span>
                                       
                                    </div>
                                    <p class="answer-text">${item.answer}</p>
                                </div>
                            </div>
`;

                    question_items_container.innerHTML += questionCard;
                });
            }
        }
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




