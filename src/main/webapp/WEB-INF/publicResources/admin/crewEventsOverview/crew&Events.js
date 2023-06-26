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
            forename: clientFirstName, surname: clientLastName, phone_number: clientPhone, emailAddress: clientEmail
        }, eventBean: {
            name: eventTitle,
            description: eventDescription,
            start: eventDateTime,
            duration: eventDuration,
            location: eventLocation,
            type: eventType,
            booking_type: bookingType
        },

        requiredCrewBeans: [{
            crew_size: eventPhotographer, role: "PHOTOGRAPHER"
        }, {
            crew_size: eventAssistant, role: "ASSISTANT"
        }, {
            crew_size: eventEditor, role: "EDITOR"
        }, {
            crew_size: eventDataHandler, role: "DATA_HANDLER"
        }, {
            crew_size: eventPlanner, role: "PLANNER"
        }, {
            crew_size: 0, role: "PRODUCER"
        }, {
            crew_size: eventVideographer, role: "VIDEOGRAPHER"
        }]


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
            //id=`${producer.id}` onclick="function(`+id+`)
            producerItem.innerHTML = `<span class="producerName">${producer.forename} ${producer.surname}</span>`;

            producerItem.appendChild(buttonElement);
            producersList.appendChild(producerItem);
        })
    });
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
    });
}

