<?php
//Create the login processing page (process_login.php)
include_once 'db_connect.php';
include_once 'functions.php';
sec_session_start(); // Our custom secure way of starting a PHP session.
if (isset($_POST['email'], $_POST['password'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];

    if (login_bcrypt($email, $password, $mysqli) == true) {
        // Login success
		echo "User Found";
        //header('Location: ../../exp_rev.php');
    } else {
        // Login failed
		echo "User Not Found";
        //header('Location: ../login.php?error=1');
    }
} else {
    // The correct POST variables were not sent to this page.
    header('Location: ../login.php?error=1');
}
?>
