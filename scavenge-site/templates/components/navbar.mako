<nav class="navbar navbar-default navbar-fixed-top">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="/" onclick="window.top.location.href = 'http://scavenge.org/';"><img id="navbar-brand-logo" src="/public/img/scavenge-brand-02.png">svcraft</a>
    </div>
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav">
        <li><a href="#">News</a></li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Community <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="/forum" onclick="window.top.location.href = 'http://scavenge.org/forum';">Forum</a></li>
            <li><a href="#">Knowledge base</a></li>
            <li class="divider"></li>
            <li><a href="https://discord.gg/kPtUdSe">Discord</a></li>
            <!--<li><a href="#">Facebook</a></li>
            <li><a href="#">Instagram</a></li>
            <li><a href="#">Snapchat</a></li>
            <li><a href="#">Twitter</a></li>-->
          </ul>
        </li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Game <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <!--<li><a href="#">Chat</a></li>
            <li><a href="#">Dynmap</a></li>-->
            <li><a href="/trade">Economy</a></li>
            <li><a href="http://ftbservers.com/server/6YQ2B2z7/vote">Vote</a></li>
          </ul>
        </li>
        <li><a href="javascript:void(0);" data-toggle="modal" data-target="#contact-modal">Contact</a></li>
      </ul>      

      <ul class="nav navbar-nav navbar-right">
% if user:
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${user.username} <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="/dashboard" onclick="window.top.location.href = 'http://scavenge.org/dashboard';">Dashboard</a></li>
            <li><a class="scavenge-red" href="/logout" onclick="window.top.location.href = 'http://scavenge.org/';">Logout</a></li>
          </ul>
        </li>
% else:
        <li><button id="login-btn" onclick="window.top.location.href = 'http://scavenge.org/login';" class="btn btn-default navbar-btn" href="#">Login</button></li>
        <li><button id="signup-btn" data-toggle="modal" data-target="#signup-modal" class="btn btn-warning navbar-btn" href="#">Sign Up</button></li>
% endif
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>

<div id="signup-modal" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header modal-header-bar">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Sign up</h4>
      </div>
      <div class="modal-body">
        <p>Thanks for your interest! By signing up, you gain access to the web services. It takes only 2 simple steps:</p>
        <ol>
        <li>Join the game server at <b>mc.scavenge.org</b></li>
        <li>Send <b>/register</b></li>
        </ol>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Got it</button>
      </div>
    </div>
  </div>
</div>

<div id="contact-modal" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header modal-header-bar">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Contact</h4>
      </div>
      <div class="modal-body two-column">
        <span class="first">Email</span><span class="second">c&#114;&#97;ftsca&#118;eng&#101;&#64;gm&#97;il&#46;c&#111;&#109;</span>
        <span class="first">In-game</span><span class="second">/helpop (message)</span>
        <span class="first">Skype</span><span class="second">scavengecraft</span>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>