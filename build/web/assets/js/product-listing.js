var subCatList;
async function loadProductData() {

    const response = await fetch("LoadProductData");

    if (response.ok) {
        const json = await response.json();

        //console.log(json);
        if (json.status) {

            loadSelect("category", json.categoryList, "name");
            loadSelect("portionSize", json.portionSizeList, "name");
            loadSelect("spiceLevel", json.spiceLevelList, "name");
            //loadSelect("productStatus",json.,"");
            loadSelect("quality", json.qualityList, "value");
            // loadSelect("subCategory",json.subCategoryList,"name");
            subCatList = json.subCategoryList;

        }

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

function loadSubcategory() {

    const catId = document.getElementById("category").value;
    const subCatSelect = document.getElementById("subCategory");
    subCatSelect.length = 1;

    subCatList.forEach(item => {

        if (item.category.id == catId) {
            const option = document.createElement("option");
            option.value = item.id;
            option.innerHTML = item.name;
            subCatSelect.appendChild(option);
        }

    });

}

async function saveProuct() {

    const categoryId = document.getElementById("category").value;
    const subCategoryId = document.getElementById("subCategory").value;
    const title = document.getElementById("productTitle").value;
    const description = document.getElementById("description").value;
    const portionSizeId = document.getElementById("portionSize").value;
    const spiceLevelId = document.getElementById("spiceLevel").value;
    const qualityId = document.getElementById("quality").value;
    const price = document.getElementById("price").value;
    const qty = document.getElementById("qty").value;

    const image1 = document.getElementById("img1").files[0];
    const image2 = document.getElementById("img2").files[0];
    const image3 = document.getElementById("img3").files[0];


    const form = new FormData();
    form.append("categoryId", categoryId);
    form.append("subCategoryId", subCategoryId);
    form.append("title", title);
    form.append("description", description);
    form.append("portionSizeId", portionSizeId);
    form.append("spiceLevelId", spiceLevelId);
    form.append("qualityId", qualityId);
    form.append("price", price);
    form.append("qty", qty);
    form.append("image1", image1);
    form.append("image2", image2);
    form.append("image3", image3);

    const response = await fetch(
            "SaveProduct",
            {
                method: "POST",
                body: form
            }

    );

    const popup = Notification();

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json.status);
            popup.success(
                    {
                        message: "New Product added successfully"
                    }
            );


            document.getElementById("category").value = "";
            document.getElementById("subCategory").value = "";
            document.getElementById("productTitle").value = "";
            document.getElementById("description").value = "";
            document.getElementById("portionSize").value = "";
            document.getElementById("spiceLevel").value = "";
            document.getElementById("quality").value = "";
            document.getElementById("price").value = "";
            document.getElementById("qty").value = "";
            document.getElementById("img1").value = "";
            document.getElementById("img2").value = "";
            document.getElementById("img3").value = "";





        } else {

            if (json.message === "Please sign In") {
                window.location = "signIn.html";
            } else {
                popup.error(
                        {
                            message: json.message
                        }
                );
            }

        }


    } else {

    }





}