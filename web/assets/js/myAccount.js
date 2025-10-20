function loadData() {
    getCityData();
    getUserData();
    getUserQuestions();
    loadOrderHistory(0);


}

async function getUserData() {
    const response = await fetch("MyAccount");

    if (response.ok) {
        
        const json = await response.json();
        console.log(json);
        document.getElementById("username").innerHTML = `${json.firstName} ${json.lastName}`;
        document.getElementById("since").innerHTML = `Member Since ${json.since}`;
        document.getElementById("firstName").value = json.firstName;
        document.getElementById("lastName").value = json.lastName;
        document.getElementById("currentPassword").value = json.password;
        document.getElementById("mobile").value = json.mobile;

        if (json.hasOwnProperty("addressList") && json.addressList !== undefined) {
            let lineOne;
            let lineTwo;
            let city;
            let postalCode;
            let cityId;
            let index;

            const addressUL = document.getElementById("addressUL");
            addressUL.innerHTML = "";

            json.addressList.forEach((address, index) => {
                const lineOne = address.lineOne;
                const lineTwo = address.lineTwo;
                const city = address.city.name;
                const postalCode = address.postalCode;
                const cityId = address.city.id;

                const card = document.createElement("div");
                card.className = "address-card default";

                card.innerHTML = `
        <h5>Address ${index + 1}</h5>
        <p class="mb-1">${lineOne}</p>
        <p class="mb-1">${lineTwo}</p>
        <p class="mb-1">${city}</p>
        <p class="mb-1">${postalCode}</p>
        <div class="address-actions">
           

        </div>
    `;

                addressUL.appendChild(card);
            });

            const firstAddress = json.addressList[0];

            document.getElementById("lineOne").value = firstAddress.lineOne;
            document.getElementById("lineTwo").value = firstAddress.lineTwo;
            document.getElementById("postalCode").value = firstAddress.postalCode;
            document.getElementById("citySelect").value = Number(firstAddress.city.id);



        }

        //   console.log(json);
    } else {
       window.location="signIn.html";
    }
}

async function getCityData() {

    const response = await fetch("CityData");

    if (response.ok) {
        const json = await response.json();
        const citySelect = document.getElementById("citySelect");

        json.forEach(city => {
            let option = document.createElement("option");
            option.innerHTML = city.name;
            option.value = city.id;
            citySelect.appendChild(option);
        });



    }

}

async function saveChanges() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const mobile = document.getElementById("mobile").value;
    const lineOne = document.getElementById("lineOne").value;
    const lineTwo = document.getElementById("lineTwo").value;
    const postalCode = document.getElementById("postalCode").value;
    const  cityId = document.getElementById("citySelect").value;

    const userDataObject = {
        firstName: firstName,
        lastName: lastName,
        mobile: mobile,
        lineOne: lineOne,
        lineTwo: lineTwo,
        postalCode: postalCode,
        cityId: cityId

    };

    const userDataJson = JSON.stringify(userDataObject);

    const response = await fetch(
            "MyAccount", {
                method: "PUT",
                body: userDataJson,
                headers: {
                    "Content-Type": "application/json"
                }

            }
    );

    if (response.ok) {
        const json = await response.json();

        let finalMsg;
        let icon;
        let  title;
        let styleClass;

        if (json.status) {

            getUserData();

            if (json.message === "111") {
                finalMsg = "User profile details update successfully";
                icon = "success";
                title = "Updated";
                styleClass = "swal-success";

            }

        } else {



            finalMsg = json.message;
            icon = "error";
            title = "Warning";
            styleClass = "swal-error";



        }

        Swal.fire({
            icon: icon,
            title: title,
            text: finalMsg,
            customClass: {
                popup: `sweet-popup ${styleClass}`,
                confirmButton: 'swal2-confirm'
            },
            width: '300px',
            confirmButtonText: 'OK'
        });
    } else {

        Swal.fire({
            icon: "error",
            title: "Warning",
            text: "Profile Details update failed",
            customClass: {
                popup: `sweet-popup swal-error`,
                confirmButton: 'swal2-confirm'
            },
            width: '300px',
            confirmButtonText: 'OK'
        });

    }

}

