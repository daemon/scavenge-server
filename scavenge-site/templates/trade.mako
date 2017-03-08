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
  % if data["alert_message"]:
    <div class="alert alert-success alert-dismissable fade in">
      <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
      ${data["alert_message"]}
    </div>
  % endif
    <div class="flex-container">
      <div id="server-status" class="panel panel-default">
        <div class="panel-body">
         <div class="row">
          <div class="col-xs-5"><button onclick="window.location.href='/create_listing'" type="button" class="btn btn-warning">Sell items <span class="glyphicon glyphicon-chevron-right"></span></button></div>
        </div><br>
        <div class="input-group col-xs-12">
          <input id="item-input" name="item" type="text" class="form-control" placeholder="Search for..." autocomplete="off" 
          % if data["query"]:
            value="${data['query']}"
          % endif
          >
          <span class="input-group-btn">
            <button onclick="window.location.href = '/trade?query=' + $('#item-input').val()" class="btn btn-default" type="button">Go</button>
          </span>
        </div>
        <div id="item-input-suggest"></div>
        <br>
        <div class="row">
          <div class="col-xs-12">
            % if data["query"]:
              <a href="/trade?page_no=${data['page_no'] - 1}&query=${data['query']}">previous</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              % if data["trades"]:
                <a class="right-ideas" href="/trade?page_no=${data['page_no'] + 1}&query=${data['query']}">next</a>
              % endif
            % else:
              <a href="/trade?page_no=${data['page_no'] - 1}">previous</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              % if data["trades"]:
                <a class="right-ideas" href="/trade?page_no=${data['page_no'] + 1}">next</a>
              % endif
            % endif
          </div>
        </div>
        % if data["trades"]:
        <div id="fp-listing-container">
        % for trade, i in zip(data["trades"], range(len(data["trades"]))):
        % if i % 2 == 0:
          <div class="fp-listing gray">
        % else:
          <div class="fp-listing">
        % endif
            <div class="fp-listing-item">${trade["item_name"]} <span class="quantity">(${trade["trade"].quantity} left)</span></div>
            <div class="fp-listing-price">$${trade["trade"].price} each</div>
            <div class="fp-listing-username">@${trade["username"]}</div>
            <div class="fp-listing-buy"><button quantity="${trade['trade'].quantity}" price-per-item="${trade['trade'].price}" trade-id="${trade['trade'].id}" item-name="${trade['item_name']}" class="btn btn-success btn-buy btn-sm" type="button">Buy</button></div>
          </div>
        % endfor
        </div>
        <div class="row">
          <div class="col-xs-12">
            % if data["query"]:
              <a href="/trade?page_no=${data['page_no'] - 1}&query=${data['query']}">previous</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="right-ideas" href="/trade?page_no=${data['page_no'] + 1}&query=${data['query']}">next</a>
            % else:
              <a href="/trade?page_no=${data['page_no'] - 1}">previous</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="right-ideas" href="/trade?page_no=${data['page_no'] + 1}">next</a>
            % endif
          </div>
        </div>
        </div>
        % else:
        <h3>No trades on this page!</h3>
        % endif
      </div>
    </div>
    </div>
</main>

<div id="buy-modal" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header modal-header-bar">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Buy items</h4>
      </div>
      <div class="modal-body">
        <form action="javascript:void(0);" name="create" role="form">
          <div class="form-group">
            <label for="text">Item:</label>
            <input type="text" class="form-control" id="item-name" value="" disabled>
          </div>
          <div class="form-group">
            <label for="pwd">Quantity:</label>
            <input type="number" class="form-control" id="item-quantity">
          </div>
          <div class="form-group">
            <label for="pwd">Total cost:</label>
            <input type="number" step="0.01" class="form-control" id="item-cost">
          </div>
        </form>
        <h4>You'll be charged $<strong><span id="calc-cost">0</span></strong> for this purchase!</h4>
      </div>
      <div class="modal-footer">
        <button onclick="doBuySubmit()" id="btn-buy-submit" type="button" class="btn btn-success btn-wide btn-default">Buy</button>
      </div>
    </div>
  </div>
</div>

<script
  src="https://code.jquery.com/jquery-3.1.1.min.js"
  integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
  crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<script>
window.updateHighlight = function() {
  $("#" + window.lastChoice).css("background-color", "#fff");
  $("#" + window.choice).css("background-color", "#f3f3f3");
};

window.clearAlerts = function() {
  $(".alert").remove();
};

window.doBuySubmit = function() {
  clearAlerts();
  var data = JSON.stringify({
    "trade_id": $("#btn-buy-submit").attr("trade-id"),
    "quantity": $("#item-quantity").val()
  });
  $.ajax({
    type: "POST",
    url: '/trade/execute',
    data: data,
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function(data) {
      clearAlerts();
      var message = "You've successfully bought those items!";
      message = '<div class="alert alert-success alert-dismissable">' +
      '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
      message + '</div>';
      $(".flex-container").before(message);
      $('#buy-modal').modal('hide');
    },
    error: function(data) {
      var message = data.responseJSON.message;
      message = '<div class="alert alert-danger alert-dismissable">' +
      '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
      message + '</div>';
      $("form[name=create]").before(message);
    }
  });
};

$(function() {
  window.lastChoice = 1;
  window.choice = 1;
  window.choiceLen = 2;
  updateHighlight();
  $(".btn-buy").mousedown(function(e) {
    $('#btn-buy-submit').attr('trade-id', $(this).attr('trade-id'));
    $('#item-quantity').attr('max', $(this).attr('quantity'));
    $('#item-cost').attr('max', parseInt($(this).attr('quantity')) * parseFloat($(this).attr('price-per-item')));
    $('#item-name').val($(this).attr('item-name'));
    $('#buy-modal').attr('price-per-item', $(this).attr('price-per-item'));
    $('#buy-modal').modal('show');
  });
  $("#item-input-suggest").hide();
  $("#item-input-suggest").css("visibility", "visible");
  $("#item-quantity").keyup(function(e) {
    if (e.which === 13) {
      doBuySubmit();
      return;
    }
    var cost = Math.round(parseFloat($("#buy-modal").attr('price-per-item')) * 100.0 * parseInt($(this).val())) / 100.0;
    $("#item-cost").val(cost.toString());
    if (cost.toString() == "NaN")
      $("#calc-cost").html("0");
    else
      $("#calc-cost").html(cost.toString());
  });
  $("#item-cost").keyup(function(e) {
    if (e.which === 13) {
      doBuySubmit();
      return;
    }
    var quantity = Math.floor(parseFloat($(this).val()) / parseFloat($("#buy-modal").attr('price-per-item')));
    var cost = Math.round(parseFloat($(this).val()) * 100) / 100.0;
    $("#item-quantity").val(quantity.toString());
    if (cost.toString() == "NaN")
      $("#calc-cost").html("0");
    else
      $("#calc-cost").html(cost.toString());
  });
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
      var value = $("#" + window.choice).html();
      if (value !== undefined)
        $("#item-input").val(value);
      window.location.href = "/trade?query=" + $(this).val();
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

</body>
</html>