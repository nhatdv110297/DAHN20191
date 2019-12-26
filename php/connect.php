<?php
	$hostname = "localhost";
	$username = "id9747172_admin";
	$password = "nhat1102";
	$databasename = "id9747172_user_infor";

	$con = mysqli_connect($hostname,$username,$password,$databasename);
	mysqli_query($con,"SET NAMES 'utf8'");
?>