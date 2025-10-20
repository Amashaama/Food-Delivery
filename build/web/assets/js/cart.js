async function loadCartItems() {

    const popup = new Notification();

    const response = await fetch("LordCartItems");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            console.log(json);
            const cart_item_container = document.getElementById("cartItems");
            cart_item_container.innerHTML = "";

            let total = 0;
            let totalQty = 0;
            let foodItemTypes = 0;

            json.cartItems.forEach(item => {



                let productSubTotal = item.foodItem.price * item.qty;
                total += productSubTotal;
                totalQty += item.qty;
                foodItemTypes += 1;

                let notAvailableLabel = "";
                if (item.foodItem.qty === 0) {
                    notAvailableLabel = `
                    
                     <div class="not-available-label">
                Not Available
            </div>

`;
                }

                let cardCart = `
               
               <div class="cart-item d-flex align-items-center" data-item-id="1" data-price="18.50">
                 <div class="cart-image-wrapper">
                                    <img src="product-images//${item.foodItem.id}//image1.png"
                                         alt="Margherita Pizza" class="cart-item-img">
                 ${notAvailableLabel}
            </div>
                                    <div class="cart-item-content">
                                        <h3 class="cart-item-title">${item.foodItem.title}</h3>
                                        <p class="cart-item-description">
                ${item.foodItem.description.length > 60 ? item.foodItem.description.substring(0, 60) + "..." : item.foodItem.description}
</p>
                                        <div class="cart-item-price"><span>Rs. </span>${new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        })
                        .format(item.foodItem.price)}</div>
                                        <div class="cart-actions">
                                           
                                                
                                               
                                              <div class="quantity-input" id="add-to-cart-qty">${item.qty}</div>


                                                
                                          
                                           
                                            <button class="btn-remove-cart" onclick="removeFromCart(${item.id},${item.foodItem.id})">
                                                <i class="bi bi-trash"></i>Remove
                                            </button>
                                        </div>
                                    </div>
                                </div>
`;
                cart_item_container.innerHTML += cardCart;

            });

            document.getElementById("totalQuantity").innerHTML = totalQty;

            document.getElementById("cartSubtotal").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    })
                    .format(total);

            document.getElementById("totalItems").innerHTML = foodItemTypes;




            document.getElementById("summarySubtotal").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    })
                    .format(total);
        } else {
            document.getElementById("cart-section").innerHTML = `
    <div style="
        display: flex;
        justify-content: center;
        align-items: center;
        height: 300px;
        width: 100%;
        font-size: 24px;
        color: var(--default-color);
        font-weight: bold;
        text-align: center;
        padding: 30px;
        margin: 0 auto;
    ">
        Your Cart is Empty
    </div>
`;


        }
    }
}


async function removeFromCart(cartId, foodItemId) {

    const popup = new Notification();

    const response = await fetch("LordCartItems",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({cartId: cartId, foodItemId: foodItemId})
            }
    );

    const json = await response.json();

    if (json.status) {
        console.log(json);
        popup.success({
            message: json.message
        });
        loadCartItems();


    } else {
        popup.error({
            message: json.message
        });
    }




}



