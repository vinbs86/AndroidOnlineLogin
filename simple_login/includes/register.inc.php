<?php
include_once 'db_connect.php';
include_once 'psl-config.php';
 
$error_msg = "";
 
if (isset($_POST['firstName'], $_POST['email'], $_POST['password'])) {
    $firstName = filter_input(INPUT_POST, 'firstName', FILTER_SANITIZE_STRING);
    $email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
	$password = filter_input(INPUT_POST, 'password', FILTER_SANITIZE_STRING);
	
	/*
	$email = filter_var($email, FILTER_VALIDATE_EMAIL);
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        // Not a valid email
        $error_msg .= 'Invalid email';
		echo $error_msg
    }
	*/
    $prep_stmt = "SELECT id FROM members WHERE email = ? LIMIT 1";
    $stmt = $mysqli->prepare($prep_stmt);

   // check existing email.. doesn't work
   
    if ($stmt) {
        $stmt->bind_param('s', $email);
        $stmt->execute();
        $stmt->store_result();
 
        if ($stmt->num_rows == 1) {
            // A user with this email address already exists
            $error_msg .= '<p class="error">A user with this email address already exists.</p>';
			echo "Email Error";
            $stmt->close();
        }
		$stmt->close();
    } else {
        $error_msg .= '<p class="error">Database error Line 39</p>';
		echo "Database Error";
        $stmt->close();
    }
	
	if (empty($error_msg)) {
		//hash the password
		$hash = password_hash($password, PASSWORD_DEFAULT);
 
        // Insert the new user into the database 
        if ($insert_stmt = $mysqli->prepare("INSERT INTO members (firstName, email, hash) VALUES (?, ?, ?)")) {            
			$insert_stmt->bind_param('sss', $firstName, $email, $hash);
			if (! $insert_stmt->execute()) {
                //header('Location: ../error.php?err=Registration failure: INSERT');
				echo "Insertion Error";
            }
		}
		echo "Success";
        //header('Location: ./register_success.php');
    }
}
?>
