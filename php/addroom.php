<?php
	require "connect.php";
	if(isset($_POST["iduser"],$_POST["name"],$_POST["codeesp"],$_POST["indexicon"])) 
    {  
    	$iduser = $_POST["iduser"]; 
    	$name = $_POST["name"];
    	$codeesp  = $_POST["codeesp"];
    	$icon = $_POST["indexicon"];

    	$query = "INSERT INTO room (iduser, name, codeesp, indexicon) VALUES('".$iduser."','".$name."','".$codeesp."','".$icon."')";
		if(mysqli_query($con,$query)){
			echo "OK";
		}
		else{
			echo "Error";
		}
	}
	else{
		echo "Error";
	}
?>