async function updatePassword() {

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    const passwordDataObject = {

        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword

    };

    const passwordDataJson = JSON.stringify(passwordDataObject);

    const response = await fetch(
            "PasswordSecurity",
            {
                method: "PUT",
                body: passwordDataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();
        let finalMsg;
        let icon;
        let  title;
        let styleClass;

        if (json.status) {
            getUserData();
            if (json.message === "111") {
                finalMsg = "Password Successfully Updated.";
                icon = "success";
                title = "Updated";
                styleClass = "swal-success";

            }

            document.getElementById("newPassword").value = "";
            document.getElementById("confirmPassword").value = "";
        } else {

            if (json.message === "11") {
                finalMsg = "Password Successfully Updated.";
                icon = "success";
                title = "Updated";
                styleClass = "swal-success";

            } else {

                finalMsg = json.message;
                icon = "error";
                title = "Warning";
                styleClass = "swal-error";

            }



        }

        Swal.fire({
            icon: icon,
            title: title,
            text: finalMsg,
            customClass: {
                popup: `sweet-popup ${styleClass}`,
                confirmButton: 'swal2-confirm'
            },
            width: '300px',
            confirmButtonText: 'OK'
        });


    } else {
        Swal.fire({
            icon: "error",
            title: "Warning",
            text: "Password update failed",
            customClass: {
                popup: `sweet-popup swal-error`,
                confirmButton: 'swal2-confirm'
            },
            width: '300px',
            confirmButtonText: 'OK'
        });
    }

}
var questionList;

async function getUserQuestions() {

    const response = await fetch("MyAccount");

    if (response.ok) {
        const json = await response.json();
        console.log("question" + json);
        const question_item_container = document.getElementById("questionsList");
        question_item_container.innerHTML = "";

        questionList = json.questionList || [];

        questionList.forEach(item => {

            let statusClass = item.question_status === "Answered" ? "status-delivered" : "status-pending";
            let statusText = item.question_status === "Answered" ? "Answered" : "Pending";

            let answerField = "";
            let actionButtons = "";

            if (item.question_status === "Answered") {
                answerField = `
                
                <div class="form-group">
                            <label for="answer-${item.id}">Your Answer:</label>
                            <textarea class="form-control" id="answer-${item.id}" style="min-height: 120px; resize: vertical;" readonly>${item.answer}</textarea>
              </div>`;

                actionButtons = "";
            } else {
                answerField = `
  <div class="form-group">
                            <label for="answer-${item.id}">Your Answer:</label>
                            <textarea class="form-control" id="answer-${item.id}" style="min-height: 120px; resize: vertical;"></textarea>
              </div>`;

                actionButtons = `
                
                                       <div class="question-action-buttons">
                            <button class="btn btn-primary" onclick="submitAnswer(${item.id})">
                                <i class="bi bi-send me-2"></i>Submit Answer
                            </button>
`;

            }


            let questionCard = `
                <div class="question-item" data-question-id="${item.id}" data-status="pending">
                    <div class="question-header">
                        <span class="question-id">Question ID: ${item.id}</span>
            
                        
                        <span class="order-status ${statusClass}">${statusText}</span>
                    </div>
                    <div class="question-preview">
                        ${item.question.length > 60 ? item.question.substring(0, 60) + "..." : item.question}
                    </div>
                    <div class="question-details" id="details-${item.id}">
                        <div class="question-detail-row">
                            <span class="question-detail-label">Question:</span>
                            <span class="question-detail-value">${item.question}</span>
                        </div>
                        <div class="question-detail-row">
                            <span class="question-detail-label">Product:</span>
                            <div class="question-detail-value">
                                <div class="question-product-info">
                                    <strong>${item.foodItem.title}</strong><br>
                                    <small style="color: #999;">Product ID: ${item.foodItem.id}</small>
                                </div>
                            </div>
                        </div>
                        <div class="question-detail-row">
                            <span class="question-detail-label">Customer:</span>
                            <span class="question-detail-value">${item.user.email}</span>
                        </div>
                        <div class="question-detail-row">
                            <span class="question-detail-label">Date:</span>
                            <span class="question-detail-value">${item.date}</span>
                        </div>
                         ${answerField}
                         ${actionButtons}
                        </div>
                    </div>
                </div>
            `;


            question_item_container.innerHTML += questionCard;


        });

        attachQuestionToggleListeners();

    }

}

async function submitAnswer(itemId) {

    const answer = document.getElementById("answer-" + itemId).value;


    const answerObject = {
        answer: answer,
        itemId: itemId
    };



    const answerDataJson = JSON.stringify(answerObject);

    const response = await fetch(
            "SaveQuestion",
            {
                method: "PUT",
                body: answerDataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );



    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            Swal.fire({
                icon: "success",
                title: "Updated",
                text: json.message,
                customClass: {
                    popup: `sweet-popup swal-success`,
                    confirmButton: 'swal2-confirm'
                },
                width: '300px',
                confirmButtonText: 'OK'
            });

            document.getElementById("answer-" + itemId).value = "";
            getUserQuestions();
            document.getElementById("statusFilter").value ="All";

        } else {
            Swal.fire({
                icon: "error",
                title: "Warning",
                text: json.message,
                customClass: {
                    popup: `sweet-popup swal-error`,
                    confirmButton: 'swal2-confirm'
                },
                width: '300px',
                confirmButtonText: 'OK'
            });
        }

    }



}

