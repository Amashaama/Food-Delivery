async function loadOrders() {
    const popup = new Notification();

    const response = await fetch("LoadAdminData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            document.getElementById("today").innerHTML = json.today;
            const tbody = document.getElementById("tbody");
            tbody.innerHTML = "";

            json.orderItemList.forEach(item => {

                let dropDown = `<select id="stsSelector" class="form-control form-control-sm" style="min-width: 50px; width:60px;" data-foodid="${item.id}" onchange="statusChange(this,${item.id});">`;

                json.orderStatusList.forEach(status => {
                    const selected = item.orderStatus && item.orderStatus.id === status.id ? "selected" : "";
                    dropDown += `<option value="${status.id}" ${selected}>${status.value}</option>`;
                });

                dropDown += `</select>`;

                let tRow = `
                                                <td>${item.orders.id}</td>
                                                <td>${item.id}</td>
                                                <td>${item.rating}</td>
                                                <td>${item.foodItem.title}</td>
                                                <td>${item.qty}</td>
                                                <td>${item.orders.created_at}</td>
                                                <td>${item.orders.user.first_name} ${item.orders.user.last_name}</td>
                                                <td>${item.orders.deliveryType.name}</td>
                                                <td>
                                                    ${dropDown}
                                                </td>
`;
                tbody.innerHTML += tRow;


            });


        }else{
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

async function statusChange(selectElement,oid){
    const popup = Notification();
     const status = selectElement.value;
     
     const data = {
         status :status,
         oid:oid
     };
     
     const jsonData = JSON.stringify(data);
     
     const response = await fetch("ChangeOrderItemStatus",{
         method: "PUT",
         headers:{
             "Content-Type":"application/json"
         },
         body:jsonData
         
     });
     
     if(response.ok){
         const json = await response.json();
         
         if(json.status){
              
             popup.success({
                 message:json.message
             });
             loadAdminData();
         }else{
             popup.error({
                 message:json.message
             });
         }
         
         
     }else{
         popup.error({
                 message:"Something went wrong. Please try again later"
             });
     }
     
     
}

