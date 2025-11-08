import { changeThemeForLoggedInUser, changeThemeForNotLoggedInUser } from "./theme.js";

let loggedIn = false;
let userInfo = null;

async function checkLogin() {
  try {
    const response = await fetch("/api/auth/me", { credentials: "include" });

    if (response.ok) {
      userInfo = await response.json();
      changeThemeForLoggedInUser(userInfo)
      loggedIn = true;
    } else {
      throw new Error("not logged in");
    }
  } catch (err) {
    changeThemeForNotLoggedInUser();
    loggedIn = false;
  }
}

checkLogin();

export function isLoggedIn() {
   if(loggedIn) return userInfo;
   return loggedIn;
}

