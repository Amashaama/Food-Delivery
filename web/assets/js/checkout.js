
payhere.onCompleted = function onCompleted(orderId) {
    const  popup = new Notification();
    popup.success({
        message: "Payment completed. Order Id " + orderId
    });

    //stay 5s then redirecting to index home
    setTimeout(() => {
        window.location = "index.html";
    }, 4000);


};


payhere.onDismissed = function onDismissed() {

    console.log("Payment dismissed");
};


payhere.onError = function onError(error) {

    console.log("Error:" + error);
};




async function loadCheckoutData() {

    const popup = new Notification();

    const response = await fetch("LoadCheckoutData");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {

            console.log(json);
            const userAddress = json.userAddress;
            const cityList = json.cityList;
            const cartItems = json.cartList;
            const deliveryTypes = json.deliveryTypes;

            const city = document.getElementById("city");

            cityList.forEach(item => {
                let option = document.createElement("option");
                option.value = item.id;
                option.innerHTML = item.name;

                city.appendChild(option);
            });

            //load current address
            const current_address_checkbox = document.getElementById("checkbox");

            current_address_checkbox.addEventListener("change", function () {

                let first_name = document.getElementById("first-name");
                let last_name = document.getElementById("last-name");
                let line_one = document.getElementById("line-one");
                let line_two = document.getElementById("line-two");
                let postal_code = document.getElementById("postal-code");
                let mobile = document.getElementById("mobile");

                if (current_address_checkbox.checked) {


                    if (userAddress && userAddress.user) {
                        first_name.value = userAddress.user.first_name;
                        last_name.value = userAddress.user.last_name;
                        city.value = userAddress.city.id;
                        city.disabled = true;
                        city.dispatchEvent(new Event("change"));
                        line_one.value = userAddress.lineOne;
                        line_two.value = userAddress.lineTwo;
                        postal_code.value = userAddress.postalCode;
                        mobile.value = userAddress.user.mobile;
                    } else {
                        popup.error({
                            message: json.message
                        });
                        current_address_checkbox.checked = false;
                    }
                } else {
                    first_name.value = "";
                    last_name.value = "";
                    city.value = 0;
                    city.disabled = false;
                    city.dispatchEvent(new Event("change"));
                    line_one.value = "";
                    line_two.value = "";
                    postal_code.value = "";
                    mobile.value = "";

                }

            });

            //cart details
            const cart_item_container = document.getElementById("cartItems");
            cart_item_container.innerHTML = "";

            let total = 0;
            let totalQty = 0;
            let foodItemTypes = 0;

            cartItems.forEach(item => {
                let productSubTotal = item.foodItem.price * item.qty;
                total += productSubTotal;
                totalQty += item.qty;
                foodItemTypes += 1;

                let cardCart = `
               
               <div class="cart-item d-flex align-items-center" data-item-id="1" data-price="18.50">
                                    <img src="product-images//${item.foodItem.id}//image1.png"
                                         alt="Margherita Pizza" class="cart-item-img">
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
                                            <div class="quantity-controls">
                                                
                                                <input class="quantity-input" id="add-to-cart-qty"
                                                       value="${item.qty}" readonly>
                                                
                                                </div>
                                            
                                            
                                        </div>
                                    </div>
                                </div>
`;
                cart_item_container.innerHTML += cardCart;

            });

            // document.getElementById("totalQuantity").innerHTML = totalQty;

            document.getElementById("subtotal").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    })
                    .format(total);


            document.getElementById("totalItems").innerHTML = foodItemTypes;

            document.getElementById("finalSubtotal").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    })
                    .format(total);

            let shipping_charges = 0;
            city.addEventListener("change", (e) => {

                let cityName = city.options[city.selectedIndex].innerHTML;
                if (cityName === "Colombo") {
                    shipping_charges = deliveryTypes[0].price;
                } else {
                    shipping_charges = deliveryTypes[1].price;
                }

                document.getElementById("deliveryFee").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        })
                        .format(shipping_charges);

                document.getElementById("totalAmount").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        })
                        .format(shipping_charges + total);





                document.getElementById("finalDeliveryFee").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        })
                        .format(shipping_charges);

                document.getElementById("finalTotal").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        })
                        .format(shipping_charges + total);




            });






        } else {
            if (json.message === "Empty cart") {
                popup.error({
                    message: "Empty cart . Please add some FoodItems"
                });
                setTimeout(() => {
                    window.location = "index.html";
                }, 4000);
            } else {


                popup.error({
                    message: json.message
                });
            }
        }

    } else {
        if (response.status === 401) {
            window.location = "signIn.html";
        }
    }

}


async function checkout() {

    let checkbox1 = document.getElementById("checkbox").checked;
    let first_name = document.getElementById("first-name");
    let last_name = document.getElementById("last-name");
    let line_one = document.getElementById("line-one");
    let line_two = document.getElementById("line-two");
    let postal_code = document.getElementById("postal-code");
    let mobile = document.getElementById("mobile");
    const city_select = document.getElementById("city");

    let data = {
        isCurrentAddress: checkbox1,
        firstName: first_name.value,
        lastName: last_name.value,
        citySelect: city_select.value,
        lineOne: line_one.value,
        lineTwo: line_two.value,
        postalCode: postal_code.value,
        mobile: mobile.value
    };

    let dataJSON = JSON.stringify(data);

    const response = await fetch("Checkout", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: dataJSON
    });

    const popup = new Notification();

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            //payhere process
            payhere.startPayment(json.payhereJson);

        } else {
            popup.error({
                message: json.message
            });
        }
    } else {
        popup.error({
            message: "Something went wrong c. Please try again! "
        });
    }



}