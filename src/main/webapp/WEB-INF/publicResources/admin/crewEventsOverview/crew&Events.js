function addEvent() {

    //get client Information
    let clientQuery = document.querySelector('#clientInfo');
    const clientFirstName = clientQuery.children[1].value;
    const clientLastName = clientQuery.children[4].value;
    const clientPhone = clientQuery.children[7].value;
    const clientEmail = clientQuery.children[10].value;


    const eventTitle = document.getElementById('eventTitle').value;
    const eventDescription = document.getElementById('eventDescription').value;
    const eventDateTime = document.getElementById("eventDate&Time").value;
    const eventLocation = document.getElementById('eventLocation').value;
    const eventDuration = document.getElementById('eventDuration').value;


    const club_photo = document.getElementById('photoBox');
    const festival = document.getElementById('festivalBox');
    const product = document.getElementById('productBox');

    const spanElement = document.getElementById('producerName');
    const spanText = spanElement.textContent;
    console.log("producer name " + spanText);

    let eventType = '';
    if (club_photo.checked) {
        eventType = club_photo.value;
    } else if (festival.checked) {
        eventType = festival.value;
    } else if (product.checked) {
        eventType = product.value;
    }

    const photography = document.getElementById('photographyBox');
    const film = document.getElementById('filmBox');
    const marketing = document.getElementById('marketingBox');
    const other = document.getElementById('otherBox');

    let bookingType = '';
    if (photography.checked) {
        bookingType = photography.value;
    } else if (film.checked) {
        bookingType = film.value;
    } else if (marketing.checked) {
        bookingType = marketing.value;
    } else if (other.checked) {
        bookingType = other.value;
    }

    let eventCrewQuery = document.querySelector("#requiredCrew");
    const eventPhotographer = eventCrewQuery.children[1].value;
    const eventAssistant = eventCrewQuery.children[4].value;
    const eventEditor = eventCrewQuery.children[7].value;
    const eventDataHandler = eventCrewQuery.children[10].value;
    const eventPlanner = eventCrewQuery.children[13].value;
    const eventVideographer = eventCrewQuery.children[16].value;


    //TODO: check how to do with the drop down list - how do i extract the name that is selected
    // const eventProducer;

    var data = {
        clientBean: {
            forename: clientFirstName,
            surname: clientLastName,
            phone_number: clientPhone,
            emailAddress: clientEmail
        },
        eventBean: {
            name: eventTitle,
            description: eventDescription,
            start: eventDateTime,
            duration: eventDuration,
            location: eventLocation,
            type: eventType,
            booking_type: bookingType
        },

        requiredCrewBeans: [
            {
                crew_size: eventPhotographer,
                role: "PHOTOGRAPHER"
            },
            {
                crew_size: eventAssistant,
                role: "ASSISTANT"
            },
            {
                crew_size: eventEditor,
                role: "EDITOR"
            },
            {
                crew_size: eventDataHandler,
                role: "DATA_HANDLER"
            },
            {
                crew_size: eventPlanner,
                role: "PLANNER"
            },
            {
                crew_size: 0,
                role: "PRODUCER"
            },
            {
                crew_size: eventVideographer,
                role: "VIDEOGRAPHER"
            }
        ]


    }
    $.ajax({
        url: "a pi/admin/crewAssignments/newEvent",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function () {
            alert("Event successfully sent!");
        },
        error: function () {
            alert("Error creating a new event!");

        }
    });
}

