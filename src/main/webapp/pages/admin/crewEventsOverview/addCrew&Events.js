function addEvent() {

    //get client Information
    let clientQuery = document.querySelector('#clientInfo');
    const clientFirstName = clientQuery.children[1].value;
    const clientLastName = clientQuery.children[4].value;
    const clientEmail = clientQuery.children[7].value;
    const clientPhone = clientQuery.children[10].value;

    const eventTitle = document.getElementById('eventTitle').value;
    //get the checkbox elements
    let eventTypeHandlerQuery = document.querySelector("#eventType");
    const clubPhotography = eventTypeHandlerQuery.children[0].children[0].value;
    const festival = eventTypeHandlerQuery.children[1].children[0].value;
    const product = eventTypeHandlerQuery.children[2].children[0].value

    const eventDateTime = document.getElementById("eventDate&Time").value;
    const eventLocation = document.getElementById('eventLocation').value;
    const eventDuration = document.getElementById('eventDuration').value;
    const eventDescription = document.getElementById('eventDescription').value;

    let eventCrewQuery = document.querySelector("#requiredCrew");
    const eventPhotographer = eventCrewQuery.children[1].value;
    const eventAssistant = eventCrewQuery.children[4].value;
    const eventEditor = eventCrewQuery.children[7].value;
    const eventDataHandler = eventCrewQuery.children[10].value;
    const eventPlanner = eventCrewQuery.children[13].value;
    const eventVideographer = eventCrewQuery.children[16].value;


    //TODO: check how to do with the drop down list - how do i extract the name that is selected
    // const eventProducer;

    const XHR = new XMLHttpRequest();
    XHR.open("Post", "/api/admin/crewEvents/newEvent",); //TODO change the URL
    XHR.setRequestHeader("Content-Type", "application/json");
    XHR.withCredentials = true;

    XHR.addEventListener("load", function () {
        if (this.status === 200) {
            console.log(XHR.getAllResponseHeaders())
            if (path == null) {
                window.location.href = XHR.getResponseHeader("Location");
            } else {
                window.location.href = path;
            }
        } else {
            alert("An error occurred: " + this.status);
        }
    });

    let eventType = ' ';
    if (clubPhotography.checked === true && festival.checked === false && product.checked === false) {
        eventType = clubPhotography;
    } else if (clubPhotography.checked === false && festival.checked === true && product.checked === false) {
        eventType = festival;
    } else if (clubPhotography.checked === false && festival.checked === false && product.checked === true) {
        eventType = product;
    }
    XHR.send(JSON.stringify({
        eventTitle: eventTitle,
        eventType: eventType,
        eventDateTime: eventDateTime,
        eventLocation: eventLocation,
        eventDuration: eventDuration,
        eventDescription: eventDescription,
        eventPhotographer: eventPhotographer,
        eventAssistant: eventAssistant,
        eventEditor: eventEditor,
        eventDataHandler: eventDataHandler,
        eventPlanner: eventPlanner,
        eventVideographer: eventVideographer
    }))

    const XHR1 = new XMLHttpRequest();
    XHR1.open("Post", "/api/admin/crewEvents/newEvent",); //TODO change the url
    XHR1.setRequestHeader("Content-Type", "application/json");
    XHR1.withCredentials = true;

    XHR1.addEventListener("load", function () {
        if (this.status === 200) {
            console.log(XHRgetAllResponseHeaders())
            if (path == null) {
                window.location.href = XHR1.getResponseHeader("Location");
            } else {
                window.location.href = path;
            }
        } else {
            alert("An error occurred: " + this.status);
        }
    });

    XHR1.send(JSON.stringify({
        firstName: clientFirstName,
        lastName: clientLastName,
        phone: clientPhone,
        email: clientEmail
    }))
}

function addCrewMember() {

    //TODO: handle the checkbox - extract the selection
    let memberQuery = document.querySelector('#memberInfo');
    const memberFirstName = memberQuery.children[1].value;
    const memberLastName = memberQuery.children[4].value;
    const memberEmail = memberQuery.children[7].value;
    const memberUsername = memberQuery.children[4].value;


    let permissionQuery = document.querySelector('#permissionType');
    const clubTeam = permissionQuery.children[0].children[0].value;
    const coreTeam = permissionQuery.children[1].children[0].value;
    const clubAndCoreTeam = permissionQuery.children[2].children[0].value;

    let permissionType = ' ';
    if (clubTeam.checked === true && coreTeam.checked === false && clubAndCoreTeam.checked === false) {
        permissionType = clubTeam;
    } else if (clubTeam.checked === false && coreTeam.checked === true && clubAndCoreTeam.checked === false) {
        permissionType = coreTeam;
    } else if (clubTeam.checked === false && coreTeam.checked === false && clubAndCoreTeam.checked === true) {
        permissionType = clubAndCoreTeam;
    }

    const XHR1 = new XMLHttpRequest();
    XHR1.open("Post", "/api/admin/crewEvents/newMember",);
    XHR1.setRequestHeader("Content-Type", "application/json");
    XHR1.withCredentials = true;

    XHR1.addEventListener("load", function () {
        if (this.status === 200) {
            console.log(XHR1.getAllResponseHeaders())
            if (path == null) {
                window.location.href = XHR1.getResponseHeader("Location");
            } else {
                window.location.href = path;
            }
        } else {
            alert("An error occurred: " + this.status);
        }
    });

    XHR1.send(JSON.stringify({
        firstName: memberFirstName,
        lastName: memberLastName,
        email: memberEmail,
        username: memberUsername,
        permissionType: permissionType
    }))
}
console.log(document.querySelector('#dropdown-menu').children);