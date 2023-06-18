function fetchPost(url, data) {
    return fetch(url, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data || {})
    });
}

function setIdAndSecret(adminKey, mailClientId, mailClientSecret) {
    fetchPost('/api/ultra-admin/set-id-and-secret', {
        adminKey: adminKey,
        mailClientId: mailClientId,
        mailClientSecret: mailClientSecret
    }).then(function (response) {
        if (response.ok) {
            alert('Id and secret is set!');
        } else if (response.status === 403) {
            alert('Wrong admin key!');
        } else {
            alert('Something went wrong!');
        }
    });
}

function activateEmail(adminKey, emailToken) {
    fetchPost('/api/ultra-admin/activate-email', {
        adminKey: adminKey,
        emailToken: emailToken
    }).then(function (response) {
        if (response.ok) {
            alert('Email is activated!');
        } else if (response.status === 403) {
            alert('Wrong admin key!');
        } else {
            alert('Something went wrong!');
        }
    });
}

function disableEmail(adminKey) {
    fetchPost('/api/ultra-admin/disable-email', {
        adminKey: adminKey
    }).then(function (response) {
        if (response.ok) {
            alert('Email is disabled!');
        } else if (response.status === 403) {
            alert('Wrong admin key!');
        } else {
            alert('Something went wrong!');
        }
    });
}

function reconnectMail(adminKey) {
    fetchPost('/api/ultra-admin/reconnect-mail', {
        adminKey: adminKey
    }).then(function (response) {
        if (response.ok) {
            alert('Email is reconnected!');
        } else if (response.status === 403) {
            alert('Wrong admin key!');
        } else {
            alert('Something went wrong!');
        }
    });
}

function changeDB(adminKey, dbPassword, previderBool) {
    fetchPost('/api/ultra-admin/change-db', {
        adminKey: adminKey,
        dbPassword: dbPassword,
        previderBool: previderBool
    }).then(function (response) {
        if (response.ok) {
            alert('DB is changed!');
        } else if (response.status === 403) {
            alert('Wrong admin key!');
        } else {
            alert('Something went wrong!');
        }
    });
}

function reconnectDB(adminKey) {
    fetchPost('/api/ultra-admin/reconnect-db', {
        adminKey: adminKey
    }).then(function (response) {
        if (response.ok) {
            alert('DB is reconnected!');
        } else if (response.status === 403) {
            alert('Wrong admin key!');
        } else {
            alert('Something went wrong!');
        }
    });
}

document.addEventListener('DOMContentLoaded', function () {
    const adminKeyInput = document.getElementById('adminKey');
    const emailTokenInput = document.getElementById('emailToken');
    const setIdAndSecretButton = document.getElementById('setIdAndSecret');
    const activateEmailButton = document.getElementById('activateEmail');
    const disableEmailButton = document.getElementById('disableEmail');
    const reconnectMailButton = document.getElementById('reconnectMail');

    const dbPasswordInput = document.getElementById('dbPassword');
    const dbConnectionSelect = document.getElementById('dbConnection');
    const saveDbChangesButton = document.getElementById('saveDbChanges');
    const reconnectDBButton = document.getElementById('reconnectDB');
    const mailClientIdInput = document.getElementById('mailClientId');
    const mailClientSecretInput = document.getElementById('mailClientSecret');

    function checkTokenInputs() {
        activateEmailButton.disabled = !emailTokenInput.value;
    }

    function checkIdAndSecretInputs() {
        setIdAndSecretButton.disabled = !mailClientIdInput.value || !mailClientSecretInput.value;
    }

    emailTokenInput.addEventListener('input', checkTokenInputs);
    mailClientIdInput.addEventListener('input', checkIdAndSecretInputs);
    mailClientSecretInput.addEventListener('input', checkIdAndSecretInputs);

    activateEmailButton.addEventListener('click', function () {
        activateEmail(adminKeyInput.value, emailTokenInput.value);
    });

    disableEmailButton.addEventListener('click', function () {
        disableEmail(adminKeyInput.value);
    });

    reconnectMailButton.addEventListener('click', function () {
        reconnectMail(adminKeyInput.value);
    });

    saveDbChangesButton.addEventListener('click', function () {
        const previderBool = dbConnectionSelect.value === 'previder';
        changeDB(adminKeyInput.value, dbPasswordInput.value, previderBool);
    });

    reconnectDBButton.addEventListener('click', function () {
        reconnectDB(adminKeyInput.value);
    });
});