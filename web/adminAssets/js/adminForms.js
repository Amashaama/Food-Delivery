async function loadCategory() {
    const popup = new Notification();
    const response = await fetch("AdminForms");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            const selector = document.getElementById("parentCategory");
            selector.innerHTML = "";
            let defaultOption = document.createElement("option");
            defaultOption.value = "0";
            defaultOption.innerHTML = "Select a category";
            selector.appendChild(defaultOption);
            json.categoryList.forEach(cat => {
                let option = document.createElement("option");
                option.value = cat.id;
                option.innerHTML = cat.name;
                selector.appendChild(option);
            });

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

}

async function addData1() {
    const popup = new Notification();
    const category = document.getElementById("categoryName").value;
   

    const data = {
        category: category
        
    };

    const dataJson = JSON.stringify(data);

    const response = await fetch("AdminForms",
            {
                method: "POST",
                body: dataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }

    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadCategory();


            popup.success({
                message: json.message
            });

            document.getElementById("categoryName").value = "";
           



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

}

async function addData2() {
    const popup = new Notification();
    
    const categorySelect = document.getElementById("parentCategory").value;
    const subCategory = document.getElementById("subcategoryName").value;
   

    const data = {
      
        categorySelect: categorySelect,
        subCategory: subCategory
        
    };

    const dataJson = JSON.stringify(data);

    const response = await fetch("AdminForms2",
            {
                method: "POST",
                body: dataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }

    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            popup.success({
                message: json.message
            });

           
            document.getElementById("subcategoryName").value = "";
            
            document.getElementById("parentCategory").selectedIndex = 0;



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
}

async function addData3() {
    const popup = new Notification();
    
    const city = document.getElementById("cityName").value;
    

    const data = {
        
        city: city
       
    };

    const dataJson = JSON.stringify(data);

    const response = await fetch("AdminForms3",
            {
                method: "POST",
                body: dataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }

    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadCategory();


            popup.success({
                message: json.message
            });

           
            document.getElementById("cityName").value = "";
           



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

}

async function addData4() {
    const popup = new Notification();
    
    const portionName = document.getElementById("portionName").value;
   

    const data = {
      
        portionName: portionName
    };

    const dataJson = JSON.stringify(data);

    const response = await fetch("AdminForms4",
            {
                method: "POST",
                body: dataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }

    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadCategory();


            popup.success({
                message: json.message
            });

           document.getElementById("portionName").value = "";
          



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

}

async function addData5() {
    const popup = new Notification();
    
    const spiceLevelName = document.getElementById("spiceLevelName").value;
    

    const data = {
       
        spiceLevelName: spiceLevelName,
       
    };

    const dataJson = JSON.stringify(data);

    const response = await fetch("AdminForms5",
            {
                method: "POST",
                body: dataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }

    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadCategory();


            popup.success({
                message: json.message
            });

            document.getElementById("spiceLevelName").value = "";
          
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

}

async function addData6() {
    const popup = new Notification();
   
    const qualityName = document.getElementById("qualityName").value;

    const data = {
        
        qualityName: qualityName
    };

    const dataJson = JSON.stringify(data);

    const response = await fetch("AdminForms6",
            {
                method: "POST",
                body: dataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }

    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadCategory();


            popup.success({
                message: json.message
            });

           document.getElementById("qualityName").value = "";
           
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

}

