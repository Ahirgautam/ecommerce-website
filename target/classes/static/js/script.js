import { isLoggedIn } from "./auth.js";
import { displayItemCountInCart } from "./addToCart.js";




document.querySelector(".apperance-option-container")?.addEventListener("click", function(e){
    if(e.target.classList.contains("apperance-option")){
        let type = e.target.dataset.type;
        fetch("/api/users/theme", {
            method:"POST",
            headers: {
                "Content-Type" : "application/json",
                "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').content
            },
            body:JSON.stringify({"theme":type})
        })
        .then(re => {
            if(!re.ok){
                throw new Error();
            }
            return re.text();
        })
        .then(re => {
            if(type == "system"){
                document.body.dataset.theme = getSystemTheme();
            }
            else{
                document.body.dataset.theme = type;
            }
            localStorage.setItem("theme", type);
            document.querySelector(".apperance-option.active")?.classList.remove("active");
            e.target.classList.add("active");
        })
        .catch(err => {
            console.log(err);
            showAlert("Unable to change theme");
        })


    }

});


function getSystemTheme(){
    if(window.matchMedia("(prefers-color-scheme : light)").matches){
        return "light";
    }
    return "dark";
}

const hamburgerBtn = document.getElementById("hamburger-btn");

hamburgerBtn.addEventListener("click", (event)=>{
    const navbarMobile = document.getElementsByClassName("navbar-mobile")[0];
    const img = document.getElementsByClassName("hamburger-img")[0];
    if(navbarMobile.classList.contains("hide")){
        img.src = "/images/icons/close.svg";
        img.alt = "close hamburger menu";
        hamburgerBtn.setAttribute("aria-expanded", true)
    }
    else{
        img.src = "/images/icons/hamburger-menu_svgrepo.com.svg";
        img.alt = "hamburger menu";
        hamburgerBtn.setAttribute("aria-expanded", false)
    }
    document.body.classList.toggle("no-scroll")
    navbarMobile.classList.toggle("hide");

});

const createAccountBtn = document.querySelector(".create-account");

function toggleCreateAccountBtn(bool){
    createAccountBtn.disabled = bool;
}
function showLoginForm(event){
    document.querySelector(".register").classList.add("hide");
    document.querySelector(".otp-verification").classList.add("hide");
    document.querySelector(".login").classList.remove("hide");
    toggleCreateAccountBtn(false);
}
function showRegisterForm(event){
    document.querySelector(".register").classList.remove("hide");
    document.querySelector(".login").classList.add("hide");
    document.querySelector(".otp-verification").classList.add("hide");
    toggleCreateAccountBtn(false);

}
function showOtpForm(email){
    document.querySelector(".register").classList.add("hide");
    document.querySelector(".otp-verification").classList.remove("hide");
    document.querySelector(".user-email").textContent = email;
}
function toggleLoginRegisterModel(){
    if(isLoggedIn()){
        let user = isLoggedIn();
        console.log(user)
    }
    document.querySelector(".login-register-container").classList.toggle("hide");
    document.body.classList.toggle("no-scroll");
}

