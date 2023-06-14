function addEvent() {

    //get client Information
    let clientQuery = document.querySelector('#clientInfo');
    const clientFirstName = clientQuery.children[1].value;
    const clientLastName = clientQuery.children[4].value;
    const clientEmail = clientQuery.children[7].value;

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


    let eventType = ' ';
    if (clubPhotography.checked === true && festival.checked === false && product.checked === false) {
        eventType = "CLUB_PHOTOGRAPHY";
    } else if (clubPhotography.checked === false && festival.checked === true && product.checked === false) {
        eventType = "FESTIVAL";
    } else if (clubPhotography.checked === false && festival.checked === false && product.checked === true) {
        eventType = "PHOTOSHOOT";
    }
    var data = {
        clientBean: {
            forename: clientFirstName,
            surname: clientLastName,
            email_address: clientEmail
        },
        eventBean: {
            name: eventTitle,
            type: eventType,
            start: eventDateTime,
            duration: eventDuration,
            location: eventLocation
        },

        requiredCrewBean: [
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
                crew_size: eventProducer,
                role: "PRODUCER"
            },
            {
                crew_size: eventVideographer,
                role: "VIDEOGRAPHER"
            }
        ]
    }

    sendHttpRequest("Post", "/api/admin/crewEvents/newEvent", {
        data
    }).catch(err => {
        console.log(err)
    })
//TODO figure out what to do when fail
}
function addCrewMember() {

    //TODO: handle the checkbox - extract the selection
    let memberQuery = document.querySelector('#memberInfo');
    const memberFirstName = memberQuery.children[1].value;
    const memberLastName = memberQuery.children[4].value;
    const memberEmail = memberQuery.children[7].value;
    const memberUsername = memberQuery.children[4].value;

    //get member's permissions
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
        roles = roles + photographer.value + ",";
    }
    if (videographer.checked) {
        roles = roles + videographer.value + ",";
    }
    if (producer.checked) {
        roles = roles + producer.value + ",";
    }
    if (assistant.checked) {
        roles = roles + assistant.value + ",";
    }
    if (dataHandler.checked) {
        roles = roles + dataHandler.value + ",";
    }
    if (planner.checked) {
        roles = roles + planner.value + ",";
    }
    if (editor.checked) {
        roles = roles + editor.value + ",";
    }
    sendHttpRequest("Post", "/api/admin/crewEvents/newMember", {
        firstName: memberFirstName,
        lastName: memberLastName,
        email: memberEmail,
        username: memberUsername,
        permissionType: permissionType,
        role: roles//TODO figure out the sending of the roles
    }).catch(err => {
        console.log(err)
    })//TODO figure out what to do when fail
}