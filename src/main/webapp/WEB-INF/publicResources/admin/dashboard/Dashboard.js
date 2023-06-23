 function saveAnnouncement() {
        const title = document.getElementById('inputTitle').value;
        const descr = document.getElementById('inputDetails').value;
        let recipient = document.getElementById('inputRecipients').value;
        if (!title) {
            alert("Title can't be null");
            return;
        }
        var parsed= parseInt(recipient);

        if (isNaN(parsed)){
            parsed = null;
        }

        console.log("this is the parsed data : " + parsed);
        $.ajax({
            url: "/api/admin/newAnnouncement",
            type: "POST",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                title: title,
                body: descr,
                recipient: parsed
            }),
            success: function () {
                console.log("Announcement saved successfully");
                getAnnouncements();
            },
            error: function () {
                console.log("Error:", error);
            }
        });
    }

function escapeHtml(unsafe) {
    return unsafe.replace(/[&<"'>]/g, function(match) {
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
        console.log(responseData);
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

function getLatestEvents() {
    let events = []
    sendHttpRequest('Get', "/api/admin/events").then(responseData => {
        console.log(responseData);
        responseData.forEach(event => events.push(event));
        var parent = document.getElementById("collapseThree");
        events.forEach(event =>{
        const titleElement = document.createElement('div');
        titleElement.classList.add('accordion-body');
        titleElement.innerHTML = `<button class="latestEvents" >${escapeHtml(event.name)}</button>`

        parent.appendChild(titleElement);

    })
    }).catch(err => {
        console.log(err);//TODO figure out what to do when fail
    })
}

function assignCrew(){
    var CrewAssigned = document.getElementById().textContent;
    $.ajax({
        url: "/api/admin/assignCrew/",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(formData),
        }
    )

}

function getCrewNeeded() {
    $.ajax({
        url: "/api/admin/crewReq",
        method: "GET"
    }).done(function (responseData) {
        let events = JSON.parse(responseData);
        console.log(responseData);
        console.log(typeof responseData);
        var parent = document.getElementById("collapseOne");
        events.forEach(event =>{
            const titleElement = document.createElement('div');
            titleElement.classList.add('accordion-body');
            titleElement.innerHTML = `<button class="latestEvents" >${escapeHtml(event.event_title)}</button>`

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
        console.log(responseData);
        var welcome = document.getElementById("welcome");
        welcome.textContent = "Welcome, " + responseData;
    }).fail(function (err) {
        console.log(err);
    })

}