function selectTeam(name) {
    const selectedTeam = document.getElementById('selectedTeam');
    selectedTeam.textContent = name;
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
            console.log("response " + responseData);
            responseData.forEach((member) => {
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
                crewName.innerHTML = `<ion-icon name="person-outline"></ion-icon> <p>${forename} ${surname}</p>`;

                let crewEmail = document.createElement("div");
                crewEmail.setAttribute('class', 'crew-email');
                crewEmail.innerHTML = `<ion-icon name="mail-outline"></ion-icon>
                                        <p>${mail}</p>`;

                let crewUsername = document.createElement("div");
                crewUsername.setAttribute('class', 'crew-username');
                crewUsername.innerHTML = `<ion-icon name="id-card-outline"></ion-icon>
                                            <p>${username}</p>`;

                let crewTeam = document.createElement("div");
                crewTeam.setAttribute('class', 'crew-team');
                crewTeam.innerHTML = `<ion-icon name="people-circle-outline"></ion-icon>
                                        <p>${team}</p> <button class="crew-info-edit-button" id = "crew-info-edit-button" onclick="showChangeTeamModal(${id})" ><ion-icon name="pencil-outline"></ion-icon></button>`
                // <script>const triggerButton = document.getElementById("crew-info-edit-button");
                //     triggerButton.addEventListener("click", showChangeTeamModal);</script>`

                let crewRole = document.createElement("div");
                crewRole.setAttribute('class', 'crew-role');
                crewRole.innerHTML = `<ion-icon name="pricetag-outline"></ion-icon>
                                        <p>${role}</p> <button class="crew-info-edit-button"><ion-icon name="pencil-outline"></ion-icon></button>`;

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

function changeTeam(memberID) {
    let team;
    const modal = document.createElement("div");
    modal.setAttribute("class", "modal fade");
    modal.setAttribute("id", "changeTeam");
    modal.setAttribute("tabindex", "-1");
    modal.setAttribute("aria-labelledby", "exampleModalLabel");
    modal.setAttribute("aria-hidden", "true");

    const modalDialog = document.createElement("div");
    modalDialog.setAttribute("class", "modal-dialog modal-dialog-centered");

    const modalContent = document.createElement("div");
    modalContent.setAttribute("class", "modal-content");

    const modalHeader = document.createElement("div");
    modalHeader.setAttribute("class", "modal-header");
    const modalTitle = document.createElement("h5");
    modalTitle.setAttribute("class", "modal-title");
    modalTitle.innerHTML = `<span>Change Team</span>`;
    modalHeader.appendChild(modalTitle);

    const modalBody = document.createElement("div");
    modalBody.setAttribute("class", "modal-body");
    modalBody.textContent = "Select the new team";


    const dropdown = document.createElement("div");
    dropdown.setAttribute("class", "dropdown");

    const dropdownButton = document.createElement("button");
    dropdownButton.setAttribute("class", "btn btn-secondary dropdown-toggle");
    dropdownButton.setAttribute("type", "button");
    dropdownButton.setAttribute("data-bs-toggle", "dropdown");
    dropdownButton.setAttribute("aria-expanded", "false");
    dropdownButton.innerHTML = `<span id="selectedTeam">select team</span>`;

    const teamsList = document.createElement('ul');
    teamsList.setAttribute("class", "dropdown-menu");

    const coreItem = document.createElement('li');
    const coreButton = document.createElement('a');
    coreButton.setAttribute("class", "dropdown-item");
    coreButton.setAttribute("href", "#");
    coreButton.textContent = "core";
    coreButton.onclick = function () {
        selectTeam("core");
        team = coreButton.textContent;
        console.log(team);
    }
    coreItem.appendChild(coreButton);
    teamsList.appendChild(coreItem);

    const clubItem = document.createElement('li');
    const clubButton = document.createElement('a');
    clubButton.setAttribute("class", "dropdown-item");
    clubButton.setAttribute("href", "#");
    clubButton.textContent = "club";
    clubButton.onclick = function () {
        selectTeam("club");
        team = clubButton.textContent;
        console.log(team);
    }
    clubItem.appendChild(clubButton);
    teamsList.appendChild(clubItem);

    const bothItem = document.createElement('li');
    const bothButton = document.createElement('a');
    bothButton.setAttribute("class", "dropdown-item");
    bothButton.setAttribute("href", "#");
    bothButton.textContent = "core&club";
    bothButton.onclick = function () {
        selectTeam("core&club");
        team = bothItem.textContent;
    }
    bothItem.appendChild(bothButton);
    teamsList.appendChild(bothItem);


    dropdown.appendChild(dropdownButton);
    dropdown.appendChild(teamsList);

    console.log("team selected " + team);
    const modalFooter = document.createElement("div");
    modalFooter.setAttribute("class", "modal-footer");
    modalFooter.innerHTML = `<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <button type="button" class="btn btn-primary" onclick="sendNewTeamToDB(${memberID}, ${team})">Save changes</button>`;

    modalContent.appendChild(modalHeader);
    modalContent.appendChild(modalBody);
    modalContent.appendChild(modalFooter);
    modalBody.appendChild(dropdown);
    dropdown.appendChild(teamsList);

    modalDialog.appendChild(modalContent);
    modal.appendChild(modalDialog);
    document.body.appendChild(modal);


}


function sendNewTeamToDB(memberID, team) {
    console.log(team)
    $.ajax(
        {
            url: `api/admin/crewAssignments/changeTeam/${memberID}/${team}`,
            method: "PUT",
            success: function () {
                alert("Successfully changed the team");
                // location.reload();
            }, error: function () {
                alert("Error changing the team");
            }
        }
    )
}

function showChangeTeamModal(id) {
    changeTeam(id);
    const modalElement = document.getElementById("changeTeam");
    const bootstrapModal = new bootstrap.Modal(modalElement);
    console.log("sunta aici in showchangemodal");
    console.log(modalElement);
    bootstrapModal.show();
}

function changeRole(memberID) {

}

function getAllEvents() {
    let events = [];
    sendHttpRequest('GET', "/api/admin/crewAssignments/bookings")
        .then(responseData => {
            responseData.forEach(event => events.push(event));
            responseData.forEach((event) => {
                const {
                    id, name, description, start, duration, location, type, booking_type
                } = event.eventDetails;

                const {
                    forename, surname, emailAddress, phone_number
                } = event.eventDetails.clients[0]

                const container = document.querySelector('.container.content-container');

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
                eventDuration.innerHTML = `<ion-icon name="time-outline"></ion-icon> <span>${duration} hours</span>`;

                let eventLocation = document.createElement("li");
                eventLocation.setAttribute('class', 'location');
                eventLocation.innerHTML = `<ion-icon name="pin-outline"></ion-icon> <span>${location}</span>`;

                let eventDescription = document.createElement("li");
                eventDescription.setAttribute('class', 'location');
                eventDescription.innerHTML = `<ion-icon name="document-outline"></ion-icon> <span>${description}</span>`;

                detailsList.appendChild(dateTime);
                detailsList.appendChild(eventLocation);
                detailsList.appendChild(eventDuration);
                detailsList.appendChild(eventDescription);
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
                if (booking_type === 'photography') {
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
    sendHttpRequest('GET', `/api/admin/crewAssignments/changeEvent/${eventID}`)
        .then(responseData => {
            console.log(responseData);
            const event = {
                id, name, description, start, duration, location, type, booking_type
            } = responseData[0]
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
        url: `/api/admin/crewAssignments/deleteEvent/${eventID}`, method: "DELETE", success: function () {
            alert("Successfully deleted this event");
            location.reload();
        }, error: function () {
            alert("Error deleting this event");
        }
    });
}

function bookingButton() {
    const bookingContainer = document.getElementById("bookingContainer");
    const crewContainer = document.getElementById("crewContainer");
    // this is to fix the bug where if you go two times on the events
    // or crew members, the # of events or crew members would fetch again
    let firstLoad = 0;

    if (bookingContainer && crewContainer && firstLoad === 0) {
        if (bookingContainer.style.display === 'none') {
            firstLoad += 1;
            bookingContainer.style.display = "flex";
            crewContainer.style.display = "none";
            getAllEvents();
        }
    }
}


function crewButton() {
    const bookingContainer = document.getElementById("bookingContainer");
    const crewContainer = document.getElementById("crewContainer");
    let firstLoad = 0;

    if (bookingContainer && crewContainer && firstLoad === 0) {
        if (crewContainer.style.display === 'none') {
            firstLoad++;
            crewContainer.style.display = "flex";
            bookingContainer.style.display = "none";
            getAllMembers();
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
