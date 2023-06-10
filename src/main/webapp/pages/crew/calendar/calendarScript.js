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
    for(let i = firstDay.getDay() - 1; i > 0; i--) {
        days += `<div class="day prev">${prevLastDayDate - i + 1}</div>`;
    }

    // current month days
    for(let x = 1; x <= lastDayDate; x++) {
        //check if it's today then add today class
        if( x === new Date().getDate() &&
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
    for( let j = 1; j <= nextDays + 1; j++) {
        days += `<div class="day next">${j}</div>`;
    }
    weekdaysNumberContainer.innerHTML = days;

    // TODO: fix the bug with October month, 1 starts on Monday as of rn, but it should start on Sunday.
    // TODO: fix buttons, they are smaller then the clickable space.

    // MANDATORY to be here because the elements are loaded by renderCalendar().
    let day_items = document.querySelectorAll(".weekdaysNumber div");
    day_items.forEach(function(item) {
        item.addEventListener("click", function() {
            day_items.forEach(function (div){
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
    if ( currentMonth > 11 ) {
        currentMonth = 0;
        currentYear++;
    }
    li_items[currentMonth].classList.add("active");
    renderCalendar();
})
prevBtn.addEventListener("click", () => {
    li_items[currentMonth].classList.remove("active");
    currentMonth--;
    if ( currentMonth < 0 ) {
        currentMonth = 11;
        currentYear--;
    }
    li_items[currentMonth].classList.add("active");
    renderCalendar();
})


monthItem.forEach( function (item) {
    item.addEventListener("click", () => {
        switch(event.target.id) {
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

function updateTaskBar() {
    let newTaskBarDate = new Date();
    newTaskBarDate.setDate(
        parseInt(document.querySelector(".day.current").textContent));
    newTaskBarDate.setMonth(currentMonth);
    eventDayHandler.innerHTML = `${days[newTaskBarDate.getDate()]}`;
    eventDateHandler.innerHTML = `${newTaskBarDate.getDate()} ${months[currentMonth]}`
    console.log(newTaskBarDate.getDay(), newTaskBarDate.getMonth());
}
updateTaskBar();