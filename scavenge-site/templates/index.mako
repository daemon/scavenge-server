<%!
import random
%>
<!DOCTYPE html>
<html>
<head>
<title>ScavengeCraft: Brand-spanking new FTB SkyFactory 3 server</title>
<link rel="stylesheet" type="text/css" href="/public/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="/public/css/style.css">
<meta name="keywords" content="SkyFactory 3 server,FTB server,SkyFactory server,FTB economy">
<meta name="description" content="Brand new SkyFactory 3 FTB server with economy, PvP, and much more! Join us today.">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<%include file="components/navbar.mako"/>
<main class="fluid-container fill-container">
  <div id="main-container">
    <div id="main-title-wrapper">
      <img id="main-logo" src="/public/img/scavenge.png">
      <h3>A ${random.choice(["rare", "neat", "dank", "nice", "shiny"])} <strong>SkyFactory 3</strong> FTB server!</h3>
    </div>
    <div id="main-attributes-wrapper">
      <h2 class="scavenge-red">// dedicated server</h2>
      <h2 class="scavenge-green">// unique gameplay</h2>
      <h2 class="scavenge-blue">// vibrant community</h2>
    </div>
  </div>
  <div id="main-server-ip"><h2>Server IP: <strong>mc.scavenge.org</strong></h2></div>
  <div id="main-submarine"></div>
</main>
<div id="main-page-2" class="container">
  <div class="row panel-3">
    <div class="col-xs-12 col-sm-4"><span class="big-icon glyphicon glyphicon-tasks scavenge-red"></span>
      <h2 class="text-center"><strong>Dedicated server</strong></h2>
      <p class="description">DDoS-protected 24/7 North American box with 64 GB of DDR4 RAM, i7-6700k 4.00 GHz processor, and Gigabit connection.</p>
    </div>
    <div class="col-xs-12 col-sm-4"><span class="big-icon glyphicon glyphicon-flag scavenge-green"></span>
      <h2 class="text-center"><strong>Unique gameplay</strong></h2>
      <p class="description">Custom platforms like economy, ranks, and website interfaces guarantee a unique gameplay experience!</p>
    </div>
    <div class="col-xs-12 col-sm-4"><span class="big-icon glyphicon glyphicon-globe scavenge-blue"></span>
      <h2 class="text-center"><strong>Vibrant community</strong></h2>
      <p class="description">Players, players, players: central to every great server is a great com-munity, and we strive to achieve that.</p>
    </div>
  </div>
</div>
<script
  src="https://code.jquery.com/jquery-3.1.1.min.js"
  integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
  crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<%include file="components/alert_message.mako"/>
</body>
</html>
