<?php
	require "connect.php";
	if(isset($_POST["id"],$_POST["namebutton"],$_POST["pin"],$_POST["indexicon"])) 
    {  
    	$id = $_POST["id"]; 
    	$namebutton = $_POST["namebutton"];
    	$pin = $_POST["pin"];
    	$indexicon = $_POST["indexicon"];

    	$query = "UPDATE button SET namebutton = '".$namebutton."', pin = '".$pin."', indexicon = '".$indexicon."' WHERE id = '".$id."'";
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