function enroll(eventId) {
    const XHR = new XMLHttpRequest();
    XHR.open("POST", "/api/event/enroll-self");
    XHR.setRequestHeader('Content-Type', 'application/json');
    XHR.withCredentials = true;
    XHR.addEventListener("load", function () {
        if (this.status === 200) {
            updateTaskBar();
            alert("Enrolled successfully");
        } else {
            alert("An error occurred, enrolling unsuccessful.");
        }
    });
    XHR.send(JSON.stringify({
        id: eventId
    }));
}

function unenroll(eventId) {
    const XHR = new XMLHttpRequest();
    XHR.open("POST", "/api/event/unenroll-self");
    XHR.setRequestHeader('Content-Type', 'application/json');
    XHR.withCredentials = true;
    XHR.addEventListener("load", function () {
        if (this.status === 200) {
            updateTaskBar();
            alert("Unenrolled successfully");
        } else {
            alert("An error occurred, unenrolling unsuccessful.");
        }
    });
    XHR.send(JSON.stringify({
        id: eventId
    }));
}

function getFormattedDate(string) {
    return (new Date(string)).toLocaleDateString("en-GB", {
        day: "numeric",
        month: "short",
        year: "numeric"
    });
}

function getFormattedTime(string) {
    return new Date(string).toLocaleTimeString("en-GB", {
        hour: "numeric",
        minute: "numeric"
    });
}

function addHover() {
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

function numberToString(number) {
    const nums = ["zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen",
        "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"];
    let prefix = number < 0 ? "negative " : "";
    number = Math.abs(number);
    return prefix + (
        number < 20 ? nums[number] :
            number < 100 ? nums[Math.floor(number / 10) + 18] + (number % 10 !== 0 ? "-" + nums[number % 10] : "") :
                number < 1000 ? nums[Math.floor(number / 100)] + " hundred" + (number % 100 !== 0 ? " and " + numberToString(number % 100) : "") :
                    "a lot"
    );
}

const formatCrewString = (crew) => {
    let crewString = "";
    crew.forEach(([crewType, crewAmount]) => {
        crewString += `${javaEnumToString(crewType)}: ${crewAmount}, `;
    });
    return crewString || "All required crew is enrolled.";
}

const formatEnrolled = (enrolled, eventName) => {
    let enrolledArray = [];
    enrolled.forEach(([role, people]) => {
        const peopleString = people.join(", ");
        const detailsId = `details-${(eventName + role).replace(/\s/g, "")}`;
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
        dottedUnderline.textContent = `${javaEnumToString(role)}`;
        enrolledSpan.appendChild(dottedUnderline);
        enrolledSpan.appendChild(document.createTextNode(`: ${people.length} `))
        enrolledSpan.appendChild(detailsSpan);
        enrolledArray.push(enrolledSpan);
    });
    return enrolledArray.length === 0 ? "No one is enrolled yet." : enrolledArray;
}

const javaEnumToString = (javaEnum) => javaEnum
    ? javaEnum.split('_').map(word => `${word.charAt(0).toUpperCase()}${word.slice(1).toLowerCase()}`).join(' ')
    : "Not defined.";

function getFormattedSpan(tagName, tagValue) {
    let tagSpan = document.createElement("span");
    if (tagValue instanceof Array) {
        tagSpan.innerHTML = `<b>${tagName}: </b>`;
        tagValue.forEach(tag => {
            tagSpan.appendChild(tag);
        });
        tagSpan.innerHTML += `<br>`;
    } else {
        let b = document.createElement("b");
        let br = document.createElement("br");
        let text = document.createTextNode(` ${tagValue}`);
        b.textContent = `${tagName}:`;
        tagSpan.appendChild(b);
        tagSpan.appendChild(text);
        tagSpan.appendChild(br);
    }
    return tagSpan;
}

function getBottomButton(canEnrol, isEnrolled, id) {
    if (canEnrol) {
        return `<br><button class="btn btn-primary" style="background-color: var(--bs-primary)" onclick="enroll(${id})">Enroll</button>`;
    } else if (isEnrolled) {
        return `<br><button class="btn btn-primary" style="background-color: red" onclick="unenroll(${id})">Unenroll</button>`;
    } else {
        return `
               <br>
               <button class="btn btn-primary inactive-btn" style="background-color: grey" disabled">
               You cannot enroll for this event
               </button>
               `;
    }
}

function getHtmlElement(event) {
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

    const startTime = getFormattedTime(date);
    const undefinedString = `Not defined for this event.`;

    let crewString = formatCrewString(crew); // A string of the crew required for the event, already sanitised.
    let enrolledArray = formatEnrolled(enrolled, name); // An array of spans, each containing a role and the people enrolled for that role. Already sanitised.

    let returnDiv = document.createElement("div");

    let innerDiv = document.createElement("div");

    let tags = [
        ["Type", javaEnumToString(type)],
        ["Start time", startTime],
        ["Location", location],
        ["Duration", `${duration} hours`],
        ["Client", client],
        ["Booking type", javaEnumToString(bookingType)],
        ["Product manager", productManager || undefinedString],
        ["Status", javaEnumToString(status)],
        ["Description", description || undefinedString],
        ["Open slots", crewString],
        ["Enrolled crew", enrolledArray],
    ];

    tags.forEach(([tagName, tagValue]) => {
        innerDiv.appendChild(getFormattedSpan(tagName, tagValue));
    });

    returnDiv.appendChild(innerDiv);

    if (status !== "done") {
        returnDiv.innerHTML += getBottomButton(canEnrol, isEnrolled, id);
    }

    return returnDiv;
}