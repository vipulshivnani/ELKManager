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
              <a href="/logout">Logout</a>
            </li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </nav>

    <!-- Begin page content -->

    <div class="container">
      <div class="page-header">
        
        <h1>VM Statistics</h1>
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
<form method="post">
  <button name = "Home" type="submit">Home</button>
</form>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<div id="vm_name">
Stats for VM: ${VMName}
<div>

<#if vm_type= "Lin">
   <div id="SSH_info">
       SSH info for  ${VMName} is : <br>
   IP Address : ${IPAD} <br>
   username : ${usernameVM}<br>
   password :${passwordVM}

<div>
</#if>
<#if vm_type= "Win">
<div id="RDP_info">
   RDP info for  ${VMName} is : <br>
 IP Address : ${IPAD} <br>
 username : ${usernameVM}<br>
password :${passwordVM}

<div>
</#if>



<script type="application/javascript"> google.load('visualization', '1', {packages: ['corechart', 'line']});
google.setOnLoadCallback(drawChart);

function drawChart(){
drawchart1();
drawLogScales();
}
function drawchart1(){
    var data1 = new google.visualization.DataTable();
    data1.addColumn('number', 'X');
    data1.addColumn('number', 'MaxCPU usage');
    data1.addColumn('number', 'overallCpuDemand');
    data1.addColumn('number', 'overallCpuUsage');
    var newrow2= new Array();

     <#list gcdata2 as mm1 >

        <#list mm1 as mmele1>
            <#assign ele=mmele1>
            newrow2.push(${mmele1});
        </#list>

        data1.addRow(newrow2);
        newrow2=[];

    </#list>

var options1 = {
    hAxis: {
    title: 'Time',
    logScale: false
    },
    vAxis: {
    title: 'MHz',
    logScale: false
    },
    colors: ['#a52714', '#097138']
};



var chart1 = new google.visualization.LineChart(document.getElementById('chart_div2'));
chart1.draw(data1, options1);
}
function drawLogScales() {

var data2 = new google.visualization.DataTable();

data2.addColumn('number', 'X');

data2.addColumn('number', 'MaxMemory Usage');

data2.addColumn('number', 'HostMemory Usage');
data2.addColumn('number', 'guestMemory Usage');


    var newrow1= new Array();

    <#list gcdata1 as mm >

    <#list mm as mmele>
        <#assign ele=mmele>
        newrow1.push(${mmele});
    </#list>

    data2.addRow(newrow1);
    newrow1=[];

</#list>



var options = {
hAxis: {
title: 'Time',
logScale: false
},
vAxis: {
title: 'MBs',
logScale: false
},
colors: ['#a52714', '#097138']
};




var chart2 = new google.visualization.LineChart(document.getElementById('chart_div1'));
chart2.draw(data2, options);
}

</script>

<div id="chart_div1"><div>
<div id="chart_div2"><div>
</center>
</div>
<footer class="footer">
      <div class="container">
        <p class="text-muted">Copyright team-03</p>
      </div>
    </footer>
</body>

</html>

