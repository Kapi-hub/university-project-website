let li_items = document.querySelectorAll(".left_side_bar ul li");

//highlight current month on page load
li_items.item(new Date().getMonth()).classList.add("active");