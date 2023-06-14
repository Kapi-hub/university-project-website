function saveAnnouncement() {
    const title = document.getElementById('inputTitle').value;
    const descr = document.getElementById('inputDetail').value;
    if (!title) {
        alert("Title can't be null");
        return;
    }
    sendHttpRequest("Post", "/api/admin/dashboard/new", {
            Title: title,
            Description: descr
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
            XHR.send(Json.stringify(data));
        }
    );
    return promise;
}

function setAnnouncements() {
    let announcements = []
    sendHttpRequest('Get', "/api/admin/dashboard/all").then(responseData => {
        responseData.accouncements.forEach(announcement => announcements.push(announcement));
        let o = 0;
        var textHolders = document.querySelectorAll('[class^="announcement"]');
        textHolders.forEach(announcement => {
                const titleElement = announcement.querySelector(".announcementTitle");
                const body = announcement.querySelector(".dateTime");
                titleElement.textContent = announcement[o].title;
                body.textContent = announcement[o].body;
                o++;
            }
        )
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

