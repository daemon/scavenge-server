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
        <h3>Tradeable items</h3><br>
        <div id="fp-listing-container">
        % for item_name, i in zip(data, range(len(data))):
        % if i % 2 == 0:
          <div class="fp-listing gray">
        % else:
          <div class="fp-listing">
        % endif
            ${item_name}
          </div>
        % endfor
        </div>
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