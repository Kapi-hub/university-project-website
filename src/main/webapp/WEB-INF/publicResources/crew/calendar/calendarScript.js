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
        //check if date has an event
        let classNames = "day";
        //check if it's today then add today class
        if (x === new Date().getDate() &&
            currentMonth === new Date().getMonth() &&
            currentYear === new Date().getFullYear()) {
            classNames += " current";
        }
        days += `<div class="${classNames}">${x.toString().padStart(2, "0")}`;
        days += `<span class="dot-container" day="${x}"></span>`;
        days += `</div>`;
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
    li_items[currentMonth].classList.remove("active");
    while (index < 0) {
        currentYear--;
        index = 12 + index;
    }
    while (index > 11) {
        currentYear++;
        index = index - 12;
    }
    currentMonth = index;
    li_items[currentMonth].classList.add("active");
    activeDate.setFullYear(currentYear);
    activeDate.setMonth(currentMonth);
    activeDate.setDate(1);
    renderCalendar();
    let days = document.getElementsByClassName("day");
    for (i = 0; i < days.length; i++) {
        if (days[i].textContent.includes("01")) {
            days[i].classList.add("current");
        } else {
            days[i].classList.remove("current");
        }
    }
    reloadSidePanelHeader();
    reloadEventsContainer();
    updateEvents(activeDate);
}

nextBtn.addEventListener("click", () => {
    setCurrentMonth(currentMonth + 1);
})
nextYrBtn.addEventListener("click", () => {
    currentYear++;
    setCurrentMonth(currentMonth);
})
prevBtn.addEventListener("click", () => {
    setCurrentMonth(currentMonth - 1);
})

prevYrBtn.addEventListener("click", () => {
    currentYear--;
    setCurrentMonth(currentMonth);
})

renderCalendar();


// /**************************************/
// /********* SIDE BAR FUNCTIONS *********/
// /**************************************/
let activeDate = new Date();

const eventDayHandler = document.querySelector(".event-day");
const eventDateHandler = document.querySelector(".event-date");
const eventContainer = document.getElementById("event-accordion");

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
            monthsEventMap.set(currentMonth + "-" + currentYear, eventsPerDay);
            reloadEventsContainer(date);
        })
        .catch(error => {
            console.error(error);
        });
}

const reloadEventsContainer = () => {
    const eventListMonth = monthsEventMap.get(activeDate.getMonth() + "-" + activeDate.getFullYear());
    const dotContainer = document.querySelectorAll(".dot-container");

    dotContainer.forEach(dot => {
        dot.innerHTML = "";
        let day = parseInt(dot.getAttribute("day"));
        if (eventListMonth && eventListMonth.has(day)) {
            eventListMonth.get(day).forEach(event => {
                let dotElem = document.createElement("div");
                dotElem.classList.add("dot");
                dotElem.style.backgroundColor = event.canEnrol ? "#FFC107" : event.isEnrolled ? "#28A745" : "#DC3545";
                dot.appendChild(dotElem);
            });
        }
    });

    if (!eventListMonth || !eventListMonth.has(activeDate.getDate())) {
        eventContainer.innerHTML = "<p style='margin-left: 50px; margin-top: 20px'>No events for today.</p>";
        return;
    }

    const eventList = eventListMonth.get(activeDate.getDate());
    let wasUnCollapsedIds = [];
    document.querySelectorAll(".accordion-button:not(.collapsed)").forEach(button => {
        wasUnCollapsedIds.push(button.getAttribute("aria-controls"));
    })

    eventContainer.innerHTML = "";
    eventList.forEach(event => {
        const startTime = getFormattedTime(event.date);
        const safeNameId = event.id.toString();

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
        if (wasUnCollapsedIds.includes(safeNameId)) {
            buttonDiv.classList.remove("collapsed");
        }
        let boldButtonText = document.createElement("b");
        boldButtonText.textContent = `${event.name}`;
        let italicButtonText = document.createElement("i");
        italicButtonText.textContent = `. Starting ${startTime}`;
        let statusBox = document.createElement("span");
        statusBox.style.marginLeft = "5px";
        statusBox.style.padding = "5px";
        statusBox.style.borderRadius = "5px";
        statusBox.style.backgroundColor = event.canEnrol ? "#FFC107" : event.isEnrolled ? "#28A745" : "#DC3545";
        statusBox.textContent = event.canEnrol ? "Open" : event.isEnrolled ? "Enrolled" : "Unavailable";
        buttonDiv.appendChild(boldButtonText);
        buttonDiv.appendChild(italicButtonText);
        buttonDiv.appendChild(statusBox);
        headerDiv.appendChild(buttonDiv);
        mainDiv.appendChild(headerDiv);

        // Create the collapse div.
        let collapseDiv = document.createElement("div");
        collapseDiv.classList.add("accordion-collapse", "collapse");
        if (wasUnCollapsedIds.includes(safeNameId)) {
            collapseDiv.classList.add("show");
        }
        collapseDiv.setAttribute("id", `${safeNameId}`);
        collapseDiv.setAttribute("data-bs-parent", "#accordionExample");
        let bodyDiv = getHtmlElement(event);
        bodyDiv.classList.add("accordion-body");

        // Append the body to the collapse div.
        collapseDiv.appendChild(bodyDiv);

        // Append the collapse div to the main div.
        mainDiv.appendChild(collapseDiv);

        // Append the main div to the event container.
        eventContainer.appendChild(mainDiv);
    });
    addHover();
}

function reloadSidePanelHeader() {
    const dayIndex = (activeDate.getDay() || 7) - 1;
    eventDayHandler.textContent = `${days[dayIndex]}`;
    eventDateHandler.textContent = `${activeDate.getDate().toString().padStart(2, "0")} ${months[currentMonth]}`;
}

function updateTaskBar() {
    if (document.querySelector(".day.current").classList.contains("prev")) {
        setCurrentMonth(currentMonth - 1);
        return;
    } else if (document.querySelector(".day.current").classList.contains("next")) {
        setCurrentMonth(currentMonth + 1);
        return;
    }
    activeDate.setDate(
        parseInt(document.querySelector(".day.current").textContent));
    activeDate.setMonth(currentMonth);

    reloadSidePanelHeader();
    reloadEventsContainer();
    updateEvents(activeDate);
}

updateTaskBar();