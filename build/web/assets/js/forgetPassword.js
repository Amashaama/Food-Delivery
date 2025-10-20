async function forgotPassword() {

 const email = document.getElementById("email").value.trim();
    const errorBox = document.getElementById("errorMsg");

    const data = {
        email: email
    };


    const datajson = JSON.stringify(data);

    const response = await fetch("ForgetPassword", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: datajson
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            window.location = "forgetPassword.html";

        } else {
            errorBox.classList.remove("d-none");
            errorBox.className = "alert alert-danger fw-bold text-center fs-8";
            errorBox.innerHTML = json.message;
        }
    } else {
        errorBox.classList.remove("d-none");
        errorBox.className = "alert alert-danger fw-bold text-center fs-8";
        errorBox.innerHTML = "Request failed. Please try again.";
    }

}

async function resetPassword() {
    const code = document.getElementById("verificationCode").value.trim();
    const newPassword = document.getElementById("newPassword").value;
    const errorBox = document.getElementById("errorMsg");

    const data = {
        code: code,
        newPassword: newPassword
    };

    const jsonData = JSON.stringify(data);

    const response = await fetch("ResetPassword", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: jsonData
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location = "signIn.html";
        } else {
            errorBox.classList.remove("d-none");
            errorBox.className = "alert alert-danger fw-bold text-center fs-8";
            errorBox.innerHTML = json.message;
        }
    } else {
        errorBox.classList.remove("d-none");
        errorBox.className = "alert alert-danger fw-bold text-center fs-8";
        errorBox.innerHTML = "Password reset failed. Please try again.";
    }

}