function addCrewMember() {
    let memberQuery = document.querySelector('#memberInfo');
    const memberFirstName = memberQuery.children[1].value;
    const memberLastName = memberQuery.children[4].value;
    const memberEmail = memberQuery.children[7].value;
    const memberUsername = memberQuery.children[10].value;
    const memberPassword = memberQuery.children[13].value;

    //get member's permissions

    let permissionType = '';
    const clubTeam = document.getElementById('clubTeam');
    const coreTeam = document.getElementById('coreTeam');
    const clubAndCore = document.getElementById('club&core');

    if (clubTeam.checked) {
        permissionType = clubTeam.value;
    } else if (coreTeam.checked) {
        permissionType = coreTeam.value;
    } else if (clubAndCore.checked) {
        permissionType = clubAndCore.value;
    }


    //get member's roles
    var roles = '';
    let photographer = document.querySelector('#photographer input[type="checkbox"]');
    let videographer = document.querySelector('#videographer input[type="checkbox"]');
    let producer = document.querySelector('#producer input[type="checkbox"]');
    let assistant = document.querySelector('#assistant input[type="checkbox"]');
    let dataHandler = document.querySelector('#dataHandler input[type="checkbox"]');
    let planner = document.querySelector('#planner input[type="checkbox"]');
    let editor = document.querySelector('#editor input[type="checkbox"]');

    if (photographer.checked) {
        roles = photographer.value;
    }
    if (videographer.checked) {
        roles = videographer.value;
    }
    if (producer.checked) {
        roles = producer.value;
    }
    if (assistant.checked) {
        roles = assistant.value;
    }
    if (dataHandler.checked) {
        roles = dataHandler.value;
    }
    if (planner.checked) {
        roles = planner.value;
    }
    if (editor.checked) {
        roles = editor.value;
    }
    var data = {
        forename: memberFirstName,
        surname: memberLastName,
        username: memberUsername,
        emailAddress: memberEmail,
        password: memberPassword,
        accountType: "CREW_MEMBER",
        salt: memberPassword,
        role: roles,
        team: permissionType,
    }

    console.log(JSON.stringify(data));
    $.ajax({
        url: "/api/admin/crewAssignments/newMember",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function () {
            alert("Member successfully sent!");
        },
        error: function () {
            alert("Error creating a new member!");

        }

    });
}

function getProducers() {
    let producers = [];
    let id = 0;
    //TODO fix bug that appends the list for each click
    sendHttpRequest('GET', "/api/admin/crewAssignments/newEvent").then(responseData => {
        console.log(responseData);
        responseData.forEach(producer => producers.push(producer));
        const producersList = document.querySelector('.dropdown-menu');
        producers.forEach(producer => {
            const producerItem = document.createElement('li');
            const buttonElement = document.createElement('a');
            producerItem.classList.add('dropdown-item');
            producerItem.onclick = function () {
                selectCrewMember(producer.forename + ' ' + producer.surname);
                id = producer.id;
            };
            producerItem.innerHTML = `<span class="producerName" id="producerName">${producer.forename} ${producer.surname}</span>`;

            producerItem.appendChild(buttonElement);
            producersList.appendChild(producerItem);
        })
    });
    return id;
}

function sendHttpRequest(method, url, data) {
    return new Promise(function (resolve, reject) {
            let XHR = new XMLHttpRequest();

            XHR.open(method, url);
            XHR.responseType = 'json';

            XHR.onload = () => {
                if (XHR.status === 200) {
                    resolve(XHR.response)
                } else {
                    reject(XHR.response)
                }
            }
            XHR.withCredentials = true;
            if (data) {
                XHR.setRequestHeader("Content-Type", "application/json");
            }
            XHR.send(JSON.stringify(data));
        }
    );
}

function selectCrewMember(name) {
    const selectedCrewMember = document.getElementById('selectedCrewMember');
    selectedCrewMember.textContent = name;
}

