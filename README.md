# AndroidOnlineLogin
A simple login form for android apps. Uses PHP to process credentials in the online server. Passwords are hashed with bcrypt.

The simple-login folder contains files for the server to process the information sent by the app. The server-side script is based on this [guide](http://www.wikihow.com/Create-a-Secure-Login-Script-in-PHP-and-MySQL).
Most of the app's code were based off this [guide](http://www.coderzheaven.com/2012/04/22/create-simple-login-form-php-android-connect-php-android/).

![login](https://github.com/nbau21/AndroidOnlineLogin/blob/master/screenshots/login.png)

The login screen asks for the user's email and password. There is also a "Remember Me" checkbox for users who do not want to login every time the app is run.

![signup](https://github.com/nbau21/AndroidOnlineLogin/blob/master/screenshots/signup.png)

The registration page asks the users for some information and then sends it to the server. If all fields are in correct format, the information is saved online.

![reset_password](https://github.com/nbau21/AndroidOnlineLogin/blob/master/screenshots/reset_password.png)

Not functional as of 02/09/2015. This page should send a reset password link to the user's email.
