<!DOCTYPE html>
<html>
<head>
<title>ScavengeCraft: Dashboard</title>
<link rel="stylesheet" type="text/css" href="/public/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="/public/css/style.css">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body class="soft-white">
<%include file="components/navbar.mako"/>
<main id="dashboard-main" class="padded md-change">
  <%include file="components/trade_sidebar.mako"/>
  <div id="trade-container">
    <div class="flex-container">
      <div id="trade-panel" class="panel panel-default">
        <section>
          <form name="create" action="/create_listing" method="get" role="form" autocomplete="off">
            <div class="form-group">
              <label for="text">Item name:</label>
              <input name="item" type="text" class="form-control" id="item-input" required>
              <div id="item-input-suggest">
              </div>
            </div>
            <div class="form-group">
              <label for="text">Quantity:</label>
              <input name="quantity" type="number" class="form-control" id="quantity-input" required>
            </div>
            <div class="form-group">
              <label for="text">Price per item:</label>
              <input name="price" type="number" min="0.01" step="0.01" class="form-control" id="price-input" required>
            </div>
            <button type="submit" id="submit-btn" class="btn btn-warning btn-wide">Submit</button>
          </form>
        </section>
        <section>
          <h3>Trading Inventory</h3>
            <ul>
            % if data:
              % for entry in data:
                <li>${entry[0].capitalize()} x <span class="quantity">${entry[1]}</span></li>
              % endfor
            % else:
              <li>No items. You can use <strong>/trade</strong> in-game to put items in your online account.</li>
            % endif
            </ul>
        </section>
      </div>
    </div>
  </div>
</main></body>
<script
  src="https://code.jquery.com/jquery-3.1.1.min.js"
  integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
  crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<script>
window.updateHighlight = function() {
  $("#" + window.lastChoice).css("background-color", "#fff");
  $("#" + window.choice).css("background-color", "#f3f3f3");
}
$(function() {
  window.lastChoice = 1;
  window.choice = 1;
  window.choiceLen = 2;
  updateHighlight();
  $("form[name=create]").submit(function(e) {
    e.preventDefault();
    var data = JSON.stringify({
      "display_name": $("#item-input").val(),
      "quantity": $("#quantity-input").val(),
      "price": $("#price-input").val()
    });
    $.ajax({
      type: "POST",
      url: '/trade/trade', 
      data: data,
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function(data) {
        window.location.href = "/trade?alert_message=Trade+successfully+listed!";
      },
      error: function(data) {
        var message = data.responseJSON.message;
        message = '<div class="alert alert-danger alert-dismissable">' +
        '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
        message + '</div>';
        $("form[name=create]").before(message);
      }
    });
  });
  $("#item-input-suggest").hide();
  $("#item-input-suggest").css("visibility", "visible");
  $("#item-input").keydown(function(e) {
    if (e.which === 38) { // up
      window.lastChoice = window.choice;
      if (window.choice > 1)
        window.choice = window.choice - 1;
      updateHighlight();
    } else if (e.which === 40) { // down
      window.lastChoice = window.choice;
      if (window.choice < window.choiceLen)
        window.choice = window.choice + 1;
      updateHighlight();
    }
  });
  $("#item-input").keyup(function(e) {
    if (e.which !== 38 && e.which !== 40) { // up and down
      $.get("http://scavenge.org/item?query=" + $(this).val(), function(data) {
        var items = data.items;
        var string = "";
        for (var i in items) {
          string += '<div class="suggest-choice" id="' + (parseInt(i) + 1) + '">' + items[i].display_name + '</div>';
        }
        $("#item-input-suggest").html(string);
        $(".suggest-choice").mouseenter(function() {
          window.lastChoice = window.choice;
          window.choice = parseInt($(this).attr("id"));
          updateHighlight();
        });
        $(".suggest-choice").mousedown(function() {
          $("#item-input").val($("#" + window.choice).html());
          $("#quantity-input").focus();
        });
        window.choice = 1;
        window.lastChoice = 1;
        window.choiceLen = items.length;
        updateHighlight();
      });
    }
  });
  $("#item-input").keypress(function(e) {
    if (e.which == 13) {
      $("#item-input").val($("#" + window.choice).html());
      $("#quantity-input").focus();
      e.preventDefault();
    }
  });
  $("#item-input").keyup(function(e) {
  });
  $("#item-input").focus(function() {
    $("#item-input-suggest").show();
  });
  $("#item-input").focusout(function() {
    $("#item-input-suggest").hide();
  });
});
</script>

</html>