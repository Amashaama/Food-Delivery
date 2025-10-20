async function verify(event) {
  if (event) event.preventDefault();

  const code =
    document.getElementById("code1").value +
    document.getElementById("code2").value +
    document.getElementById("code3").value +
    document.getElementById("code4").value +
    document.getElementById("code5").value +
    document.getElementById("code6").value;

  const verification = {
    verificationCode: code
  };

  const verificationJson = JSON.stringify(verification);

  const response = await fetch(
    "VerifyAccount",
    {
      method: "POST",
       credentials: "include",
      body: verificationJson,
      headers: {
        "Content-Type": "application/json"
      }
    }
  );

  if (response.ok) {
    const json = await response.json();

    if (json.status) {
      window.location = "index.html";
    } else {
      if (json.message === "Email not found") {
        window.location = "signIn.html";
      } else {
        const errorBox = document.getElementById("errorMsg");
        errorBox.classList.remove("d-none");
        errorBox.className = "alert alert-danger fw-bold text-center fs-8";
        errorBox.innerHTML = json.message;
      }
    }
  } else {
    const errorBox = document.getElementById("errorMsg");
    errorBox.classList.remove("d-none");
    errorBox.className = "alert alert-danger fw-bold text-center fs-8";
    errorBox.innerHTML = "Verification Failed. Please Try Again";
  }
}
