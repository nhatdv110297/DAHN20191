<?php
	require "connect.php";
	if(isset($_POST["username"],$_POST["password"])) 
    {  
    	$username = $_POST["username"];
    	$password = $_POST["password"];

    	$query = "INSERT INTO user (username, password) VALUES('".$username."','".$password."')";
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