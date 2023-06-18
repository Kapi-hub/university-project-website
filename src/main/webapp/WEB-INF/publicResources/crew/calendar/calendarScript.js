const weekdaysNumberContainer = document.querySelector(".weekdaysNumber"),
    prevYrBtn = document.querySelector(".btn.prevYr"),
    prevBtn = document.querySelector(".btn.prev"),
    nextBtn = document.querySelector(".btn.next"),
    nextYrBtn = document.querySelector(".btn.nextYr");

month = document.querySelector(".month");

const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

// get current date, month, year
let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();

let monthsEventMap = new Map();

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
    console.log("prevday: " + prevLastDay);
    console.log("prevdate: " + prevLastDayDate);
    let nextDays = 7 - (lastDayIndex || 7);
    // console.log(prevLastDay);
    // console.log(prevLastDayDate);
    // console.log(lastDay);
    // console.log(lastDayIndex);
    // update month, year
    month.innerHTML = `${months[currentMonth]} ${currentYear}`;
    let days = "";

    // previous month days
    for (let i = (firstDay.getDay() || 7) - 1; i > 0; i--) {
        days += `<div class="day prev">${prevLastDayDate - i + 1}</div>`;
    }

    // current month days
    for (let x = 1; x <= lastDayDate; x++) {
        //check if it's today then add today class
        if (x === new Date().getDate() &&
            currentMonth === new Date().getMonth() &&
            currentYear === new Date().getFullYear()) {
            // if date month year matches
            days += `<div class="day current">${x.toString().padStart(2, "0")}</div>`;
        } else {
            // don't add today
            days += `<div class="day ">${x.toString().padStart(2, "0")}</div>`;
        }
    }
    for (j = 0; j < nextDays; j++) {
        days += `<div class="day next">${(j + 1).toString().padStart(2, "0")}</div>`;
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

function setCurrentMonth(index) {
    currentMonth = index;
    renderCalendar();
}

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
nextYrBtn.addEventListener("click", () => {
    currentYear++;
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

prevYrBtn.addEventListener("click", () => {
    currentYear--;
    renderCalendar();
})

renderCalendar();


// /**************************************/
// /********* SIDE BAR FUNCTIONS *********/
// /**************************************/
let activeDate = new Date();

const eventDayHandler = document.querySelector(".event-day");
const eventDateHandler = document.querySelector(".event-date");
const eventContainer = document.getElementById("event-accordion");

const javaEnumToString = (javaEnum) => javaEnum
    ? javaEnum.split('_').map(word => `${word.charAt(0).toUpperCase()}${word.slice(1).toLowerCase()}`).join(' ')
    : "Not defined.";

const getEventsForMonth = (date) => {
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
        });
}

const reloadEventsContainer = () => {
    const eventListMonth = monthsEventMap.get(`${activeDate.getMonth()}-${activeDate.getFullYear()}`);

    if (!eventListMonth || !eventListMonth.has(activeDate.getDate())) {
        eventContainer.innerHTML = "<p style='margin-left: 50px; margin-top: 20px'>No events for today.</p>";
        return;
    }

    const eventList = eventListMonth.get(activeDate.getDate());

    const wasCollapsed = !document.querySelector(".accordion-button:not(.collapsed)");

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
            description,
            isEnrolled,
            canEnrol,
        } = event;

        const parsedDate = new Date(date);
        const startTime = `${String(parsedDate.getHours()).padStart(2, '0')}:${String(parsedDate.getMinutes()).padStart(2, '0')}`;
        const safeNameId = name.replace(/\s/g, "");
        const undefinedString = `Not defined for this event.`;

        let crewString = formatCrewString(crew); // A string of the crew required for the event, already sanitised.
        let enrolledArray = formatEnrolled(enrolled); // An array of spans, each containing a role and the people enrolled for that role. Already sanitised.

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
        if (!wasCollapsed) {
            buttonDiv.classList.remove("collapsed");
        }
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
        if (!wasCollapsed) {
            collapseDiv.classList.add("show");
        }
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
            ["Open slots", crewString],
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

        // Add missing br and enroll or unenroll button / no button if we cannot enroll.
        if (canEnrol) {
            bodyDiv.innerHTML += `<br><button class="btn btn-primary" style="background-color: var(--bs-primary)" onclick="enroll(${id})">Enroll</button>`;
        } else if (isEnrolled) {
            bodyDiv.innerHTML += `<br><button class="btn btn-primary" style="background-color: red" onclick="unenroll(${id})">Unenroll</button>`;
        } else {
            bodyDiv.innerHTML +=
                `
                <br>
                <button class="btn btn-primary unavailable" style="background-color: grey" disabled">
                You cannot enroll for this event
                </button>
                `;
        }

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

function updateTaskBar() {
    if (document.querySelector(".day.current").classList.contains("prev")) {
        li_items[currentMonth].classList.remove("active");
        currentMonth--;
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        }
        li_items[currentMonth].classList.add("active");
        renderCalendar();
        return;
    } else if (document.querySelector(".day.current").classList.contains("next")) {
        li_items[currentMonth].classList.remove("active");
        currentMonth++;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }
        li_items[currentMonth].classList.add("active");
        renderCalendar();
        return;
    }
    activeDate.setDate(
        parseInt(document.querySelector(".day.current").textContent));
    activeDate.setMonth(currentMonth);
    const dayIndex = (activeDate.getDay() || 7) - 1;
    eventDayHandler.textContent = `${days[dayIndex]}`;
    eventDateHandler.textContent = `${activeDate.getDate().toString().padStart(2, "0")} ${months[currentMonth]}`;

    reloadEventsContainer();
    updateEvents(activeDate);
}

updateTaskBar();