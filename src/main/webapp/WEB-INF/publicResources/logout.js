function logout() {
    const XHR = new XMLHttpRequest();
    XHR.open("POST", "/api/login/logout");
    XHR.addEventListener("load", function () {
        if (this.status === 200) {
            document.cookie = "sessionId=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
            document.cookie = "accountId=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
            window.location.href = "/login?logout=true"
        } else {
            alert("Logout failed due to internal server error, please try again.")
        }
    });
    XHR.send();
}