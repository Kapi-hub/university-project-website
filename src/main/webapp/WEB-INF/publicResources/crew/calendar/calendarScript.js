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

let eventDayHandler = document.querySelector(".event-day");
let eventDateHandler = document.querySelector(".event-date");
let eventContainer = document.getElementById("event-accordion");

function javaEnumToString(javaEnum) {
    return javaEnum == null ? "Not defined." : javaEnum.split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
        .join(' ');
}

function getEventsForDay(date) {
    // Format the date as DD-MM-YYYY
    const day = String(date.getDate()).padStart(2, '0'); // padding with 0s to make it 2 digits
    const month = String(date.getMonth() + 1).padStart(2, '0'); // JavaScript months are 0-11 apparently
    const year = date.getFullYear(); // 4 digit year
    const formattedDate = `${year}-${month}-${day}`;

    return fetch('/api/event/getFromDate?date=' + formattedDate, {
        method: 'GET',
        credentials: 'include',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok || response.status === 204) {
                throw new Error('No events returned ' + response.status);
            }
            return response.json();
        });
}

function updateTaskBar() {
    let newTaskBarDate = new Date();
    newTaskBarDate.setDate(
        parseInt(document.querySelector(".day.current:not(.prev):not(.next)").textContent));
    newTaskBarDate.setMonth(currentMonth);
    let dayIndex = newTaskBarDate.getDay() - 1;
    if (dayIndex < 0) {
        dayIndex = 6;
    }
    eventDayHandler.innerHTML = `${days[dayIndex]}`;
    eventDateHandler.innerHTML = `${newTaskBarDate.getDate()} ${months[currentMonth]}`
    console.log(newTaskBarDate.getDay(), newTaskBarDate.getMonth());

    getEventsForDay(newTaskBarDate)
        .then(eventList => {
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
                const startTime = String(parsedDate.getHours()).padStart(2, '0') + ":" + String(parsedDate.getMinutes()).padStart(2, '0');
                let crewString = "";
                crew.forEach(function (crewTypeAndPeople) {
                    const crewType = crewTypeAndPeople[0];
                    const crewAmount = crewTypeAndPeople[1];

                    crewString += crewType + ": " + crewAmount + ", ";
                });

                if (crewString === "") {
                    crewString = "All required crew is enrolled.";
                }

                let enrolledString = "";
                enrolled.forEach(function (roleAndPeople) {
                    const role = roleAndPeople[0];
                    const people = roleAndPeople[1];

                    let peopleString = people.join(", ");
                    let detailsId = "details-" + (name + role).replace(/\s/g, ""); // replace spaces with nothing to make it a valid id
                    enrolledString += `<span class="hoverable" data-details="${detailsId}">${role}: ${people.length} <span id="${detailsId}" class="details">${peopleString}</span></span>, `;
                });

                if (enrolledString === "") {
                    enrolledString = "No one is enrolled yet.";
                }

                eventContainer.innerHTML += `<div class="accordion-item">
                        <h2 class="accordion-header">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#` + name.replace(/\s/g, "") + `" aria-expanded="true" aria-controls="` + name.replace(/\s/g, "") + `">
                                <b>` + name + `</b>. Starting ` + startTime + `
                            </button>
                        </h2>
                        <div id="` + name.replace(/\s/g, "") + `" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
                            <div class="accordion-body">
                                <div class="halfOne">
                                    <b>Name:</b> ` + name + `<br>
                                    <b>Type:</b> ` + javaEnumToString(type) + `<br>
                                    <b>Start time:</b> ` + startTime + `<br>
                                    <b>Location:</b> ` + location + `<br>
                                    <b>Duration:</b> ` + duration + ` hours<br>
                                </div>
                                <div class="halfTwo">
                                    <b>Client:</b> ` + client + `<br>
                                    <b>Booking Type:</b> ` + javaEnumToString(bookingType) + `<br>
                                    <b>Product Manager:</b> ` + productManager + `<br>
                                    <b>Missing Crew:</b> ` + crewString + `<br>
                                    <b>Currently Enrolled:</b> ` + enrolledString + `<br>
                                    <b>Status:</b> ` + javaEnumToString(status) + `<br>
                                    <b>Description:</b> ` + description + `
                                </div>
                                <br>
                                <button class="btn btn-primary" onclick="enroll(` + id + `)">Enroll</button>
                            </div>
                        </div>
                    </div>`;
            });
            document.querySelectorAll('.hoverable').forEach((item) => {
                item.addEventListener('mousemove', (e) => {
                    const detailsId = e.target.getAttribute('data-details');
                    const details = document.getElementById(detailsId);
                    details.style.left = (e.pageX + 10) + 'px';
                    details.style.top = (e.pageY + 10) + 'px';
                });
            });
        })
        .catch(function (error) {
            console.log(error);
            eventContainer.innerHTML = "<p style='margin-left: 50px; margin-top: 20px'>No events for today.</p>";
        });
}

updateTaskBar();