function getAllMembers() {
    let members = [];
    sendHttpRequest('GET', "/api/admin/crewAssignments/members")
        .then(responseData => {
            responseData.forEach(member => members.push(member));
            responseData.forEach((member) => {
                console.log(responseData);
                const {
                    id, forename, surname, mail, username, role, team
                } = member;
                const container = document.getElementById("crewContainer");

                let card = document.createElement("div");
                card.setAttribute('class', 'card');

                let cardBody = document.createElement("div");
                cardBody.setAttribute('class', 'card-body');

                let crewDetails = document.createElement("div");
                crewDetails.setAttribute('class', 'crew-details');

                let crewName = document.createElement("div");
                crewName.setAttribute('class', 'crew-name');
                crewName.innerHTML = `<ion-icon name="person-outline"></ion-icon> <span>${forename} ${surname}</span>`;

                let crewEmail = document.createElement("div");
                crewEmail.setAttribute('class', 'crew-email');
                crewEmail.innerHTML = `<ion-icon name="mail-outline"></ion-icon>
                                        <p>${mail}</p>`;

                let crewUsername = document.createElement("div");
                crewUsername.setAttribute('class', 'crew-username');
                crewUsername.innerHTML = `<ion-icon name="id-card-outline"></ion-icon>
                                            <p>${username}</p>`;

                let crewRole = document.createElement("div");
                crewRole.setAttribute('class', 'crew-role');
                crewRole.innerHTML = `<ion-icon name="pricetag-outline"></ion-icon>
                                        <p>${role}</p>`;

                let crewTeam = document.createElement("div");
                crewTeam.setAttribute('class', 'crew-team');
                crewTeam.innerHTML = `<ion-icon name="people-circle-outline"></ion-icon>
                                        <p>${team}</p>`;
                container.appendChild(card);
                card.appendChild(cardBody);
                cardBody.appendChild(crewDetails)
                crewDetails.appendChild(crewName);
                crewDetails.appendChild(crewEmail);
                crewDetails.appendChild(crewUsername);
                crewDetails.appendChild(crewTeam);
                crewDetails.appendChild(crewRole);
            })

        })
}

