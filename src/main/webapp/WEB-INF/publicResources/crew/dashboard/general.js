function resizeContainer() {
    let container =
        document.getElementById("welcomeContainer");
    container.style.height = "30%";
}

let eventMap = new Map();

fetch("/api/event/getEnrolled", {
    method: "GET",
    headers: {
        "Content-Type": "application/json"
    }
}).then(response => {
    let parsedBody = response.json();
    parsedBody.forEach(event => {
        const eventDate = new Date(event.start);
        eventMap.set((eventDate.getDate() + "-" + eventDate.getMonth() + "-" + eventDate.getFullYear()), event);
    });

});

if (isFromLogin()) {
    const nameContainer = document.getElementsByClassName("welcomeMessageName");
    nameContainer[0].textContent = "Welcome, " + window.sessionStorage.getItem("forename") || "user" + "!";

    const eventMessageContainer = document.getElementsByClassName("welcomeMessageEvents");
    const eventCount = window.sessionStorage.getItem("eventCount");

    if (eventCount === undefined) {
        eventMessageContainer[0].textContent = "When I have a camera in my hand, I know no fear.";
        let br = document.createElement("br");
        let i = document.createElement("i");
        i.style.fontSize = "0.8em";
        // make sure the author is aligned to the right
        i.style.float = "right";
        i.textContent = "- Alfred Eisenstaed";
        eventMessageContainer[0].appendChild(br);
        eventMessageContainer[0].appendChild(i);
    } else {
        eventMessageContainer[0].textContent = "You have " + (eventCount || "no") + " event" + (eventCount === "1" ? "" : "s") + " today.";
    }
}