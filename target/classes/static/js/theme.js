
export function changeThemeForNotLoggedInUser(){
    let themeType = localStorage.getItem("theme");
    if(themeType == null){
        localStorage.setItem("theme", "dark");
        return;
    }
    if(themeType == "system"){
        themeType = getSystemTheme();
    }
    document.body.dataset.theme = themeType;

}

export function changeThemeForLoggedInUser(user){
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


function getSystemTheme(){
    if(window.matchMedia("(prefers-color-scheme : light)").matches){
        return "light";
    }
    return "dark";
}