function getAllEvents() {
    let events = [];
    sendHttpRequest('GET', "/api/admin/crewAssignments/bookings")
        .then(responseData => {
            console.log(responseData);
            responseData.forEach(event => events.push(event));
            responseData.forEach((event) => {
                const {
                    id,
                    name,
                    description,
                    start,
                    duration,
                    location,
                    type,
                    booking_type
                    //TODO integrate type and booking type
                } = event.eventDetails;

                const {
                    forename,
                    surname,
                    emailAddress,
                    phone_number
                } = event.eventDetails.clients[0]

                const container = document.querySelector('.container.crew-container');

                let card = document.createElement("div");
                card.setAttribute('class', 'card');

                let cardBody = document.createElement("div");
                cardBody.setAttribute('class', 'card-body');

                let eventDetails = document.createElement("div");
                eventDetails.setAttribute('class', 'event-details');

                let eventName = document.createElement("div");
                eventName.setAttribute('class', 'event-name');
                eventName.innerHTML = `
                        <span>${name}</span>
                        <div class="buttons">
                            <ion-icon name="pencil-outline" id="pencil-outline-icon" onclick = "changeDetails(` + id + `)"></ion-icon>
                            <ion-icon name="trash-outline" id="trash-outline-icon" onclick = "confirmationToast(` + id + `)"></ion-icon>
                        </div>`

                let eventOtherDetails = document.createElement("div");
                eventOtherDetails.setAttribute('class', 'event-other-details');

                let detailsList = document.createElement("ul");

                let dateTime = document.createElement("li");
                dateTime.setAttribute('class', 'date-time');
                let date = new Date(start);
                let minutes = date.getMinutes().toString().padStart(2, '0');
                let dateFormat = date.toDateString() + ", " + date.getHours() + ":" + minutes;
                dateTime.innerHTML = `<ion-icon name="calendar-outline"></ion-icon> <span>${dateFormat}</span>`;

                let eventDuration = document.createElement("li");
                eventDuration.setAttribute('class', 'duration');
                eventDuration.innerHTML = `<ion-icon name="time-outline"></ion-icon> <span>${duration}</span>`;

                let eventLocation = document.createElement("li");
                eventLocation.setAttribute('class', 'location');
                eventLocation.innerHTML = `<ion-icon name="pin-outline"></ion-icon> <span>${location}</span>`;

                let eventDescription = document.createElement("li");
                eventDescription.setAttribute('class', 'location');
                eventDescription.innerHTML = `<ion-icon name="document-outline"></ion-icon> <span>${description}</span>`;

                detailsList.appendChild(eventDescription);
                detailsList.appendChild(eventLocation);
                detailsList.appendChild(eventDuration);
                detailsList.appendChild(dateTime);
                eventOtherDetails.appendChild(detailsList);

                let eventType = document.createElement("div");
                eventType.setAttribute('class', 'event-type');

                //TODO: for alex to change the appearance when smth is selected
                if (type === 'club_photography') {
                    eventType.innerHTML = `<div class="card-event-type club-photography" style="border-color: #1F1F1F; color: white; border-radius: 10px;">
                    <div class="inner club-photography-inner">
                        <div class="front-side"><ion-icon name="camera-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Club<br>Photo</p></div>
                    </div>
                </div>
                <div class="card-event-type festival">
                    <div class="inner festival-inner">
                        <div class="front-side"><ion-icon name="musical-notes-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Festival</p></div>
                    </div>
                </div>
                <div class="card-event-type product-shot">
                    <div class="inner product-shot-inner">
                        <div class="front-side"><ion-icon name="videocam-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Prod<br>Shoot</p></div>
                    </div>
                </div>`;
                } else if (type === 'festival') {
                    eventType.innerHTML = `<div class="card-event-type club-photography">
                    <div class="inner club-photography-inner">
                        <div class="front-side"><ion-icon name="camera-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Club<br>Photo</p></div>
                    </div>
                </div>
                <div class="card-event-type festival" style="border-color: #1F1F1F; color: white; border-radius: 10px;">
                    <div class="inner festival-inner">
                        <div class="front-side"><ion-icon name="musical-notes-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Festival</p></div>
                    </div>
                </div>
                <div class="card-event-type product-shot">
                    <div class="inner product-shot-inner">
                        <div class="front-side"><ion-icon name="videocam-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Prod<br>Shoot</p></div>
                    </div>
                </div>`;
                } else {
                    eventType.innerHTML = `<div class="card-event-type club-photography" style="background-color: red; color: white;">
                    <div class="inner club-photography-inner">
                        <div class="front-side"><ion-icon name="camera-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Club<br>Photo</p></div>
                    </div>
                </div>
                <div class="card-event-type festival">
                    <div class="inner festival-inner">
                        <div class="front-side"><ion-icon name="musical-notes-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Festival</p></div>
                    </div>
                </div>
                <div class="card-event-type product-shot" style="border-color: #1F1F1F; color: white; border-radius: 10px;">
                    <div class="inner product-shot-inner">
                        <div class="front-side"><ion-icon name="videocam-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Prod<br>Shoot</p></div>
                    </div>
                </div>`;
                }

                let bookingType = document.createElement("div");
                bookingType.setAttribute('class', 'booking-type');
                if(booking_type === 'photography') {
                    bookingType.innerHTML = `<div class="booking-type-card photography" style="border-color: #1F1F1F; color: white; border-radius: 10px;">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="camera-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Photo</p></div>
                    </div>
                </div>
                <div class="booking-type-card film">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="film-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Film</p></div>
                    </div>
                </div>
                <div class="booking-type-card marketing">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="analytics-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Marketing</p></div>
                    </div>
                </div>
                <div class="booking-type-card other">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="ellipsis-horizontal-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Other</p></div>
                    </div>
                </div>`;
                } else if (booking_type === 'film') {
                    bookingType.innerHTML = `<div class="booking-type-card photography">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="camera-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Photo</p></div>
                    </div>
                </div>
                <div class="booking-type-card film" style="border-color: #1F1F1F; color: white; border-radius: 10px;">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="film-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Film</p></div>
                    </div>
                </div>
                <div class="booking-type-card marketing">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="analytics-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Marketing</p></div>
                    </div>
                </div>
                <div class="booking-type-card other">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="ellipsis-horizontal-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Other</p></div>
                    </div>
                </div>`;
                } else if (booking_type === "marketing") {
                    bookingType.innerHTML = `<div class="booking-type-card photography">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="camera-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Photo</p></div>
                    </div>
                </div>
                <div class="booking-type-card film">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="film-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Film</p></div>
                    </div>
                </div>
                <div class="booking-type-card marketing" style="border-color: #1F1F1F; color: white; border-radius: 10px;">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="analytics-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Marketing</p></div>
                    </div>
                </div>
                <div class="booking-type-card other">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="ellipsis-horizontal-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Other</p></div>
                    </div>
                </div>`;
                } else {
                    bookingType.innerHTML = `<div class="booking-type-card photography">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="camera-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Photo</p></div>
                    </div>
                </div>
                <div class="booking-type-card film">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="film-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Film</p></div>
                    </div>
                </div>
                <div class="booking-type-card marketing">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="analytics-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Marketing</p></div>
                    </div>
                </div>
                <div class="booking-type-card other" style="border-color: #1F1F1F; color: white; border-radius: 10px;">
                    <div class="booking-type-card-inner">
                        <div class="front-side"><ion-icon name="ellipsis-horizontal-outline"></ion-icon></div>
                        <div class="back-side"><p class="card-description">Other</p></div>
                    </div>
                </div>`;
                }

                let clientDetails = document.createElement("div");
                clientDetails.setAttribute('class', 'client-details');
                let clientName = document.createElement("div");
                clientName.setAttribute('class', 'last-first-name');
                clientName.innerHTML = `<ion-icon name="person-outline"></ion-icon>
                                        <p>${forename} ${surname}</p>`;

                let clientEmail = document.createElement("div");
                clientEmail.setAttribute('class', 'email');
                clientEmail.innerHTML = `<ion-icon name="mail-outline"></ion-icon>
                                        <p>${emailAddress}</p>`;

                let clientPhone = document.createElement("div");
                clientPhone.setAttribute('class', 'phone');
                clientPhone.innerHTML = `<ion-icon name="call-outline"></ion-icon>
                                           <p>${phone_number}</p>`;

                clientDetails.appendChild(clientName);
                clientDetails.appendChild(clientPhone);
                clientDetails.appendChild(clientEmail);


                let eventStaff = document.createElement("div");
                const requirementsArray = event.eventDetails.requirements;
                eventStaff.setAttribute('class', 'event-staff');
                eventStaff.innerHTML = '';
                if (requirementsArray === null) {
                    eventStaff.innerHTML = `<span>There is no crew</span>`
                } else {
                    requirementsArray.forEach(requirement => {
                        const {role, crew_size} = requirement;

                        let roleHtml = `<div class="slide ${role}">
                        <div class="icon" id="${role}-icon">
                            <ion-icon name="person-outline"></ion-icon>
                        </div>
                        <div class="text" id="${role}-text">
                            <p>${role}</p>
                        </div>
                        <div class="counter" id="${role}-counter">
                            <p>${crew_size}</p>
                        </div>
                    </div>`
                        eventStaff.innerHTML += roleHtml;

                    })
                }
                let eventProducer = document.createElement("div");
                eventProducer.setAttribute('class', 'event-producer');
                eventProducer.innerHTML = ` <div class="icon"><ion-icon name="briefcase-outline"></ion-icon></div>
                <div class="text">Event Prod</div>
                <div class="producer-name">Antoine Moghadar</div>`;

                container.appendChild(card);
                card.appendChild(cardBody);
                cardBody.appendChild(eventDetails);
                eventDetails.appendChild(eventName);
                eventDetails.appendChild(eventOtherDetails);
                cardBody.appendChild(eventType);
                cardBody.appendChild(bookingType);
                cardBody.appendChild(clientDetails);
                cardBody.appendChild(eventProducer);
                cardBody.appendChild(eventStaff);
            });
        })
        .catch(error => {
            console.error("Error fetching events:", error);
        });
}

