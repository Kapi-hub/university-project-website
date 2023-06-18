function saveAnnouncement() {
    const title = document.getElementById('inputTitle').value;
    const descr = document.getElementById('inputDetails').value;
    alert(`${title}\n${descr}`);
    if (!title) {
        alert("Title can't be null");
        return;
    }
    sendHttpRequest("POST", "/api/admin/dashboard/new", {
            title: title,
            body: descr
        }
    ).catch(err => {
        console.log(err)
    }) //TODO figure out what to do when fail
}

function sendHttpRequest(method, url, data) {
    const promise = new Promise(function (resolve, reject) {
            let XHR = new XMLHttpRequest();

            XHR.open(method, url);
            XHR.responseType = 'json';

            XHR.onload = () => {
                if (XHR.status == 200) {
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
    return promise;
}

function getAnnouncements() {
    let announcements = [];
    sendHttpRequest('GET', "/api/admin/announcements").then(responseData => {
        console.log(responseData);
        responseData.forEach(announcement => announcements.push(announcement));
        let o = 0;
        var announcementsList = document.querySelector('.announcementsList');
        var child = document.getElementById("announcementPlaceholder");
        announcementsList.removeChild(child);
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
            titleElement.innerHTML = `<span class="announcementTitle">${announcement.announcement_title}</span>
            <span class="dateTime">${announcement.announcer.forename + " " + formattedDate }</span>`;

            const contentElement = document.createElement('div');
            contentElement.classList.add('content');
            contentElement.innerHTML = `<p class="announcementContent">${announcement.announcement_body}</p>`;

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
        let o = 0;
        var parent = document.getElementById("collapseThree");
        events.forEach(event =>{
        const titleElement = document.createElement('div');
        titleElement.classList.add('accordion-body');
        titleElement.innerHTML = `<button class="latestEvents" >${event.name}</button>`

        parent.appendChild(titleElement);

    })
    }).catch(err => {
        console.log(err);//TODO figure out what to do when fail
    })
}

function getCrewNeeded(){
    let events = []
    sendHttpRequest("Get","/api/admin/crewReq").then(responseData => {
        responseData.events.forEach(event => {
            events.push(event);
        });
        const parent = document.getElementsByClassName('eventsList-item');
        for(let o = 0; o<events.length && o<4;o++ ){
            parent[o].button.textContent = events[o].name;
            span = parent[o].querySelector('accordion-body span');
            span.textContent = events[o].id.toString();
        }
    }
    )
}

