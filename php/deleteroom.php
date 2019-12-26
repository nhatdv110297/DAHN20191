<?php
	require "connect.php";
	if(isset($_POST["idroom"])) 
    {  
    	$idroom = $_POST["idroom"]; 
    	$query2 = "DELETE FROM room WHERE id = '".$idroom."'";
		if(mysqli_query($con,$query2)){
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