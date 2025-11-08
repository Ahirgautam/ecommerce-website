function showAlert(message, type = "success"){
    const customAlertContainer = document.querySelector(".custom-alert-container");
    customAlertContainer.classList.remove("custom-alert-hide");

    const newCustomAlert = document.querySelector(".custom-alert").cloneNode(true);
    const alertMessage = newCustomAlert.querySelector(".alert-message");
    alertMessage.textContent = message;
    newCustomAlert.classList.add(type);
    newCustomAlert.classList.remove("hide");
    newCustomAlert.querySelector(".close-alert").addEventListener("click", function(){
        closeAlert(type, newCustomAlert, customAlertContainer);
    });
    customAlertContainer.append(newCustomAlert);
    setTimeout(function(){
        closeAlert(type, newCustomAlert, customAlertContainer);
    }, 3000);
}
function closeAlert(type = "success", newCustomAlert, customAlertContainer){
//    customAlertContainer.innerHTML == "";
    customAlertContainer.classList.add("custom-alert-hide");
    newCustomAlert.classList.add("move-up");
    newCustomAlert.addEventListener("animationend", function(){
        newCustomAlert.classList.remove(type);
        newCustomAlert.remove();
    });
}