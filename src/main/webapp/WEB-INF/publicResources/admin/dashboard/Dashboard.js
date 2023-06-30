var clickedEvent;
function closeModal() {
    document.getElementById("postEventsModal").style.display = "none";
}
function closeAModal(){
    document.getElementById("acceptEvent").style.display = "none";
}
function saveAnnouncement() {
        const title = document.getElementById('inputTitle').value;
        const descr = document.getElementById('inputDetails').value;
        let recipient = document.getElementById('inputID').value;
        let urgency = document.getElementById("myCheckbox");
        if (urgency.checked){
            urgency = true;
        }else urgency =null ;
        if (!title) {
            alert("Title can't be null");
            return;
        }
        var parsed= parseInt(recipient);

        if (isNaN(parsed)){
            parsed = null;
        }else{

        }

        $.ajax({
            url: "/api/admin/newAnnouncement",
            type: "POST",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                title: title,
                body: descr,
                recipient: parsed,
                urgent: urgency
            }),
            success: function () {
                getAnnouncements();
                $('#liveToast').toast('show');
                $('#liveToast').toast('autohide(true)');


            },
            error: function (jqXHR, textStatus) {
                console.log("Error:", textStatus , jqXHR);
                alert("An error occurred whilst making this Announcement, please check your information");
                getAnnouncements();
            }
        });
    }

function escapeHtml(unsafe) {
    var safe = String(unsafe); // Convert input to a string
    return safe.replace(/[&<"'>]/g, function(match) {
        switch (match) {
            case '&':
                return '&amp;';
            case '<':
                return '&lt;';
            case '>':
                return '&gt;';
            case '"':
                return '&quot;';
            case "'":
                return '&#039;';
            default:
                return match;
        }
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
        }
    );
}

function getAnnouncements() {
    let announcements = [];
    sendHttpRequest('GET', "/api/admin/announcements").then(responseData => {
        responseData.forEach(announcement => announcements.push(announcement));
        const announcementsList = document.querySelector('.announcementsList');
        announcementsList.innerHTML= '';
        announcements.forEach(announcement =>{
            const datetemplate = new Date(announcement.announcement_timestamp);
            const formattedDate = datetemplate.toLocaleString(undefined, {
                year: 'numeric',
                month: 'numeric',
                day: 'numeric',
                hour: 'numeric',
                minute: 'numeric',
                hour12: true
            });

            const announcementItem = document.createElement('div');
            announcementItem.classList.add('announcementItem');

            const titleElement = document.createElement('div');
            titleElement.classList.add('title');
            titleElement.innerHTML = `<span class="announcementTitle">${escapeHtml(announcement.announcement_title)}</span>
            <span class="dateTime">${escapeHtml(announcement.announcer.forename) + " " + formattedDate }</span>`;
            if (announcement.urgency)
                titleElement.style.color ='rgb(255, 0, 0, 0.5)';


            const contentElement = document.createElement('div');
            contentElement.classList.add('content');
            contentElement.innerHTML = `<p class="announcementContent">${escapeHtml(announcement.announcement_body)}</p>`;

            if (announcement.recipient===null){}
            else {
                contentElement.innerText = contentElement.innerText + " -" + 'Sent to id: ' + announcement.recipient;
            }

            announcementItem.appendChild(titleElement);
            announcementItem.appendChild(contentElement);


            announcementsList.appendChild(announcementItem);

        });
    }).catch(err => {
        console.log(err);//TODO figure out what to do when fail
    })
}

