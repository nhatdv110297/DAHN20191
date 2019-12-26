<?php
	require "connect.php";
	class Room{
		function Room($id,$iduser,$name, $codeesp, $indexicon, $numberdevices){
		    $this->Id = $id;
			$this->Iduser = $iduser;
			$this->Name = $name;
			$this->Codeesp = $codeesp;
			$this->Indexicon = $indexicon;
			$this->Numberdevices = $numberdevices;
		}
	}
	if(isset($_POST["iduser"])) 
    {  
    	$iduser = $_POST["iduser"]; 
		$result = mysqli_query($con,"SELECT * FROM room WHERE iduser = '".$iduser."'");
		$arrayRoom = array();
		while($row = mysqli_fetch_assoc($result)){
		    $result2 = mysqli_query($con,"SELECT count(*) as count FROM button WHERE idroom = '".$row['id']."'");
		    $devices = mysqli_fetch_assoc($result2);
			array_push($arrayRoom, new Room($row['id'],$row['iduser'],$row['name'],$row['codeesp'],$row['indexicon'],$devices['count']));
		}
		echo json_encode($arrayRoom);
	}
	else{
		echo "Error";
	}
?>