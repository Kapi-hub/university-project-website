const weekdaysNumberContainer = document.querySelector(".weekdaysNumber");
const month = document.querySelector(".month");
const months = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec"
]
const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

// get current date, month, year
let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();

// function to render the calendar for current month
function renderCalendar() {

    // get prev, current, next month days
    currentDate.setDate(1);
    const firstDay = new Date(currentYear, currentMonth, 1);
    // lastDay -> 0 refers to the last day of prev month, so this is why
    // currentMonth + 1 was passed as an argument. lastDay, thus, gets the last day
    // of the current month
    const lastDay = new Date(currentYear, currentMonth + 1, 0);
    const lastDayIndex = lastDay.getDay();
    const lastDayDate = lastDay.getDate();
    const prevLastDay = new Date(currentYear, currentMonth, 0);
    const prevLastDayDate = prevLastDay.getDate();
    const nextDays = 7 - lastDayIndex - 1;

    // update month, year & days
    month.innerHTML = `${months[currentMonth]} ${currentYear}`;
    let days = "";

    // prev month days
    // for(let x = firstDay.getDate() + 1; x > 0; x--) {
    //     days += `<div class= "day prev"> ${prevLastDayDate - x + 1}</div>`;
    // }

    // current month days
    for(let x = 1; x <= lastDayDate; x++) {
        //check if it's today then add today class
        if( x === new Date().getDate() &&
            currentMonth === new Date().getMonth() &&
            currentYear === new Date().getFullYear()) {
            // if date month year matches
            days += `<div class="day today">${x}</div>`;
        } else {
            // dont add today
            days += `<div class="day">${x}</div>`;

        }
    }

    weekdaysNumberContainer.innerHTML = days;
}

renderCalendar();

