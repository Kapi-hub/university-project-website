let li_items = document.querySelectorAll(".left_side_bar ul li");

li_items.forEach(function(li_main) {
    li_main.addEventListener("click", function() {
        li_items.forEach(function (li){
            li.classList.remove("active");
        });
        li_main.classList.add("active");
    });
})

