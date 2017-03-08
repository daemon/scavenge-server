<!DOCTYPE html>
<html>
<head>
<title>Register for ScavengeCraft</title>
<link rel="stylesheet" type="text/css" href="/public/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="/public/css/style.css">
<meta name="keywords" content="ScavengeCraft register,ScavengeCraft signup">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body class="soft-white">
<%include file="components/navbar.mako"/>
<div class="container first-container">
  <div class="row">
    <div class="col-xs-12 col-sm-9 center-block">
      <div class="panel panel-default">
        <div class="panel-heading"><h3><strong>Registration</strong></h3></div>
        <div class="panel-body">
          <form action="/register/" method="post" role="form">
            <div class="form-group">
              <label for="username">Username:</label>
              <input name="username" type="email" class="form-control" id="username" value="${data['username']}" disabled>
            </div>
            <div class="form-group">
              <label for="email">Email address:</label>
              <input type="email" class="form-control" name="email" id="email">
            </div>
            <div class="form-group">
              <label for="pwd">Password:</label>
              <input type="password" class="form-control" name="password" id="pwd">
            </div>
            <div class="form-group">
              <label for="pwd-confirm">Confirm password:</label>
              <input type="password" class="form-control" id="pwd-confirm">
            </div>
            <button type="submit" class="btn btn-warning" disabled>Submit</button>
          </form>
        </div>
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
window.checkSubmitReady = function() {
  var pwSet = false;
  var pwConfirm = false;
  if ($("#pwd").val() === "") {
  } else if ($("#pwd").val().length < 8) {
    $("#pwd").css("background-color", "#ffd1d1");
    $("#pwd").tooltip("show");
  } else {
    $("#pwd").css("background-color", "#fff");
    $("#pwd").tooltip("hide");
    pwSet = true;
  }
  if ($("#pwd-confirm").val() === "") {
  } else if ($("#pwd-confirm").val() !== $("#pwd").val()) {
    $("#pwd-confirm").css("background-color", "#ffd1d1");
  } else {
    $("#pwd-confirm").css("background-color", "#fff");
    pwConfirm = true;
  }
  if (pwSet && pwConfirm && $("#email").val().length > 0) {
    $("button[type=submit]").attr("disabled", false);
  }
}

window.pwSet = false;
window.pwConfirm = false;

$(function() {
  $('#pwd').tooltip({'trigger':'manual', 'title': 'Must have at least 8 characters'});
  $('#pwd').change(checkSubmitReady);
  $('#pwd-confirm').change(checkSubmitReady);
  $('#pwd-confirm').keyup(checkSubmitReady);
  $('#email').change(checkSubmitReady);
  $('#pwd').focus(function() {
    $("#pwd").tooltip("show");
  });
});
</script>
</body>
</html>