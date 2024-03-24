/*<![CDATA[*/
const sort = /*[[${page.sort}]]*/ "";
const pageSize = /*[[${page.size}]]*/ 1;
const page = /*[[${page.page}]]*/ 1;
const search = /*[[${page.search}]]*/ "";
$(document).ready(function () {
  $(".sort-type").each(function () {
    let sortType = $(this).find("span").text();
    $(this).on("click", function () {
      console.log(sortType);
      location.href = `/library?search=${search}&sort=${sortType}&page=${page}&size=${pageSize}`;
    });
  });

  $(".page-index").each(function () {
    const pageIndex = $(this).text();
    if (pageIndex <= 9) {
      $(this).css("padding", "5px 13px");
    } else if (pageIndex > 9) {
      $(this).css("padding", "5px 8px");
    }
    $(this).on("click", function () {
      location.href = `/library?search=${search}&sort=${sort}&page=${pageIndex}&size=${pageSize}`;
    });
  });
});

function prevPage() {
  location.href = `/library?search=${search}&sort=${sort}&page=${
    page - 1
  }&size=${pageSize}`;
}

function nextPage() {
  console.log(page + 1);
  // location.href = `/library?search=${search}&sort=${sort}&page=${
  //   page + 1
  // }&size=${pageSize}`;
}

$(document).ready(function () {
  $("#navSearchInput").on("keydown", function (event) {
    if (event.keyCode === 13) {
      searchLibraryItem();
    }
  });
});

function searchLibraryItem() {
  let term = $("#navSearchInput").val();
  location.href = `/library?search=${term}&sort=${sort}&page=${page}&size=${pageSize}`;
}
/*]]>*/