function changeDetails(eventID) {
    alert("aaaaaaaaaaaaaaaaa");
    sendHttpRequest('GET', `/api/admin/crewAssignments`)
        .then(responseData => {
            const event = {
                id,
                name,
                description,
                start,
                duration,
                location,
                type,
                booking_type
                //TODO integrate type and booking type
            } = responseData
            console.log("Se ia ev bine? " + responseData)
            const modalBody = document.querySelector('.modal-body');
            let eventInfo = document.createElement("div");
            eventInfo.setAttribute("class", "eventInfo");
            eventInfo.setAttribute("id", "eventDetails");
            eventInfo.innerHTML = ` <label for="eventTitle" class="eventTitleText">Event Title</label>
                    <input type="text" class="eventTitle" id="editEventTitle" >
                    <br>

                    <label for="eventDescription" class="eventDescriptionText"> Event Description</label>
                    <input type="text" class="eventTime" id="editEventDescription" >
                    <br>

                    <label for="eventDate&Time" class="eventDate&TimeText"> Event Date&Time</label>
                    <input type="datetime-local" class="eventTime" id="editEventDate_Time">
                    <br>

                    <label for="eventLocation" class="eventLocationText"> Event Location</label>
                    <input type="text" class="eventTime" id="editEventLocation">
                    <br>

                    <label for="eventDuration" class="eventDurationText"> Event Duration</label>
                    <input type="number" min="0" class="eventTime" id="editEventDuration"
                           >
                    <br>

                    <label for="eventType" class="eventTypeText">Event Type</label>
                    <br>`;
            // Set the textContent of elements
            eventInfo.querySelector('#editEventTitle').textContent = event.name;
            eventInfo.querySelector('#editEventDescription').textContent = event.description;
            eventInfo.querySelector('#editEventDate_Time').textContent = event.start;
            eventInfo.querySelector('#editEventLocation').textContent = event.location;
            eventInfo.querySelector('#editEventDuration').textContent = event.duration;
            // Set the textContent for eventType
            let editEventType = document.createElement("div");
            editEventType.setAttribute("id", "editEventType");
            editEventType.innerHTML = ` <label class="photoBox">
                            <input type="radio" name="eventRadio" id="editPhotoBox" value="CLUB_PHOTOGRAPHY">
                            <span class="checkmark">Club Photography</span>
                        </label>

                        <label class="festivalBox">
                            <input type="radio" name="eventRadio" id="editFestivalBox" value="FESTIVAL">
                            <span class="checkmark">Festival</span>
                        </label>

                        <label class="productBox">
                            <input type="radio" name="eventRadio" id="editProductBox" value="PRODUCT_SHOOT">
                            <span class="checkmark">Product Shoot</span>
                        </label>`

            if (type === "club_photography") {
                document.getElementById("editPhotoBox").checked = true;
            } else if (type === "festival") {
                document.getElementById("editFestivalBox").checked = true;
            } else {
                document.getElementById("editProductBox").checked = true;
            }

            let editBookingType = document.createElement("div");
            editBookingType.innerHTML = `<label for="bookingType" class="bookingTypeText">Booking Type</label>
                    <br>
                    <label class="photographyBox">
                            <input type="radio" name="bookingRadio" value="PHOTOGRAPHY" id="editPhotographyBox">
                            <span class="checkmark">Photography</span>
                        </label>

                        <label class="filmBox">
                            <input type="radio" name="bookingRadio" id="editFilmBox" value="FILM">
                            <span class="checkmark">Film</span>
                        </label>

                        <label class="marketingBox">
                            <input type="radio" name="bookingRadio" id="editMarketingBox" value="MARKETING">
                            <span class="checkmark">Marketing</span>
                        </label>

                        <label class="otherBox">
                            <input type="radio" name="bookingRadio" id="editOtherBox" value="OTHER">
                            <span class="checkmark">Other</span>
                        </label>`
            if (booking_type === "photography") {
                document.getElementById("editPhotographyBox").checked = true;

            } else if (booking_type === "videography") {
                document.getElementById("editFilmBox").checked = true;

            } else if (booking_type === "marketing") {
                document.getElementById("editMarkteingBox").checked = true;

            } else {
                document.getElementById("editOtherBox").checked = true;
            }
            // let editEventProducer = document.createElement("div");
            // let editRequiredCrew = document.createElement("div");

            eventInfo.appendChild(editRequiredCrew);
            eventInfo.appendChild(editEventProducer);
            eventInfo.appendChild(editBookingType);
            eventInfo.appendChild(editEventType);
            modalBody.appendChild(eventInfo);

        })

    const eventTitle = document.getElementById('eventTitle').value;
    const eventDescription = document.getElementById('eventDescription').value;
    const eventDateTime = document.getElementById("eventDate&Time").value;
    const eventLocation = document.getElementById('eventLocation').value;
    const eventDuration = document.getElementById('eventDuration').value;


    const club_photo = document.getElementById('photoBox');
    const festival = document.getElementById('festivalBox');
    const product = document.getElementById('productBox');

    let eventType = '';
    if (club_photo.checked) {
        eventType = club_photo.value;
    } else if (festival.checked) {
        eventType = festival.value;
    } else if (product.checked) {
        eventType = product.value;
    }

    const photography = document.getElementById('photographyBox');
    const film = document.getElementById('filmBox');
    const marketing = document.getElementById('marketingBox');
    const other = document.getElementById('otherBox');

    let bookingType = '';
    if (photography.checked) {
        bookingType = photography.value;
    } else if (film.checked) {
        bookingType = film.value;
    } else if (marketing.checked) {
        bookingType = marketing.value;
    } else if (other.checked) {
        bookingType = other.value;
    }

    var data = {
        eventBean: {
            name: eventTitle,
            description: eventDescription,
            start: eventDateTime,
            duration: eventDuration,
            location: eventLocation,
            type: eventType,
            booking_type: bookingType
        }
    }
    $.ajax({
        url: "../api/admin/crewAssignments/{eventId}",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function () {
            alert("Event successfully edited!");
        },
        error: function () {
            alert("Error editing this event!");

        }
    });

}

