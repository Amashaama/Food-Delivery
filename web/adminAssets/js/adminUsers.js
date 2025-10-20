async function loadUsers() {
    const popup = new Notification();

    const response = await fetch("LoadAdminData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
             document.getElementById("today").innerHTML = json.today;
            const tableContainer = document.getElementById("tbody");
            tableContainer.innerHTML = "";

            json.userList.forEach(item => {

                let tableRow = `
                
                <tr>
                                                <td>${item.id}</td>
                                                <td>${item.first_name} ${item.last_name}</td>
                                                <td>${item.email}</td>
                                                <td>${item.password}</td>
                                                <td>${item.verification}</td>
                                                <td>${item.created_at}</td>
                                                <td>${item.mobile}</td>
                                                <td>${item.userType.name}</td>
                                            </tr>
`;
                tableContainer.innerHTML += tableRow;
            });


        }
    } else {
        if (json.message === "1") {

            window.location = "signIn.html";

        } else {
            popup.error({
                message: json.message
            });

        }
    }

}

