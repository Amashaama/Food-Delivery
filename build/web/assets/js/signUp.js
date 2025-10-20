async function signUp(event) {
 
  if (event) event.preventDefault();

 
  const firstName = document.getElementById("firstName").value.trim();
  const lastName = document.getElementById("lastName").value.trim();
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value;
   const mobile = document.getElementById("mobile").value;

  
  const errorBox = document.getElementById("errorMsg");
  const submitBtn = document.querySelector("button.btn-primary");

  submitBtn.disabled = true;
  submitBtn.innerHTML = "Please wait...";

  const user = {
    firstName: firstName,
    lastName: lastName,
    email: email,
    password: password,
    mobile: mobile
  };

  try {
    const response = await fetch("SignUp", {
      method: "POST",
      

      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(user)
    });

   

    if (response.ok) {
      const json = await response.json();

      if (json.status) {
        // errorBox.classList.remove("d-none");
       // errorBox.className = "alert alert-success fw-bold text-center fs-8";
       //  errorBox.innerHTML = json.message;
        window.location="verification.html";
      } else {
         errorBox.classList.remove("d-none");
        errorBox.className = "alert alert-danger fw-bold text-center fs-8";
         errorBox.innerHTML = json.message;
      }

     

    } else {
       errorBox.classList.remove("d-none");
      errorBox.className = "alert alert-danger fw-bold text-center fs-8";
      errorBox.innerHTML = "Registration Failed. Please Try Again";
    }

  } catch (error) {
    errorBox.classList.remove("d-none");
    errorBox.className = "alert alert-danger fw-bold text-center fs-8";
    errorBox.innerHTML = "An error occurred. Please try again later.";
    console.error("Signup error:", error);
  }


  submitBtn.disabled = false;
  submitBtn.innerHTML = '<i class="fas fa-user-plus me-2"></i>Create Account';
}
