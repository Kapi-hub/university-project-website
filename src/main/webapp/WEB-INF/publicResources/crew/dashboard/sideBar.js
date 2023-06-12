let li_items = document.querySelectorAll("li.sidebar-list-item");
const slider = document.querySelector('.slider');

li_items.forEach(function (li_main) {
    li_main.addEventListener("click", function () {
        li_items.forEach(function (li) {
            li.classList.remove("active");
        });
        li_main.classList.add("sidebar-list-item", "active");
    });
})

li_items.forEach((item, index) => {
    item.addEventListener('click', () => {
        slider.style.transform = `translateY(calc(100% * ${index}))`;
    });
});

window.onload = function () {
    li_items[0].classList.add("active");
}
