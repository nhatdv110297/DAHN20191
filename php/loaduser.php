<?php
	require "connect.php";
	class User{
		function User($id,$username, $password){
			$this->Id=$id;
			$this->Username = $username;
			$this->Password = $password;
		}
	}
	if(isset($_POST["iduser"])) 
    {  
    	$iduser = $_POST["iduser"];
    	if($iduser==1){
    		$result = mysqli_query($con,"SELECT id, username, password FROM user");
			$arrayUser = array();
			while($row = mysqli_fetch_assoc($result)){
				array_push($arrayUser, new User($row['id'],$row['username'],$row['password']));
			}
			echo json_encode($arrayUser);
    	}
    	else{
    		echo "Error permission";
    	}
		
	}
	else{
		echo "Error";
	}
?>