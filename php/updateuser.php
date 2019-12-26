<?php
	require "connect.php";
	if(isset($_POST["id"],$_POST["password"])) 
    {  
    	$id = $_POST["id"]; 
    	$password = $_POST["password"];

    	$query = "UPDATE user SET password = '".$password."' WHERE id = '".$id."'";
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