function getIncomingEvents() {
    let events = []
    sendHttpRequest('Get', "/api/admin/events").then(responseData => {
        responseData.forEach(event => events.push(event));
        var parent = document.getElementById("collapseTwo");
        parent.innerText= '';
        events.forEach(event =>{
        const titleElement = document.createElement('div');
        titleElement.classList.add('accordion-body');
        titleElement.innerHTML = `<button class="latestEvents" onclick="loadAcceptation(${event.id})" >${escapeHtml(event.name)}</button>`

        parent.appendChild(titleElement);

    })
    }).catch(err => {
        console.log(err);//TODO figure out what to do when fail
    })
}
function loadCrewEnrolled(clickedEvent) {
    const crewList = document.getElementById("addedCrew")
    crewList.innerText= "";
    crewList.innerHTML= "<span> Added Crew:</span><br>"
    $.ajax({
        url:`/api/admin/enrolledCrew/${clickedEvent}`,
        type:"GET",
        dataType: "JSON",
        success: function (responseData){
            responseData.forEach(crew => {
                let crewItem = document.createElement("span");
                crewItem.innerText = `${crew.name} ${crew.lastName} (${crew.role})`;

                let lineBreak = document.createElement("br");
                crewItem.appendChild(lineBreak);

                crewList.appendChild(crewItem);
            })
        }
    })
}
function loadRequiredCrew(clickedEvent) {
    $.ajax({
        url:`/api/admin/reqCrew/${clickedEvent}`,
        type:"GET",
        dataType:"JSON",
        success: function(responseData){
            const eventAssist = document.getElementById("eventAssistant");
            const eventPhotog = document.getElementById("eventPhotographer");
            const eventEdit = document.getElementById("eventEditor");
            const eventData= document.getElementById("eventDataHandler");
            const eventPlan = document.getElementById("eventPlanner");
            const eventVideo = document.getElementById("eventVideographer");

            eventAssist.innerText = "Assistant: " + responseData[0].assistant;
            eventPhotog.innerText = "Photographer: " + responseData[0].photographer;
            eventEdit.innerText = "Editor: " + responseData[0].editor;
            eventData.innerText = "Data Handler: " + responseData[0].data_handler;
            eventPlan.innerText = "Event Planner: " + responseData[0].planner;
            eventVideo.innerText = "Videographer: " + responseData[0].videographer;


        }
    })
}
function loadEvents(id) {
    clickedEvent = id;
    let crewlist = document.getElementById("addedCrew");
    $.ajax({
        url: `/api/admin/event/${clickedEvent}`,
        type:"GET",
        dataType: "JSON",
        success: function(responseData){
            const eventTitle = document.getElementById('eventTitle');
            const eventDescription = document.getElementById('eventDescription');
            const eventDateTime = document.getElementById("eventDateTime");
            const eventLocation = document.getElementById('eventLocation');
            const eventDuration = document.getElementById('eventDuration');
            const bookingType = document.getElementById('bookingType');
            const eventType = document.getElementById("eventType");
            const datetemplate = new Date(responseData.eventDateTime);
            const formattedDate = datetemplate.toLocaleString(undefined, {
                year: 'numeric',
                month: 'numeric',
                day: 'numeric',
                hour: 'numeric',
                minute: 'numeric',
                hour12: true
            });
            eventTitle.innerText = "Event Title: " + responseData.eventTitle;
            eventDescription.innerText = "Event Description: " + responseData.eventDescription;
            eventDateTime.innerText = "Event Date & Time: " + formattedDate;
            eventLocation.innerText = "Event Location: " + responseData.eventLocation;
            eventDuration.innerText = "Event Duration: " + responseData.eventDuration;
            eventType.innerText = "Event Type: " + responseData.eventType;
            bookingType.innerText = "Booking Type: " + responseData.bookingType;
            loadRequiredCrew(clickedEvent);
            loadCrewEnrolled(clickedEvent);
        }
    })

    document.getElementById("postEventsModal").style.display = "block";
    document.getElementById("postEventsModal").classList.add("show");
}

function assignCrew(id) {
    var payload = {
        crewMember: {
            id: id
        },
        event: {
            id: clickedEvent
        }
    };

    $.ajax({
        url: "/api/event/force-enroll",
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            'Accept': 'application/json'
        },
        data: JSON.stringify(payload),
        dataType: "text",
        success: function(data,textStatus ,xhr) {
            console.log(xhr.status);
                if (xhr.status === 200) {
                    // Enrolment successful
                    alert("Enrolment successful");
                    // Handle any additional logic or UI updates
                }else if (xhr.status === 304) {
                    // Other error status or response is missing
                    alert("Error in enrolment");
                    // Handle any additional logic or UI updates
                }
            else {
                // Response is undefined
                alert("Something went wrong please try again later");
                // Handle any additional logic or UI updates
            }
            loadCrewEnrolled(clickedEvent);
        },
        error: function(jqXHR, textStatus) {
            console.log("Error:", textStatus, jqXHR);
            // Handle the error case
        }
    });
}

function getCrew(){
    var createCrew = document.getElementById("ddCrew");
    createCrew.textContent= "";
    $.ajax({
        url: "/api/admin/crewAssignments/members/",
        method: "GET",
        dataType: "JSON",
        success: function (responseData){
            responseData.forEach(crew => {
                const crewItem = document.createElement('li');
                const buttonElement = document.createElement('a');
                crewItem.classList.add('dropdown-item');
                crewItem.id = `${escapeHtml(crew.id)}`;
                crewItem.innerHTML = `<span class="producerName" onclick="assignCrew(${escapeHtml(crew.id)})">${escapeHtml(crew.forename)} ${escapeHtml(crew.surname)} (${escapeHtml(crew.role)})</span>`;

                crewItem.appendChild(buttonElement);
                createCrew.appendChild(crewItem);
            })
        },
        err: function (jqXHR, textStatus) {
            console.log("Error:", textStatus , jqXHR);
        },
    })
}

