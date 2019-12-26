<?php
	require "connect.php";
	class Room{
		function Room($id, $idroom, $name, $pin, $indexicon){
			$this->Id = $id;
			$this->Idroom = $idroom;
			$this->Name = $name;
			$this->Pin = $pin;
			$this->Indexicon = $indexicon;
		}
	}
	if(isset($_POST["idroom"])) 
    {  
    	$idroom = $_POST["idroom"]; 
		$result = mysqli_query($con,"SELECT * FROM button WHERE idroom = '".$idroom."'");
		$arrayRoom = array();
		while($row = mysqli_fetch_assoc($result)){
			array_push($arrayRoom, new Room($row['id'],$row['idroom'],$row['namebutton'],$row['pin'],$row['indexicon']));
		}
		echo json_encode($arrayRoom);
	}
	else{
		echo "Error";
	}
?>