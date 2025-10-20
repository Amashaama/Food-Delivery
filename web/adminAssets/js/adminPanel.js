async function loadAdminData() {
    const popup = new Notification();

    const response = await fetch("LoadAdminData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            document.getElementById("today").innerHTML = json.today;
            document.getElementById("most-sold").innerHTML = json.mostSoldItem;
            document.getElementById("top-buyer").innerHTML = json.bestCustomer;
            document.getElementById("d-revenue").innerHTML = "Rs " + new Intl.NumberFormat(
                "en-US",
                {
                    minimumFractionDigits: 2
                })
                .format(json.dailyEarnings);
            document.getElementById("m-revenue").innerHTML = "Rs " + new Intl.NumberFormat(
                "en-US",
                {
                    minimumFractionDigits: 2
                })
                .format(json.monthlyEarnings);


            const tbody = document.getElementById("tbody");
            tbody.innerHTML = "";

            json.foodItemList.forEach(item => {

                let dropDown = `<select class="form-control form-control-sm" style="min-width: 50px; width: 100px;" data-foodid="${item.id}" onchange="statusChange(this, ${item.id});">
`;

                json.statusList.forEach(status => {
                    const selected = item.status && item.status.id === status.id ? "selected" : "";
                    dropDown += `<option value="${status.id}" ${selected}>${status.value}</option>`;
                });

                dropDown += `</select>`;


                let tRow = ` <tr>
                                                <td>${item.id}</td>
                                                <td>${item.title}</td>
                                                <td>Rs. ${new Intl.NumberFormat(
                "en-US",
                {
                    minimumFractionDigits: 2
                })
                .format(item.price)}</td>
                                                <td>${item.user.id}</td>
                                                <td>${item.qty}</td>
                                                <td>${item.subCategory.category.name}</td>
                                                <td>${item.subCategory.name}</td>
                                                <td>
                                                    ${dropDown}
                                                </td>
                                            </tr>`;

                tbody.innerHTML += tRow;
            });


        } else {
            
            if(json.message ==="1"){
                
                window.location="signIn.html";
                
            }else{
                  popup.error({
                message: json.message
            });

            }

          



        }
    }

}


async function statusChange(selectElement, fid) {
    const popup = Notification();
    const status = selectElement.value;

    const data = {
        status: status,
        foodId: fid
    };

    const jsonData = JSON.stringify(data);

    const response = await fetch("ChangeFoodItemStatus", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: jsonData
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            popup.success({
                message: json.message
            });
            loadAdminData();
        } else {
            popup.error({
                message: json.message
            });
        }
    } else {
        popup.error({
            message: "Something went wrong. Please try again later"
        });
    }
}
