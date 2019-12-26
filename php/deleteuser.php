<?php
	require "connect.php";
	if(isset($_POST["id"])) 
    {  
    	$id = $_POST["id"]; 
    	$query = "DELETE FROM user WHERE id = '".$id."'";
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