const forms = [...document.getElementsByClassName("form")];
let userEmail = "";
forms.forEach((form, i) => {
    form.addEventListener("submit", (event) => {
        let isEmailValid = true;
        let isPasswordValid = true;

        const errorMessageEmail = form.querySelector(".error-message-email");
        const errorMessagePass = form.querySelector(".error-message-password");

        const emailInput = form.email;
        const passwordInput = form.password;

        const email = emailInput.value.trim();
        const password = passwordInput.value;


        // validate password
        if (password === "") {
            errorMessagePass.textContent = "Please Enter Password";
            errorMessagePass.classList.remove("hide");
            passwordInput.classList.add("invalid-input");
            passwordInput.focus();
            isPasswordValid = false;
        }
        else {
            const requirements = [
                { regex: /\d/, message: "At least one digit" },
                { regex: /[a-zA-Z]/, message: "At least one letter" },
                { regex: /.{8,}/, message: "Minimum 8 characters" },
                { regex: /\W/, message: "At least one special symbol" }
            ];

            const errors = requirements.filter(r => !r.regex.test(password));

            if (errors.length === 0) {
                errorMessagePass.classList.add("hide");
                passwordInput.classList.remove("invalid-input");
            }
            else {
                const ul = document.createElement("ul");
                errors.forEach(err => {
                    const li = document.createElement("li");
                    li.textContent = err.message;
                    ul.appendChild(li);
                });
                errorMessagePass.innerHTML = "";
                errorMessagePass.appendChild(ul);
                errorMessagePass.classList.remove("hide");
                passwordInput.classList.add("invalid-input");
                isPasswordValid = false;
            }
        }

        // validate emali
        if (email === "") {
            errorMessageEmail.textContent = "Please Enter Email";
            errorMessageEmail.classList.remove("hide");
            emailInput.classList.add("invalid-input");
            emailInput.focus();
            isEmailValid = false;
        }
        else {
            errorMessageEmail.classList.add("hide");
            emailInput.classList.remove("invalid-input");
        }

        if (!isEmailValid || !isPasswordValid) {
            event.preventDefault();
        }
        else{
            if(i == 1){
                toggleCreateAccountBtn(true);
                const form = event.target;
                const formData = new FormData(form);
                userEmail = email;
                fetch("/send-otp", {
                    method: "POST",
                    headers: {
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    },
                    body: formData
                })
                .then(response =>{
                     if(!response.ok ){
                        if(response.status == 409){
                            showAlert("User With Same Email Exists!","error");
                            toggleCreateAccountBtn(false);
                        }
                        throw new Error('Something went wrong!. Please Try Again');
                     }
                     return response.text()
                })
                .then(text => {

                    showAlert(text);
                    showOtpForm(form.email.value);
                })
                .catch(error => {
                    console.error(error);
                    toggleCreateAccountBtn(false);
                    showAlert(error.message, "error");
                });
                event.preventDefault();

            }
            else{
                document.querySelector(".login-btn").disabled=true;
            }


        }
    });
});
const otpVerificationInput = document.querySelector(".otp-verification-input");
otpVerificationInput.addEventListener("submit", function(e){
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);
    formData.set("email", userEmail);
    fetch("/validate-otp", {
        method: "POST",
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        },
        body: formData
    })
    .then(response => {
        if(!response.ok){
            throw new Error("Invalid otp");
        }
        response.text()
    })
    .then(data => {
        showOtpForm(userEmail);
        showAlert("OTP Verified. Now You Can Login");
        showLoginForm();
    })
    .catch(error => {
        showAlert("Network error: " + error.message, "error");
    });

});


function logout() {
    fetch('/logout', {
      method: 'POST',
      headers: {
        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
      }
    }).then(() => {
      window.location.href = '/?logout=true';
    })
    .catch(error => {
        showAlert("logout failed")
    })
}
window.addEventListener("DOMContentLoaded", function(){
    const param = new URLSearchParams(window.location.search);


    displayItemCountInCart();
    if(param.get("error") == "true"){
        toggleLoginRegisterModel();
        showAlert("Invalid UserName Password !","error");
        removeAttributeFromUrl();
    }
    else if(param.get("login") === "true"){
        showAlert("login successful");
        removeAttributeFromUrl();
    }
    else if(param.get("logout") === "true"){
        console.log("joo");
        showAlert("logout successful");
        removeAttributeFromUrl();
    }
});

document.querySelector(".header-login-btn")?.addEventListener("click", toggleLoginRegisterModel);
document.querySelector(".login-close-btn").addEventListener("click", toggleLoginRegisterModel);
document.querySelectorAll(".show-register-btn").forEach(ele => ele.addEventListener("click", showRegisterForm));
document.querySelector(".register-close-btn").addEventListener("click", toggleLoginRegisterModel);
document.querySelector(".goto-login-btn").addEventListener("click", showLoginForm);
document.querySelector(".close-otp-btn").addEventListener("click", toggleLoginRegisterModel);
function removeAttributeFromUrl(){
    const newUrl = window.location.origin + window.location.pathname;
    history.replaceState({}, document.title, newUrl);
}


const userAccountOpener = document.querySelector(".user-account-opener");
const loginRegisterContainer = document.querySelector(".login-register-container");
window.addEventListener("click", function (e) {

    if (
        userAccountOpener?.open &&
        !userAccountOpener.contains(e.target)
    ) {
        userAccountOpener.open = false;
    }

    if (e.target.classList.contains("login-register-container")) {
        toggleLoginRegisterModel();
    }
});