function getEventsCrewNeeded() {
    $.ajax({
        url: "/api/admin/crewReq",
        method: "GET"
    }).done(function (responseData) {
        let events = JSON.parse(responseData);
        var parent = document.getElementById("collapseOne");
        parent.innerText= '';
        events.forEach(event =>{
            const titleElement = document.createElement('div');
            titleElement.classList.add('accordion-body');
            titleElement.innerHTML = `<button class="latestEvents"  id="${escapeHtml(event.event_id)}"  onclick="loadEvents(${escapeHtml(event.event_id)})" >${escapeHtml(event.event_title)}</button>`

            parent.appendChild(titleElement);
        })
    }).fail(function (err) {
        console.log(err);
    })
}

function getUser(){
    $.ajax({
        url: "/api/admin/user",
        method: "GET"
    }).done(function (responseData){
        console.log(responseData)
        var welcome = document.getElementById("welcome");
        welcome.textContent = "Welcome, " + responseData;
    }).fail(function (err) {
        console.log(err);
    })

}
function loadAcceptation(id){
    clickedEvent = id;

    function loadARequiredCrew(id) {
        console.log(id);
        clickedEvent =id;
        document.getElementById("rejectBtn").onclick = function() {
            reject(id);
            closeAModal();
        };

        document.getElementById("saveBTN").onclick = function() {
            acceptE(id);
            closeAModal();
        };
        $.ajax({
            url:`/api/admin/reqCrew/${clickedEvent}`,
            type:"GET",
            dataType:"JSON",
            success: function(responseData){
                console.log(responseData);
                const eventAssist = document.getElementById("eventAAssistant");
                const eventPhotog = document.getElementById("eventAPhotographer");
                const eventEdit = document.getElementById("eventAEditor");
                const eventData= document.getElementById("eventADataHandler");
                const eventPlan = document.getElementById("eventAPlanner");
                const eventVideo = document.getElementById("eventAVideographer");

                eventAssist.innerText = "Assistant: " + responseData[0].assistant;
                eventPhotog.innerText = "Photographer: " + responseData[0].photographer;
                eventEdit.innerText = "Editor: " + responseData[0].editor;
                eventData.innerText = "Data Handler: " + responseData[0].data_handler;
                eventPlan.innerText = "Event Planner: " + responseData[0].planner;
                eventVideo.innerText = "Videographer: " + responseData[0].videographer;


            }
        })
    }
        $.ajax({
        url: `/api/admin/event/${clickedEvent}`,
        type:"GET",
        dataType: "JSON",
        success: function(responseData){
            console.log(responseData);
            const eventTitle = document.getElementById('eventATitle');
            const eventDescription = document.getElementById('eventADescription');
            const eventDateTime = document.getElementById("eventADateTime");
            const eventLocation = document.getElementById('eventALocation');
            const eventDuration = document.getElementById('eventADuration');
            const bookingType = document.getElementById('bookingAType');
            const eventType = document.getElementById("eventAType");
            const datetemplate = new Date(responseData.eventDateTime);
            const formattedDate = datetemplate.toLocaleString(undefined, {
                year: 'numeric',
                month: 'numeric',
                day: 'numeric',
                hour: 'numeric',
                minute: 'numeric',
                hour12: true
            });
            eventTitle.innerText = "Event Title: " + responseData.eventTitle;
            eventDescription.innerText = "Event Description: " + responseData.eventDescription;
            eventDateTime.innerText = "Event Date & Time: " + formattedDate;
            eventLocation.innerText = "Event Location: " + responseData.eventLocation;
            eventDuration.innerText = "Event Duration: " + responseData.eventDuration;
            eventType.innerText = "Event Type: " + responseData.eventType;
            bookingType.innerText = "Booking Type: " + responseData.bookingType;
            loadARequiredCrew(clickedEvent);
        }
    })
    document.getElementById("acceptEvent").style.display = "block";
    document.getElementById("acceptEvent").classList.add("show");
}
function acceptE(){
    $.ajax({
        url:`/api/admin/accept/${clickedEvent}`,
        type:"PUT",
        success : function(){
            getEventsCrewNeeded();
            getIncomingEvents();

        }
    })
}
function reject(){
    $.ajax({
        url:`/api/admin/delete/${clickedEvent}`,
        type:"DELETE",
        success : function(){
            getEventsCrewNeeded();
            getIncomingEvents();
        }

    })
}


