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

    console.log(JSON.stringify(data), null, 2);
    $.ajax({
        url: "../api/admin/crewAssignments",
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
        console.log(permissionType);
    } else if (coreTeam.checked) {
        permissionType = coreTeam.value;
        console.log(permissionType);
    } else if (clubAndCore.checked) {
        permissionType = clubAndCore.value;
        console.log(permissionType);
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
        role: roles,
        team: permissionType
    }

    console.log(JSON.stringify(data));
    $.ajax({
        url: "../api/admin/crewAssignments",
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
    // $.ajax({
    //     url: "../api/admin/crewAssignments/newEvent",
    //     method: "GET",
    //     dataType: "json",
    //     success: function (response) {
    //         console.log(response)
    //         alert("Producers successfully get!")
    //     },
    //     error: function (jqXHR, textStatus, errorThrown) {
    //
    //         console.log("An error occurred:", errorThrown);
    //     }
    // })

    // <li><a className="dropdown-item" href="#" onClick="selectCrewMember('Jack John')">Jack John</a>
    // </li>

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
            producerItem.onclick = function() {
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