function deleteEvent(eventID) {

    $.ajax({
        url: `/api/admin/crewAssignments/deleteEvent/${eventID}`,
        method: "DELETE",
        success: function () {
            alert("Successfully deleted this event");
            location.reload();
        },
        error: function () {
            alert("Error deleting this event");
        }

    });
}

function bookingButton() {
    const bookingContainer = document.getElementById("bookingContainer");
    const crewContainer = document.getElementById("crewContainer");

    if (bookingContainer && crewContainer) {
        if (bookingContainer.style.display === 'none') {
            bookingContainer.style.display = 'flex';
            crewContainer.style.display = 'none';
            getAllEvents();
        } else {
            // If the bookingContainer is already visible, do nothing or perform any desired action
        }
    }
}


function crewButton() {
    const bookingContainer = document.getElementById("bookingContainer");
    const crewContainer = document.getElementById("crewContainer");

    if (bookingContainer && crewContainer) {
        if (crewContainer.style.display === 'none') {
            crewContainer.style.display = 'flex';
            bookingContainer.style.display = 'none';
            getAllMembers();
        } else {
        }
    }
}

function confirmationToast(id) {
    let toastDeleteEvent = document.getElementById("deleteEventDialog");
    toastDeleteEvent.showModal();
    let deleteButton = document.getElementById('deleteButton')
    deleteButton.addEventListener('click', function () {
        deleteEvent(id)
    });
}
