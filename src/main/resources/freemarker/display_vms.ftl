<!DOCTYPE html>
<!-- saved from url=(0054)http://getbootstrap.com/examples/sticky-footer-navbar/ -->
<html lang="en"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="http://getbootstrap.com/favicon.ico">

    <title>Dashboard</title>

    <!-- Bootstrap core CSS -->
    <link href="http://getbootstrap.com/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="http://getbootstrap.com/examples/sticky-footer-navbar/sticky-footer-navbar.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="./Sticky Footer Navbar Template for Bootstrap_files/ie-emulation-modes-warning.js"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

    <!-- Fixed navbar -->
    <nav class="navbar navbar-default navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="http://localhost:8082/displayVMs">Private Cloud Dashboard</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li>
              <a href="/display_vms">Refresh</a>
            </li>
            <li>
              <a href="/logout">Logout</a>
            </li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </nav>

    <!-- Begin page content -->

    <div class="container">
      <div class="page-header">
        
        <h1>Instance details</h1>
      </div>
     <style>
body {background-color: Lightblue;}
table {
    border: 1px solid black;
    border-collapse: unset;
    border-spacing: 0px;
     margin: 19px;
}

td, th {
    padding: 6px;
}
table {
    background-color: white;
}

td, th {
    border: 1px solid black;
    padding: 6px;
}

button {
    margin: 5px;
    overflow: visible;
}

</style>
<center>
<form method="post" >

<table>
   <tr>
       <th>Name</th>
       <th>Instance ID</th>
       <th>Connection Status</th>
       <th>IP Address</th>
       <th>Alarm State</th>
       <th>Power State</th>
       <th>Launch Time</th>
       <th>Select Instance</th>

   </tr>
   <#list VMs as vm>
       <tr>
           <#if (vm.name)??>
               <td>${vm["name"]}</td>
           <#else>
               <td></td>
           </#if>
           <#if (vm.instanceId)??>
               <td>${vm["instanceId"]}</td>
           <#else>
               <td></td>
           </#if>
           <#if (vm.conectionState)??>
               <td>${vm["conectionState"]}</td>
           <#else>
               <td></td>
           </#if>
           <#if (vm.ip)??>
               <td>${vm["ip"]}</td>
           <#else>
               <td></td>
           </#if>
           <#if (vm.alarmState)??>
               <td>${vm["alarmState"]}</td>
           <#else>
               <td></td>
           </#if>
           <#if (vm.powerState)??>
               <td>${vm["powerState"]}</td>
           <#else>
               <td></td>
           </#if>
           <#if (vm.launchTime)??>
               <td>${vm["launchTime"]}</td>
           <#else>
               <td></td>
           </#if>

            <td><input type="checkbox" name="${vm.name}" value="${vm.name}" id="${vm.name}"></td>

       </tr>
   </#list>
</table>
  <button name = "PowerOn" type="submit" id="PowerOn">Power On</button>
  <button name = "PowerOff" type="submit" id="PowerOff">Power Off</button>
  <button name = "Delete" type="submit" id="Delete">Delete</button>
  <button name = "Create" type="submit" id="CreateInstance">Create Instance</button>
  <button name = "Get_Chart" type="submit" id="MoreInfo">More Info</button>

</form>
</center>
    </div>

    <footer class="footer">
      <div class="container">
        <p class="text-muted">Copyright team-03</p>
      </div>
    </footer>


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="./Sticky Footer Navbar Template for Bootstrap_files/jquery.min.js"></script>
    <script src="./Sticky Footer Navbar Template for Bootstrap_files/bootstrap.min.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="./Sticky Footer Navbar Template for Bootstrap_files/ie10-viewport-bug-workaround.js"></script>
  <script type="application/javascript">

  var checker = document.getElementById('checkme');
  var sendbtn = document.getElementById('sendNewSms');
  // when unchecked or checked, run the function
  checker.onchange = function(){
      if(this.checked){
          sendbtn.disabled = true;
      } else {
          sendbtn.disabled = false;
      }

  }

  </script>

</body></html>