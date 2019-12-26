<?php
	require "connect.php";
	if(isset($_POST["idroom"],$_POST["namebutton"],$_POST["pin"],$_POST["indexicon"])) 
    {  
    	$idroom = $_POST["idroom"]; 
    	$namebutton = $_POST["namebutton"];
    	$pin = $_POST["pin"];
    	$indexicon = $_POST["indexicon"];

    	$query = "INSERT INTO button (idroom, namebutton, pin, indexicon) VALUES('".$idroom."','".$namebutton."','".$pin."','".$indexicon."')";
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