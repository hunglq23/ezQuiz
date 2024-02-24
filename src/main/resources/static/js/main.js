(function () {
  "use strict";

  /**
   * Navbar effects and scrolltop buttons upon scrolling
   */
  const navbar = document.getElementById("header-nav");
  var body = document.getElementsByTagName("body")[0];
  const scrollTop = document.getElementById("scrolltop");
  window.onscroll = () => {
    if (window.scrollY > 0) {
      navbar.classList.add("fixed-top", "shadow-sm");
      // body.style.paddingTop = navbar.offsetHeight + "px";
      // scrollTop.style.visibility = "visible";
      // scrollTop.style.opacity = 1;
    } else {
      navbar.classList.remove("fixed-top", "shadow-sm");
      // body.style.paddingTop = "0px";
      // scrollTop.style.visibility = "hidden";
      // scrollTop.style.opacity = 0;
    }
  };

  /**
   * Masonry Grid
   */
  var elem = document.querySelector(".grid");
  if (elem) {
    imagesLoaded(elem, function () {
      new Masonry(elem, {
        itemSelector: ".grid-item",
        percentPosition: true,
        horizontalOrder: true,
      });
    });
  }

  /**
   * Big Picture Popup for images and videos
   */
  document.querySelectorAll("[data-bigpicture]").forEach(function (e) {
    e.addEventListener("click", function (t) {
      t.preventDefault();
      const data = JSON.parse(e.dataset.bigpicture);
      BigPicture({
        el: t.target,
        ...data,
      });
    });
  });

  /**
   * Big Picture Popup for Photo Gallary
   */
  document.querySelectorAll(".bp-gallery a").forEach(function (e) {
    var caption = e.querySelector("figcaption");
    var img = e.querySelector("img");
    // set the link present on the item to the caption in full view
    img.dataset.caption =
      '<a class="link-light" target="_blank" href="' +
      e.href +
      '">' +
      caption.innerHTML +
      "</a>";
    window.console.log(caption, img);
    e.addEventListener("click", function (t) {
      t.preventDefault();
      BigPicture({
        el: t.target,
        gallery: ".bp-gallery",
      });
    });
  });

  // Add your javascript here
})();

/**
 * Function to toggle password visibility
 * @param {string} passwordFieldId - The ID of the password input field
 */
function togglePasswordVisibility(passwordFieldId) {
  const passwordField = document.getElementById(passwordFieldId);
  const passwordToggleIcon = document.getElementById(
    `${passwordFieldId}-toggle-icon`
  );

  if (
    passwordField.type === "password" ||
    passwordField.type === "passwordConfirm"
  ) {
    passwordField.type = "text";
    passwordToggleIcon.classList.remove("fa-eye");
    passwordToggleIcon.classList.add("fa-eye-slash");
  } else {
    passwordField.type = "password";
    passwordToggleIcon.classList.remove("fa-eye-slash");
    passwordToggleIcon.classList.add("fa-eye");
  }
}

// Suggestion for Nav Search
const navSearchInput = document.getElementById("navSearchInput");
const navSuggestionList = document.querySelector(".suggestions-list");

if (navSearchInput !== null) {
  navSearchInput.addEventListener("input", () => {
    // Cập nhật danh sách gợi ý dựa trên nội dung nhập của người dùng
    // ...

    navSuggestionList.style.display = "block";
  });
  document.addEventListener("click", (event) => {
    if (!event.target.closest(".suggestions-list")) {
      navSuggestionList.style.display = "none";
    }
  });
}

function handleFormSubmitByIdAndMethod(formId, method = "post") {
  const form = $("#" + formId);
  if (form === null) {
    console.error("Form is null!");
  } else {
    console.log("Handling submit...");
    console.log(form);
    const formData = new FormData(form[0]);

    $.ajax({
      url: form.attr("action"),
      method: method,
      data: formData,
      cache: false,
      contentType: false,
      processData: false,
      dataType: "json",
      success: (resp) => {
        console.log("success");
        location.reload();
      },
      error: (xhr, status, err) => {
        if (xhr.status === 400) {
          console.error("showing 400...");
          const errors = JSON.parse(xhr.responseText).errors;
          Object.entries(errors).forEach(([key, value]) => {
            console.log(`${key}: ${value}`);
            var errorId = "#" + key;
            $(errorId + "Msg").text(value);
            $(errorId).removeClass("d-none");
          });
        } else {
          console.error("Other error, not 400...");
          console.log(xhr.status);
        }
      },
    });
  }
}
