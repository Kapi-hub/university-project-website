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

function setAnnouncements() {
    let announcements = []
    sendHttpRequest('GET', "/api/admin/dashboard/all").then(responseData => {
        console.log(responseData);
        responseData.forEach(announcement => announcements.push(announcement));
        let o = 0;
        var textHolders = document.querySelectorAll('[class^="announcementsContainer"]');
        textHolders.forEach(announcement => {
            console.log(announcement);
            const titleElement = announcement.querySelector(".announcementTitle");
            const body = announcement.querySelector(".content");
            const dateTime = announcement.querySelector(".dateTime");
            const datetemplate = new Date(announcements[o].announcement_timestamp);
            const formattedDate = datetemplate.toLocaleString(undefined, {
                year: 'numeric',
                month: 'numeric',
                day: 'numeric',
                hour: 'numeric',
                minute: 'numeric',
                hour12: true
            });
            console.log(datetemplate);
                console.log(announcements[o].announcement_id);
                titleElement.textContent = announcements[o].announcement_title;
                body.textContent = announcements[o].announcement_body;
                dateTime.textContent = announcements[o].announcer.forename + " " + formattedDate;
                o++;
        })
    }).catch(err => {
        console.log(err);//TODO figure out what to do when fail
    })
}

function setLatestEvents() {
    let events = []
    sendHttpRequest('Get', "/api/admin/dashboard/latest").then(responseData => {
        responseData.events.forEach(event => events.push(event));
        let o = 0;
        var parent = document.getElementById("collapseThree");
        const children = Array.from(parent.children);
        while(o < children.length && o< events.length){
            children[o].span.textContent = events[o].title;
            o++;
        }
    }).catch(err => {
        console.log(err);//TODO figure out what to do when fail
    })
}

function setCrewNeeded(){
    let events = []
    sendHttpRequest("Get","/api/admin/dashboard/crewReq").then(responseData => {
        responseData.events.forEach(event => {
            events.push(event);
        });
        const parent = document.getElementsByClassName('eventsList-item');
        for(let o = 0; o<events.length && o<4;o++ ){
            parent[o].button.textContent = events[o].name;
            span = parent[o].querySelector('accordion-body span');
            span.textContent = events[o].id.toString();
        }
    })
}

