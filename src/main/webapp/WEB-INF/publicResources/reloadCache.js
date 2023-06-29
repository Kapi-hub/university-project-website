/*
Include this in any sensitive page by using <script src="/static/reloadCache.js></script>
*/
window.onpageshow = function(event) {
        if (event.persisted) {
                window.location.reload();
                    }
};