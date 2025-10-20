async function signIn(event) {
    if (event)
        event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const signIn = {
        email: email,
        password: password
    };

    const signInJson = JSON.stringify(signIn);

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: signInJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );



    if (response.ok) {

        const json = await response.json();
        if (json.status) {
            if (json.message === "1") {

                window.location = "verification.html";

            } else if (json.message === "admin") {
                window.location = "adminPanel.html";
            } else {
                window.location = "index.html";
            }
        } else {
            const errorBox = document.getElementById("errorMsg");
            errorBox.classList.remove("d-none");
            errorBox.className = "alert alert-danger fw-bold text-center fs-8";
            errorBox.innerHTML = json.message;
        }

    } else {
        const errorBox = document.getElementById("errorMsg");
        errorBox.classList.remove("d-none");
        errorBox.className = "alert alert-danger fw-bold text-center fs-8";
        errorBox.innerHTML = "SignIn Failed. Please Try Again";

    }



}