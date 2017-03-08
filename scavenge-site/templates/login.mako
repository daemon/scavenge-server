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
        <div class="panel-heading"><h3><strong>Login</strong></h3></div>
        <div class="panel-body">
          <form name="login" action="/login/" method="post" role="form">
            <div class="form-group">
              <label for="text">Username:</label>
              <input name="username" type="text" class="form-control" id="username">
            </div>
            <div class="form-group">
              <label for="pwd">Password:</label>
              <input type="password" class="form-control" name="password" id="pwd">
            </div>
          </form>
          <button id="submit-btn" onclick="forumLogin()" class="btn btn-warning btn-wide">Submit</button><br>
          <a data-toggle="modal" data-target="#forgot-modal" href="javascript:void(0);">Forgot password or username?</a>
        </div>
      </div>
    </div>
  </div>
</div>

<div id="forgot-modal" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header modal-header-bar">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Forgot credentials</h4>
      </div>
      <div class="modal-body">
        Connect to the game server at <b>mc.scavenge.org</b> and send <b>/forgot</b>.
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">OK</button>
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
$("input").keyup(function(event){
    if(event.keyCode == 13){
        $("#submit-btn").click();
    }
});
window.forumLogin = function() {
  var username = $("#username").val();
  var password = $("#pwd").val();
  $.post("/forum/login_user.php", {username: username, password: password}, 
    function(data) {
      document.login.submit();
    });
  return false;
}
</script>
<%include file="components/alert_message.mako"/>
</body>
</html>