function loadQuestionsOnStatus() {
    const status = document.getElementById("statusFilter").value;

    const question_item_container = document.getElementById("questionsList");
    question_item_container.innerHTML = "";

    questionList.forEach(item => {

        if (item.question_status === status) {

            let statusClass = item.question_status === "Answered" ? "status-delivered" : "status-pending";
            let statusText = item.question_status === "Answered" ? "Answered" : "Pending";

            let answerField = "";
            let actionButtons = "";

            if (item.question_status === "Answered") {
                answerField = `
                
                <div class="form-group">
                            <label for="answer-${item.id}">Your Answer:</label>
                            <textarea class="form-control" id="answer-${item.id}" style="min-height: 120px; resize: vertical;" readonly>${item.answer}</textarea>
              </div>`;

                actionButtons = "";
            } else {
                answerField = `
  <div class="form-group">
                            <label for="answer-${item.id}">Your Answer:</label>
                            <textarea class="form-control" id="answer-${item.id}" style="min-height: 120px; resize: vertical;"></textarea>
              </div>`;

                actionButtons = `
                
                                       <div class="question-action-buttons">
                            <button class="btn btn-primary" onclick="submitAnswer(${item.id})">
                                <i class="bi bi-send me-2"></i>Submit Answer
                            </button>
`;

            }


            let questionCard = `
                <div class="question-item" data-question-id="${item.id}" data-status="pending">
                    <div class="question-header">
                        <span class="question-id">Question ID: ${item.id}</span>
            
                        
                        <span class="order-status ${statusClass}">${statusText}</span>
                    </div>
                    <div class="question-preview">
                        ${item.question.length > 60 ? item.question.substring(0, 60) + "..." : item.question}
                    </div>
                    <div class="question-details" id="details-${item.id}">
                        <div class="question-detail-row">
                            <span class="question-detail-label">Question:</span>
                            <span class="question-detail-value">${item.question}</span>
                        </div>
                        <div class="question-detail-row">
                            <span class="question-detail-label">Product:</span>
                            <div class="question-detail-value">
                                <div class="question-product-info">
                                    <strong>${item.foodItem.title}</strong><br>
                                    <small style="color: #999;">Product ID: ${item.foodItem.id}</small>
                                </div>
                            </div>
                        </div>
                        <div class="question-detail-row">
                            <span class="question-detail-label">Customer:</span>
                            <span class="question-detail-value">${item.user.email}</span>
                        </div>
                        <div class="question-detail-row">
                            <span class="question-detail-label">Date:</span>
                            <span class="question-detail-value">${item.date}</span>
                        </div>
                         ${answerField}
                         ${actionButtons}
                        </div>
                    </div>
                </div>
            `;


            question_item_container.innerHTML += questionCard;
        } else if (status === "All") {

            let statusClass = item.question_status === "Answered" ? "status-delivered" : "status-pending";
            let statusText = item.question_status === "Answered" ? "Answered" : "Pending";

            let answerField = "";
            let actionButtons = "";

            if (item.question_status === "Answered") {
                answerField = `
                
                <div class="form-group">
                            <label for="answer-${item.id}">Your Answer:</label>
                            <textarea class="form-control" id="answer-${item.id}" style="min-height: 120px; resize: vertical;" readonly>${item.answer}</textarea>
              </div>`;

                actionButtons = "";
            } else {
                answerField = `
  <div class="form-group">
                            <label for="answer-${item.id}">Your Answer:</label>
                            <textarea class="form-control" id="answer-${item.id}" style="min-height: 120px; resize: vertical;"></textarea>
              </div>`;

                actionButtons = `
                
                                       <div class="question-action-buttons">
                            <button class="btn btn-primary" onclick="submitAnswer(${item.id})">
                                <i class="bi bi-send me-2"></i>Submit Answer
                            </button>
`;

            }


            let questionCard = `
                <div class="question-item" data-question-id="${item.id}" data-status="pending">
                    <div class="question-header">
                        <span class="question-id">Question ID: ${item.id}</span>
            
                        
                        <span class="order-status ${statusClass}">${statusText}</span>
                    </div>
                    <div class="question-preview">
                        ${item.question.length > 60 ? item.question.substring(0, 60) + "..." : item.question}
                    </div>
                    <div class="question-details" id="details-${item.id}">
                        <div class="question-detail-row">
                            <span class="question-detail-label">Question:</span>
                            <span class="question-detail-value">${item.question}</span>
                        </div>
                        <div class="question-detail-row">
                            <span class="question-detail-label">Product:</span>
                            <div class="question-detail-value">
                                <div class="question-product-info">
                                    <strong>${item.foodItem.title}</strong><br>
                                    <small style="color: #999;">Product ID: ${item.foodItem.id}</small>
                                </div>
                            </div>
                        </div>
                        <div class="question-detail-row">
                            <span class="question-detail-label">Customer:</span>
                            <span class="question-detail-value">${item.user.email}</span>
                        </div>
                        <div class="question-detail-row">
                            <span class="question-detail-label">Date:</span>
                            <span class="question-detail-value">${item.date}</span>
                        </div>
                         ${answerField}
                         ${actionButtons}
                        </div>
                    </div>
                </div>
            `;


            question_item_container.innerHTML += questionCard;


        }

    });

    attachQuestionToggleListeners();



}



