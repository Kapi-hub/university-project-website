const weekdaysNumberContainer = document.querySelector(".weekdaysNumber"),
    prevBtn = document.querySelector(".btn.prev"),
    nextBtn = document.querySelector(".btn.next"),
    month = document.querySelector(".month");
let monthItem = document.querySelectorAll(".monthItem");

const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

// get current date, month, year
let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();

let monthsEventMap = new Map();

let recursion = false;

// function to render the calendar for current month
function renderCalendar() {
    // get prev, current, next month days
    currentDate.setDate(1);
    // lastDay -> 0 refers to the last day of prev month, so this is why
    // currentMonth + 1 was passed as an argument. lastDay, thus, gets the last day
    // of the current month
    const firstDay = new Date(currentYear, currentMonth, 1);
    const lastDay = new Date(currentYear, currentMonth + 1, 0);
    const lastDayIndex = lastDay.getDay();
    const lastDayDate = lastDay.getDate();
    const prevLastDay = new Date(currentYear, currentMonth, 0);
    const prevLastDayDate = prevLastDay.getDate();
    const nextDays = 7 - lastDayIndex - 1;
    // console.log(prevLastDay);
    // console.log(prevLastDayDate);
    // console.log(lastDay);
    // console.log(lastDayIndex);
    // update month, year
    month.innerHTML = `${months[currentMonth]} ${currentYear}`;
    let days = "";

    // previous month days
    for (let i = firstDay.getDay() - 1; i > 0; i--) {
        days += `<div class="day prev">${prevLastDayDate - i + 1}</div>`;
    }

    // current month days
    for (let x = 1; x <= lastDayDate; x++) {
        //check if it's today then add today class
        if (x === new Date().getDate() &&
            currentMonth === new Date().getMonth() &&
            currentYear === new Date().getFullYear()) {
            // if date month year matches
            days += `<div class="day current">${x}</div>`;
        } else {
            // don't add today
            days += `<div class="day ">${x}</div>`;
        }
    }
    console.log(nextDays)
    for (let j = 1; j <= nextDays + 1; j++) {
        days += `<div class="day next">${j}</div>`;
    }
    weekdaysNumberContainer.innerHTML = days;

    // TODO: fix the bug with October month, 1 starts on Monday as of rn, but it should start on Sunday.
    // TODO: fix buttons, they are smaller then the clickable space.

    // MANDATORY to be here because the elements are loaded by renderCalendar().
    let day_items = document.querySelectorAll(".weekdaysNumber div");
    day_items.forEach(function (item) {
        item.addEventListener("click", function () {
            day_items.forEach(function (div) {
                div.classList.remove("current");
            });
            item.classList.add("day", "current");
        });
    })
}

renderCalendar();

nextBtn.addEventListener("click", () => {
    li_items[currentMonth].classList.remove("active");
    currentMonth++;
    if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
    }
    li_items[currentMonth].classList.add("active");
    renderCalendar();
})
prevBtn.addEventListener("click", () => {
    li_items[currentMonth].classList.remove("active");
    currentMonth--;
    if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
    }
    li_items[currentMonth].classList.add("active");
    renderCalendar();
})


monthItem.forEach(function (item) {
    item.addEventListener("click", () => {
        switch (event.target.id) {
            case "Jan":
                currentMonth = 0;
                break;
            case "Feb":
                currentMonth = 1;
                break;
            case "Mar":
                currentMonth = 2;
                break;
            case "Apr":
                currentMonth = 3;
                break;
            case "May":
                currentMonth = 4;
                break;
            case "Jun":
                currentMonth = 5;
                break;
            case "Jul":
                currentMonth = 6;
                break;
            case "Aug":
                currentMonth = 7;
                break;
            case "Sep":
                currentMonth = 8;
                break;
            case "Oct":
                currentMonth = 9;
                break;
            case "Nov":
                currentMonth = 10;
                break;
            case "Dec":
                currentMonth = 11;
                break;
        }
        renderCalendar();
    })
});


// /**************************************/
// /********* SIDE BAR FUNCTIONS *********/
// /**************************************/

const eventDayHandler = document.querySelector(".event-day");
const eventDateHandler = document.querySelector(".event-date");
const eventContainer = document.getElementById("event-accordion");

const javaEnumToString = (javaEnum) => javaEnum
    ? javaEnum.split('_').map(word => `${word.charAt(0).toUpperCase()}${word.slice(1).toLowerCase()}`).join(' ')
    : "Not defined.";

