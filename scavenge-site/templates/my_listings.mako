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
      <div id="server-status" class="panel panel-default">
        <div class="panel-body">
        % if data["trades"]:
        <h3>My listings</h3><br>
        <div id="fp-listing-container">
        % for trade, i in zip(data["trades"], range(len(data["trades"]))):
        % if i % 2 == 0:
          <div class="fp-listing gray">
        % else:
          <div class="fp-listing">
        % endif
            <div class="fp-listing-item">${trade["item_name"]} x <span class="quantity">${trade["trade"].quantity}</span></div>
            <div class="fp-listing-price">$${trade["trade"].price} each</div>
            <div class="fp-listing-buy"><button price-per-item="${trade['trade'].price}" trade-id="${trade['trade'].id}" class="btn btn-danger btn-remove btn-sm" type="button">Remove</button></div>
          </div>
        % endfor
        </div>
        </div>
        % else:
        <h3>You don't have any listings!</h3>
        % endif
      </div>
    </div>
    </div>
</main>

<script
  src="https://code.jquery.com/jquery-3.1.1.min.js"
  integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
  crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<script>
window.clearAlerts = function() {
  $(".alert").remove();
};

$(function() {
  $(".btn-remove").mousedown(function(e) {
    clearAlerts();
    var this_ = this;
    var data = JSON.stringify({
      "trade_id": $(this).attr("trade-id")
    });
    $.ajax({
      type: "POST",
      url: '/trade/delete', 
      data: data,
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function(data) {
        $(this_).parent().parent().css("text-decoration", "line-through");
        $(this_).parent().parent().css("color", "darkred");
        $(this_).attr("disabled", "");
      },
      error: function(data) {
        var message = data.responseJSON.message;
        message = '<div class="alert alert-danger alert-dismissable">' +
        '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
        message + '</div>';
        $(".flex-container").before(message);
      }
    });
  });
});
</script>

</body>
</html>