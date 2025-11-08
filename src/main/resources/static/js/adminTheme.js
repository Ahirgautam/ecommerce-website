

function changeThemeForLoggedInUser(user){
    const theme = user.themePreference.toLowerCase();

    document.querySelector(".apperance-option.active")?.classList.remove("active");

    if(theme == "system"){

        document.body.dataset.theme = getSystemTheme();
        document.querySelector(".apperance-option[data-type=system]")?.classList.add("active");
    }
    else{

        document.body.dataset.theme = theme;
        document.querySelector(`.apperance-option[data-type=${document.body.dataset.theme}]`)?.classList.add("active");
    }
}