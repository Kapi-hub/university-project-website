function resizeContainer() {
    let container =
        document.getElementById("welcomeContainer");
    container.style.height = "30%";
}

let eventMap = new Map();

function fetchEvents() {
    fetch("/api/event/getEnrolled", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    }).then(response => {
        if (response.status === 200) {
            let parsedBody = response.json();
            eventMap = new Map();
            parsedBody.then(parsedBody => {
                parsedBody.forEach(event => {
                    const eventTimeType = (new Date(event.date).getTime() + (event.duration * 60 * 60 * 1000) < new Date().getTime()) ? "past" : "future";
                    let events = eventMap.get(eventTimeType);
                    if (events === undefined) {
                        events = [];
                    }
                    events.push(event);
                    eventMap.set(eventTimeType, events);
                });
                loadCurrentEvents();
                loadPastEvents();
            });
        } else if(response.status === 204){
            loadCurrentEvents();
            loadPastEvents();
        } else{
            console.error("Error fetching events");
        }
    });
}

fetchEvents();

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

function loadCurrentEvents() {
    let header = document.getElementsByClassName("tab_content tab_current container-fluid")[0].children[0];
    let carousel = document.getElementById("carouselContainer");
    let container = carousel.children[0];
    container.innerHTML = "";
    let eventsFuture = eventMap.get("future");
    console.log(eventsFuture);
    if (eventsFuture === undefined) {
        header.textContent = "You are not currently enrolled into any events.";
        carousel.children[1].style.display = "none";
        carousel.children[2].style.display = "none";
        container.innerHTML = "<div class='carousel-item active'><button class='btn btn-primary' onclick='window.location.href=\"/crew/calendar\"'>Enrol into one!</button></div>";
    } else {
        if (eventsFuture.length === 1) {
            carousel.children[1].style.display = "none";
            carousel.children[2].style.display = "none";
        }
        header.textContent = "You are enrolled into " + numberToString(eventsFuture.length) + " upcoming event" + (eventsFuture.length === 1 ? "" : "s") + ".";
        eventsFuture.forEach(event => {
            let carouselItem = document.createElement("div");
            carouselItem.className = "carousel-item";
            let titleSpan = document.createElement("span");
            titleSpan.classList.add("titleSpan");
            let title = document.createElement("h3");
            let b = document.createElement("b");
            let i = document.createElement("i");
            b.textContent = event.name + " - ";
            i.textContent = getFormattedDate(event.date);
            title.appendChild(b);
            title.appendChild(i);
            titleSpan.appendChild(title);
            carouselItem.appendChild(titleSpan);
            let eventBody = getHtmlElement(event);
            eventBody.classList.add("eventBody");
            carouselItem.appendChild(eventBody);
            container.appendChild(carouselItem);
        });
        container.children[0].classList.add("active");
        addHover();
    }
}

function loadPastEvents() {
    let header = document.getElementsByClassName("tab_content tab_past container-fluid")[0].children[0];
    let accordion = document.getElementById("pastEvents");

    let eventsPast = eventMap.get("past");
    if (eventsPast === undefined) {
        accordion.innerHTML = "<h3>You have no past events yet.</h3>";
        let subtitle = document.createElement("h5");
        subtitle.textContent = "Once you have participated in some events, you can view them here.";
        accordion.appendChild(subtitle);
    } else {
        accordion.innerHTML = "";
        header.textContent = "You have " + numberToString(eventsPast.length) + " past event" + (eventsPast.length === 1 ? "" : "s") + ".";
        eventsPast.forEach(event => {
            let accordionItem = document.createElement("div");
            accordionItem.className = "accordion-item";
            let accordionHeader = document.createElement("h2");
            accordionHeader.className = "accordion-header";
            let button = document.createElement("button");
            button.className = "accordion-button collapsed";
            button.type = "button";
            button.setAttribute("data-bs-toggle", "collapse");
            button.setAttribute("data-bs-target", "#collapse" + event.id);
            button.setAttribute("aria-expanded", "false");
            button.setAttribute("aria-controls", "collapse" + event.id);
            button.textContent = event.name;
            accordionHeader.appendChild(button);
            accordionItem.appendChild(accordionHeader);
            let accordionCollapse = document.createElement("div");
            accordionCollapse.className = "accordion-collapse collapse";
            accordionCollapse.id = "collapse" + event.id;
            accordionCollapse.setAttribute("data-bs-parent", "#pastEvents");
            let accordionBody = document.createElement("div");
            accordionBody.className = "accordion-body";
            let eventBody = getHtmlElement(event);
            eventBody.classList.add("eventBody");
            accordionBody.appendChild(eventBody);
            accordionCollapse.appendChild(accordionBody);
            accordionItem.appendChild(accordionCollapse);
            accordion.appendChild(accordionItem);
        });
    }
//     <div className="accordion-item">
//         <h2 className="accordion-header">
//             <button aria-controls="collapseOnePast" aria-expanded="false"
//                     className="accordion-button collapsed"
//                     data-bs-target="#collapseOnePast" data-bs-toggle="collapse" type="button">
//                 Past event #1
//             </button>
//         </h2>
//         <div className="accordion-collapse collapse" data-bs-parent="#pastEvents" id="collapseOnePast">
//             <div className="accordion-body">
//                 <div className="halfOne">
//                     Name: <br>
//                     Type: <br>
//                     Date: <br>
//                     Location: <br>
//                     Duration: <br>
//                 </div>
//                 <div className="halfTwo">
//                     Client: <br>
//                     Booking Type: <br>
//                     Product Manager: <br>
//                     Crew: <br>
//                     Status: <br>
//                     Description:
//                 </div>
//             </div>
//         </div>
//     </div>
}