//QUESTION TOGGLE FUNCTION

function attachQuestionToggleListeners() {
    const questionItems = document.querySelectorAll('.question-item');

    questionItems.forEach(item => {
        item.addEventListener('click', function (e) {
            if (e.target.closest('button') || e.target.closest('textarea') || e.target.closest('input'))
                return;

            const questionId = this.dataset.questionId;
            const details = document.getElementById(`details-${questionId}`);

            document.querySelectorAll('.question-details').forEach(detail => {
                if (detail !== details)
                    detail.classList.remove('show');
            });

            document.querySelectorAll('.question-item').forEach(qi => qi.classList.remove('active'));

            if (details.classList.contains('show')) {
                details.classList.remove('show');
                this.classList.remove('active');
            } else {
                details.classList.add('show');
                this.classList.add('active');
            }
        });
    });
}

let current_page = 0;
const product_per_page = 4;

async function loadOrderHistory(page) {

    const popup = new Notification();
    const response = await fetch("LoadOrderHistory");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);

          

            const orderHistoryContainer = document.getElementById("orders-container");
            orderHistoryContainer.innerHTML = "";

            const orderList = json.orderList;
            const orderItemsList = json.orderItemsList;

            const start = page * product_per_page;
            const end = start + product_per_page;

            const paginatedOrders = orderList.slice(start, end);




            paginatedOrders.forEach(order => {
                let total = 0;
                let orderItem = "";


                json.orderItemsList.forEach(item => {


                    if (item.orders.id === order.id) {

                        let statusClass = "";
                        let statusText = item.orderStatus.value;

                        switch (statusText.toLowerCase()) {
                            case "delivered":
                                statusClass = "status-delivered";
                                break;
                            case "pending":
                                statusClass = "status-pending";
                                break;
                            default:
                                statusClass = "status-cancelled ";
                        }

                        orderItem += `  <div class="order-item-name">${item.foodItem.title} × ${item.qty}   <span class="order-status ${statusClass}"> ${statusText}</span></div>   `;


                        total += item.qty * item.foodItem.price;
                    }

                });

                let orderCard = `
               
               <div class="order-item">
                                        <div class="order-header">
                                            <div>
                                                <span class="order-id">${order.id}</span>
                                                <small class="text-muted ms-2">${order.created_at}</small>
                                            </div>
                                            
                                        </div>
                
                              
                                        <div class="order-items">
                                           ${orderItem}
                                        </div>
                                        <div class="order-total">Total: Rs. ${total}</div>
                                        
                                    </div>


`;

                orderHistoryContainer.innerHTML += orderCard;

            });

            //////////////////////////////////////////////////////////////////////////
            const st_pagination_container = document.getElementById("st-pagination-container");
            if (st_pagination_container) {
                st_pagination_container.innerHTML = "";
            }



            const totalOrders = orderList.length;
            const totalPages = Math.ceil(totalOrders / product_per_page);

            // Create button element base
            const st_pagination_button = document.createElement("button");

            // Prev
            if (current_page > 0) {
                const prevBtn = st_pagination_button.cloneNode(true);
                prevBtn.innerHTML = "Prev";
                prevBtn.className = "btn btn-outline-secondary me-2";
                prevBtn.addEventListener("click", (e) => {
                    current_page--;
                    loadOrderHistory(current_page);
                    e.preventDefault();
                });
                st_pagination_container.appendChild(prevBtn);
            }

            // Page buttons
            for (let i = 0; i < totalPages; i++) {
                const pageBtn = st_pagination_button.cloneNode(true);
                pageBtn.innerHTML = i + 1;
                pageBtn.className = i === current_page
                        ? "btn btn-primary fw-bold me-2"
                        : "btn btn-outline-secondary me-2";
                pageBtn.addEventListener("click", (e) => {
                    current_page = i;
                    loadOrderHistory(current_page);
                    e.preventDefault();
                });
                st_pagination_container.appendChild(pageBtn);
            }

            // Next
            if (current_page < totalPages - 1) {
                const nextBtn = st_pagination_button.cloneNode(true);
                nextBtn.innerHTML = "Next";
                nextBtn.className = "btn btn-outline-secondary me-2";
                nextBtn.addEventListener("click", (e) => {
                    current_page++;
                    loadOrderHistory(current_page);
                    e.preventDefault();
                });
                st_pagination_container.appendChild(nextBtn);
            }
           }

        
    }

}

