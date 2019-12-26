<?php
	require "connect.php";
	if(isset($_POST["id"],$_POST["iduser"],$_POST["name"],$_POST["codeesp"],$_POST["indexicon"])) 
    {  
    	$id = $_POST["id"];
    	$iduser = $_POST["iduser"]; 
    	$name = $_POST["name"];
    	$codeesp  =$_POST["codeesp"];
    	$icon = $_POST["indexicon"];

    	$query = "UPDATE room SET name = '".$name."', codeesp = '".$codeesp."',indexicon = '".$icon."' WHERE id = '".$id."' AND iduser = '".$iduser."'";
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