const getEventsForMonth = (date) => {
    console.log("request made");
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const formattedMonth = `${year}-${month}`;

    return fetch(`/api/event/getFromMonth?month=${formattedMonth}`, {
        method: 'GET',
        credentials: 'include',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok || response.status === 204) {
                throw new Error(`No events returned ${response.status}`);
            }
            return response.json();
        });
}

const formatCrewString = (crew) => {
    let crewString = "";
    crew.forEach(([crewType, crewAmount]) => {
        crewString += `${crewType}: ${crewAmount}, `;
    });
    return crewString || "All required crew is enrolled.";
}

const formatEnrolled = (enrolled) => {
    let enrolledArray = [];
    enrolled.forEach(([role, people]) => {
        const peopleString = people.join(", ");
        const detailsId = `details-${(name + role).replace(/\s/g, "")}`;
        let enrolledSpan = document.createElement("span");
        enrolledSpan.classList.add("hoverable");
        enrolledSpan.setAttribute("data-details", detailsId);
        let detailsSpan = document.createElement("span");
        detailsSpan.classList.add("details");
        detailsSpan.setAttribute("id", detailsId);
        detailsSpan.textContent = peopleString;
        let dottedUnderline = document.createElement("u");
        dottedUnderline.style.borderBottom = "1px dotted #000";
        dottedUnderline.style.textDecoration = "none";
        dottedUnderline.textContent = `${role}`;
        enrolledSpan.appendChild(dottedUnderline);
        enrolledSpan.appendChild(document.createTextNode(`: ${people.length} `))
        enrolledSpan.appendChild(detailsSpan);
        enrolledArray.push(enrolledSpan);
    });
    return enrolledArray.length === 0 ? "No one is enrolled yet." : enrolledArray;
}

const updateEvents = (date) => {
    getEventsForMonth(date)
        .then(eventList => {
            let eventsPerDay = new Map();
            eventList.forEach(event => {
                const eventDate = new Date(event.date);
                const eventDay = eventDate.getDate();
                if (eventsPerDay.has(eventDay)) {
                    eventsPerDay.get(eventDay).push(event);
                } else {
                    eventsPerDay.set(eventDay, [event]);
                }
            });
            const currentMonth = date.getMonth();
            const currentYear = date.getFullYear();
            monthsEventMap.set(`${currentMonth}-${currentYear}`, eventsPerDay);
            reloadEventsContainer(date);
        })
        .catch(error => {
            console.log(error);
            // eventContainer.innerHTML = "<p style='margin-left: 50px; margin-top: 20px'>No events for today.</p>";
        });
}

