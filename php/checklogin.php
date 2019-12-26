<?php
    require "connect.php";
    if(isset($_POST["username"], $_POST["password"])) 
    {     

        $name = $_POST["username"]; 
        $password = $_POST["password"]; 
        

        $result1 = mysqli_query($con,"SELECT id FROM user WHERE username='$name' AND password='$password'");

        if(mysqli_num_rows($result1) > 0 )
        { 
            $row = mysqli_fetch_assoc($result1);
            echo $row['id'];
        }
        else
        {
            echo 'The username or password are incorrect!';
        }
    }
    else{
        echo "error data";
    }
?>