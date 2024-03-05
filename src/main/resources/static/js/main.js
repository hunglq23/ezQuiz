(function () {
  "use strict";

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

  // Scrollable Navbar
  const navbar = document.getElementById("header-nav");
  if (navbar) {
    var body = document.getElementsByTagName("body")[0];

    window.onscroll = () => {
      if (window.scrollY > 0 && navbar) {
        navbar.classList.add("fixed-top", "shadow-sm");
        body.style.paddingTop = navbar.offsetHeight + "px";
      } else {
        navbar.classList.remove("fixed-top", "shadow-sm");
        body.style.paddingTop = "0px";
      }
    };
  }
})();

// Active the tooltips
const tooltipTriggerList = document.querySelectorAll(
  '[data-bs-toggle="tooltip"]'
);
const tooltipList = [...tooltipTriggerList].map(
  (tooltipTriggerEl) => new bootstrap.Tooltip(tooltipTriggerEl)
);

// Login - Register
function togglePasswordVisibility(passwordFieldId) {
  const passwordField = document.getElementById(passwordFieldId);
  const passwordToggleIcon = document.getElementById(`${passwordFieldId}Icon`);

  if (passwordField.type === "password") {
    passwordField.type = "text";
    passwordToggleIcon.classList.remove("fa-eye");
    passwordToggleIcon.classList.add("fa-eye-slash");
  } else {
    passwordField.type = "password";
    passwordToggleIcon.classList.remove("fa-eye-slash");
    passwordToggleIcon.classList.add("fa-eye");
  }
}

(function compareConfirmPassToFirstPass() {
  let firstPass = document.getElementById("password");
  let confirmPass = document.getElementById("passwordConfirm");

  let confirmPassErr = document.getElementById("passwordConfirmError");
  if (firstPass && confirmPass) {
    firstPass.addEventListener("keyup", () => {
      if (firstPass.value !== confirmPass.value) {
        confirmPass.classList.add("is-invalid");
        confirmPassErr.classList.remove("d-none");
        signUpBtn.disabled = true;
      } else {
        confirmPass.classList.remove("is-invalid");
        confirmPassErr.classList.add("d-none");
        signUpBtn.disabled = false;
      }
    });
    confirmPass.addEventListener("keyup", () => {
      if (firstPass.value !== confirmPass.value) {
        confirmPass.classList.add("is-invalid");
        confirmPassErr.classList.remove("d-none");
        signUpBtn.disabled = true;
      } else {
        confirmPass.classList.remove("is-invalid");
        confirmPassErr.classList.add("d-none");
        signUpBtn.disabled = false;
      }
    });
  }
})();

(function handleInputError() {
  const IDs = ["fullName", "email", "password"];
  for (let id of IDs) {
    let input = document.getElementById(id);
    let inputError = document.getElementById(id + "Error");
    if (input) {
      input.addEventListener("keyup", () => {
        input.classList.remove("is-invalid");
        inputError.classList.add("d-none");
      });
    }
  }
})();

function handleSignUpClick() {
  console.log("clicked... ");

  const form = $("#registerForm");

  $.ajax({
    url: form.attr("action"),
    method: "post",
    data: form.serialize(),
    success: () => {
      window.location = "/login?signUpSuccessfully";
    },
    error: (xhr) => {
      if (xhr.status === 400) {
        console.error("showing 400...");
        Object.entries(JSON.parse(xhr.responseText).errors).forEach(
          ([key, value]) => {
            console.log(`${key}: ${value}`);

            $("." + key).each(function () {
              $(this).text(value);
              $(this).removeClass("d-none");
            });

            $("." + key.replace("Error", "Input")).each(function () {
              $(this).addClass("is-invalid");
            });
          }
        );
      } else {
        console.error("Other error, not 400...");
      }
    },
  });
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

function handleFormSubmitByIdAndMethod(formId, onSuccess, method = "post") {
  const form = $("#" + formId);
  if (form === null) {
    console.error("Form is null!");
  } else {
    console.log("Handling submit...");
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
        console.log("Success");
        console.log(resp);
        if (typeof onSuccess === "function") {
          onSuccess();
        }
      },
      error: (xhr, status, err) => {
        if (xhr.status === 400) {
          console.error("showing 400...");
          Object.entries(JSON.parse(xhr.responseText).errors).forEach(
            ([key, value]) => {
              console.log(`${key}: ${value}`);

              $("." + key).each(function () {
                $(this).text(value);
                $(this).removeClass("d-none");
              });

              $("." + key.replace("Error", "Input")).each(function () {
                $(this).addClass("is-invalid");
              });
            }
          );
        } else {
          console.error("Other error, not 400...");
        }
      },
    });
  }
}
