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
<main id="dashboard-main" class="padded sm-change">
  <div id="dashboard-actions" class="action-panel">
    <div class="action-panel-heading">Shortcuts</div>
    <div class="flex-grid">
      <a href="/trade" class="action disabled"><p class="icon"><span class="glyphicon glyphicon-euro"></span></p>economy</a>
      <a href="/forum" class="action"><p class="icon"><span class="glyphicon glyphicon-user"></span></p>social</a>
      <a href="/forum/viewforum.php?f=4" class="action disabled"><p class="icon"><span class="glyphicon glyphicon-question-sign"></span></p>help</a>
      <a href="http://ftbservers.com/server/6YQ2B2z7/vote" class="action disabled"><p class="icon"><span class="glyphicon glyphicon-circle-arrow-up"></span></p>vote</a>
      <a href="/" class="action disabled"><p class="icon"><span class="glyphicon glyphicon-heart"></span></p>donate</a>
    </div>
  </div>
  <div id="dashboard-stats">
    <div class="flex-container">
      <div id="server-status" class="panel panel-default">
        <div class="panel-heading">Server news</div>
        <div class="panel-body">Test</div>
      </div>
      <div class="right">
        <div id="user-status" class="panel panel-default">
          <div class="panel-heading">Server status</div>
          <div class="panel-body">Test</div>
        </div>
        <div id="recent-activity" class="panel panel-default">
          <div class="panel-heading">User status</div>
          <div class="panel-body">Test</div>
        </div>
      </div>
    </div>
  </div>
</main>
<script
  src="https://code.jquery.com/jquery-3.1.1.min.js"
  integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
  crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>