const reloadEventsContainer = (date) => {
    const eventListMonth = monthsEventMap.get(`${date.getMonth()}-${date.getFullYear()}`);

    if (!eventListMonth || !eventListMonth.has(date.getDate())) {
        eventContainer.innerHTML = "<p style='margin-left: 50px; margin-top: 20px'>No events for today.</p>";
        return;
    }

    const eventList = eventListMonth.get(date.getDate());

    eventContainer.innerHTML = "";
    eventList.forEach(event => {
        const {
            id,
            name,
            type,
            date,
            location,
            duration,
            client,
            bookingType,
            productManager,
            crew,
            enrolled,
            status,
            description
        } = event;

        const parsedDate = new Date(date);
        const startTime = `${String(parsedDate.getHours()).padStart(2, '0')}:${String(parsedDate.getMinutes()).padStart(2, '0')}`;
        const safeNameId = name.replace(/\s/g, "");
        const undefinedString = `Not defined for this event.`;

        let crewString = formatCrewString(crew); // A string of the crew required for the event, already sanitised.
        let enrolledArray = formatEnrolled(enrolled); // An array of spans, each containing a role and the people enrolled for that role. Already sanitised.

        console.log(enrolledArray);
        // Create the main div for the event.
        let mainDiv = document.createElement("div");
        mainDiv.classList.add("accordion-item");

        // Create the header.
        let headerDiv = document.createElement("h2");
        headerDiv.classList.add("accordion-header");
        let buttonDiv = document.createElement("button");
        buttonDiv.classList.add("accordion-button", "collapsed");
        buttonDiv.setAttribute("type", "button");
        buttonDiv.setAttribute("data-bs-toggle", "collapse");
        buttonDiv.setAttribute("data-bs-target", `#${safeNameId}`);
        buttonDiv.setAttribute("aria-expanded", "true");
        buttonDiv.setAttribute("aria-controls", `${safeNameId}`);
        let boldButtonText = document.createElement("b");
        boldButtonText.textContent = `${name}`;
        let italicButtonText = document.createElement("i");
        italicButtonText.textContent = `. Starting ${startTime}`;
        let statusBox = document.createElement("span");
        statusBox.style.marginLeft = "5px";
        statusBox.style.padding = "5px";
        statusBox.style.borderRadius = "5px";
        statusBox.style.backgroundColor = status === "TODO" ? "#FFC107" : status === "ENROLLED" ? "#28A745" : "#DC3545";
        statusBox.textContent = javaEnumToString(status);
        buttonDiv.appendChild(boldButtonText);
        buttonDiv.appendChild(italicButtonText);
        buttonDiv.appendChild(statusBox);
        headerDiv.appendChild(buttonDiv);
        mainDiv.appendChild(headerDiv);

        // Create the collapse div.
        let collapseDiv = document.createElement("div");
        collapseDiv.classList.add("accordion-collapse", "collapse");
        collapseDiv.setAttribute("id", `${safeNameId}`);
        collapseDiv.setAttribute("data-bs-parent", "#accordionExample");
        let bodyDiv = document.createElement("div");
        bodyDiv.classList.add("accordion-body");

        // Create the first half of the body.
        let halfOneDiv = document.createElement("div");
        halfOneDiv.classList.add("halfOne");

        let halfOneTags = [
            ["Name", name],
            ["Type", javaEnumToString(type)],
            ["Start time", startTime],
            ["Location", location],
            ["Duration", `${duration} hours`],
        ];

        halfOneTags.forEach(([tagName, tagValue]) => {
            let tagSpan = document.createElement("span");
            tagSpan.innerHTML = `<b>${tagName}:</b> ${tagValue}<br>`;
            halfOneDiv.appendChild(tagSpan);
        });

        // Create the second half of the body.
        let halfTwoDiv = document.createElement("div");
        halfTwoDiv.classList.add("halfTwo");

        let halfTwoTags = [
            ["Client", client],
            ["Booking type", javaEnumToString(bookingType)],
            ["Product manager", productManager || undefinedString],
            ["Status", javaEnumToString(status)],
            ["Description", description || undefinedString],
            ["Missing crew", crewString],
            ["Enrolled crew", enrolledArray],
        ];

        halfTwoTags.forEach(([tagName, tagValue]) => {
            let tagSpan = document.createElement("span");
            if (tagValue instanceof Array) {
                tagSpan.innerHTML = `<b>${tagName}: </b>`;
                tagValue.forEach(tag => {
                    tagSpan.appendChild(tag);
                });
                tagSpan.innerHTML += `<br>`;
            } else {
                tagSpan.innerHTML = `<b>${tagName}:</b> ${tagValue}<br>`;
            }
            halfTwoDiv.appendChild(tagSpan);
        });

        // Append the two halves to the body.
        bodyDiv.appendChild(halfOneDiv);
        bodyDiv.appendChild(halfTwoDiv);

        // Add missing br and enroll button.
        bodyDiv.innerHTML += `<br><button class="btn btn-primary" onclick="enroll(${id})">Enroll</button>`;

        // Append the body to the collapse div.
        collapseDiv.appendChild(bodyDiv);

        // Append the collapse div to the main div.
        mainDiv.appendChild(collapseDiv);

        // Append the main div to the event container.
        eventContainer.appendChild(mainDiv);
    });
    document.querySelectorAll('.hoverable').forEach((item) => {
        item.addEventListener('mousemove', (e) => {
            let hoverable = e.target;
            while (!hoverable.classList.contains('hoverable')) {
                hoverable = hoverable.parentNode;
                if (!hoverable) return;
            }
            const detailsId = hoverable.getAttribute('data-details');
            const details = document.getElementById(detailsId);
            details.style.left = (e.pageX + 10) + 'px';
            details.style.top = (e.pageY + 10) + 'px';
        });
    });
}

const updateTaskBar = () => {
    const newTaskBarDate = new Date();
    newTaskBarDate.setDate(
        parseInt(document.querySelector(".day.current:not(.prev):not(.next)").textContent));
    newTaskBarDate.setMonth(currentMonth);
    let dayIndex = newTaskBarDate.getDay() - 1;
    dayIndex = dayIndex < 0 ? 6 : dayIndex;
    eventDayHandler.textContent = `${days[dayIndex]}`;
    eventDateHandler.textContent = `${newTaskBarDate.getDate()} ${months[currentMonth]}`;
    console.log(newTaskBarDate.getDay(), newTaskBarDate.getMonth());

    // display a temporary events container with the cached events.
    reloadEventsContainer(newTaskBarDate);

    // fetch updated events from the server and reload the events' container.
    updateEvents(newTaskBarDate);
}

updateTaskBar();