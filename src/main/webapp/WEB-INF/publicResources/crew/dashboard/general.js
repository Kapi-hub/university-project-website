function filterEvents() {
    $("#applyFilterButton").on("click", function() {
        // Get the filter values from the text input fields
        const clientFilterValue = $("#clientFilter").val();
        const monthFilterValue = parseInt($(".dropdown-item.active").data("month"));

        // Parse the filter values and provide default values of 0 if empty
        const client = clientFilterValue !== "" ? parseInt(clientFilterValue) : 0;
        const month = isNaN(monthFilterValue) ? 0 : monthFilterValue;

        // Call fetchEvents with the filter values
        fetchEvents(client, month);
    });

    // Add click event listener to month filter dropdown items
    $(".dropdown-item").on("click", function() {
        // Remove active class from previous active item and add it to the clicked item
        $(".dropdown-item.active").removeClass("active");
        $(this).addClass("active");

        // Update the dropdown button text with the selected month
        const selectedMonth = $(this).text();
        $("#monthFilterDropdown").text(selectedMonth);
    });
}

filterEvents();

function resizeContainer() {
    let container =
        document.getElementById("welcomeContainer");
    container.style.height = "30%";
}

let eventMap = new Map();

function fetchEvents(client, month) {
    const params = new URLSearchParams();
    params.set("client", client);
    params.set("month", month);

    const url = `/api/event/getEnrolled?${params.toString()}`;
    fetch(url, {
        method: "GET",
        headers: {
            "Accept": "application/json"
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

fetchEvents(0,0);

if (isFromLogin()) {
    const nameContainer = document.getElementsByClassName("welcomeMessageName");
    nameContainer[0].textContent = "Welcome, " + (window.sessionStorage.getItem("forename") || "user") + "!";

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
        eventMessageContainer[0].textContent = "You have " + (eventCount === '0' ? "no" : eventCount) + " event" + (eventCount === "1" ? "" : "s") + " today.";
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
            let title = document.createElement("h4");
            title.classList.add("event-title");
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
        accordion.innerHTML = "<h4 class='event-title'>You have no past events yet.</h4>";
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
            let b = document.createElement("b");
            b.textContent = event.name;
            let i = document.createElement("i");
            i.textContent = getFormattedDate(event.date);
            button.appendChild(b);
            button.innerHTML += "&ensp;-&ensp;";
            button.appendChild(i);
            accordionHeader.appendChild(button);
            accordionItem.appendChild(accordionHeader);
            let accordionCollapse = document.createElement("div");
            accordionCollapse.className = "accordion-collapse collapse";
            accordionCollapse.id = "collapse" + event.id;
            accordionCollapse.setAttribute("data-bs-parent", "#pastEvents");
            let accordionBody = getHtmlElement(event);
            accordionBody.classList.add("accordion-body", "eventBody");
            accordionCollapse.appendChild(accordionBody);
            accordionItem.appendChild(accordionCollapse);
            accordion.appendChild(accordionItem);
        });
        addHover()
    }
}


let announcementList = [];
let title = document.getElementsByClassName("tab_content tab_anno container-fluid")[0].children[0];
let container = document.getElementById("announcements")
function fetchAnnouncements() {
    fetch("/api/announcement/forCrew", {
        method: "GET",
        headers: {
            "Accept": "application/json",
        }
    }).then(response => {
        if (response.status === 200) {
            console.log("Announcements fetched successfully.")
            let data = response.json();
            data.then(parsedData => {
                parsedData.forEach(announcement => {
                    announcementList.push(announcement);
                });
                loadAnnouncements();
            });
        } else {
            if (response.status !== 204) {
                console.log("Error while fetching announcements: " + response.status);
            }
            title.textContent = "You have no announcements yet.";
            let subtitle = document.createElement("h5");
            subtitle.textContent = "Once you have received some announcements, you can view them here.";
            container.appendChild(subtitle);
        }
    });

}

function loadAnnouncements() {
    title.innerHTML = "";
    title.textContent = "You have " + numberToString(announcementList.length) + " announcement" + (announcementList.length === 1 ? "" : "s") + ".";
    announcementList.forEach(announcement => {
        let accordionItem = document.createElement("div");
        accordionItem.className = "accordion-item";
        let accordionHeader = document.createElement("h2");
        accordionHeader.className = "accordion-header";
        let button = document.createElement("button");
        button.className = "accordion-button collapsed";
        if (announcement.urgent) {
            button.classList.add("urgent")
            button.innerHTML = "URGENT:&ensp;";
        }
        button.type = "button";
        button.setAttribute("data-bs-toggle", "collapse");
        button.setAttribute("data-bs-target", "#collapse" + announcement.id);
        button.setAttribute("aria-expanded", "false");
        button.setAttribute("aria-controls", "collapse" + announcement.id);
        let b = document.createElement("b");
        b.textContent = announcement.title;
        let i = document.createElement("i");
        i.textContent = announcement.announcer;
        button.appendChild(b);
        button.innerHTML += "&ensp;-&ensp;";
        button.appendChild(i);
        if (announcement.isPersonal) {
            let personalTag = document.createElement("span");
            personalTag.classList.add("personalTag")
            personalTag.textContent = "Personal Message";
            button.appendChild(personalTag);
        }
        let date = document.createElement("span");
        date.textContent = getFormattedDate(announcement.dateTime) + " " + getFormattedTime(announcement.dateTime);
        date.classList.add("announcement-date-span")
        button.appendChild(date);
        accordionHeader.appendChild(button);
        accordionItem.appendChild(accordionHeader);
        let accordionCollapse = document.createElement("div");
        accordionCollapse.className = "accordion-collapse collapse";
        accordionCollapse.id = "collapse" + announcement.id;
        accordionCollapse.setAttribute("data-bs-parent", "#announcements");
        let accordionBody = document.createElement("div");
        accordionBody.className = "accordion-body";
        accordionBody.textContent = announcement.body;
        accordionCollapse.appendChild(accordionBody);
        accordionItem.appendChild(accordionCollapse);
        container.appendChild(accordionItem);
    })
}
fetchAnnouncements();



function fetchHoursWorked() {
    fetch("/api/event/getHoursWorked", {
        headers: {
            "Accept": "application/json"
        }
    }).then(response => {
        if (!response.status === 200) {
            throw new Error("HTTP error " + response.status)
        }
        console.log(response.body)
        return response.json();
    }).then(parsedData => {
        let hoursWorkedPerMonth = new Map();
        parsedData.forEach(month => {
            hoursWorkedPerMonth.set(month[0], month[1]);
        })
        createHoursWorkedGraph(hoursWorkedPerMonth);
    }).catch(error => {
        console.error("Error retrieving data for statistics: " + error);
    });
}

function createHoursWorkedGraph(hoursWorkedPerMonth) {
    const ctx = document.getElementById('hoursWorked').getContext('2d');
    const myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Array.from(hoursWorkedPerMonth.keys()),
            datasets: [{
                label: 'Amount of hours worked',
                data: Array.from(hoursWorkedPerMonth.values()),
                backgroundColor: 'rgba(22, 150, 210, 0.1)',
                borderColor: 'rgba(22, 150, 210)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

fetchHoursWorked();