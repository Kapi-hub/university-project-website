const weekdaysNumberContainer = document.querySelector(".weekdaysNumber");
const month = document.querySelector(".month");
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
    const lastDay = new Date(currentYear, currentMonth + 1, 0);
    const lastDayDate = lastDay.getDate();
    // update month, year & days
    month.innerHTML = `${months[currentMonth]} ${currentYear}`;
    let days = "";

    // current month days
    for(let x = 1; x <= lastDayDate; x++) {
        //check if it's today then add today class
        if( x === new Date().getDate() &&
            currentMonth === new Date().getMonth() &&
            currentYear === new Date().getFullYear()) {
            // if date month year matches
            days += `<div class="day current">${x}</div>`;
        } else {
            // dont add today
            days += `<div class="day">${x}</div>`;
        }
    }
    weekdaysNumberContainer.innerHTML = days;

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