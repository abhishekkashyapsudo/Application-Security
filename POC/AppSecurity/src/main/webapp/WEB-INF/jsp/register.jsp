
<html>
<head>
<title>Ecom Registration Form</title>

<script>

function clearFields() {
	document.getElementById("name").value = "";
	document.getElementById("password").value = "";
	document.getElementById("confirmPassword").value = "";
	document.getElementById("contact").value = "";
	document.getElementById("address").value = "";
	document.getElementById("username").value = "";
	document.getElementById("captcha").value = "";
}
function validateAndSubmit(){

	var errors = "";
				document.getElementById("error").innerHTML = "";
	
	let name = document.getElementById("name").value;
	let password = document.getElementById("password").value;
	let confirmPassword = document.getElementById("confirmPassword").value;
	let contact = document.getElementById("contact").value;
	let address = document.getElementById("address").value;
	let username = document.getElementById("username").value;
	let captcha = document.getElementById("captcha").value;
	
	if(name === "" || name.length <3){
		errors += "Name must have at least 3 characters.<br/>";
	}
	if(password === "" || password.length <8){
		errors += "Password must have at least 8 characters.<br/>";
	}
	if(confirmPassword === "" || confirmPassword.length <8){
		errors += "Confirm Password must have at least 8 characters.<br/>";
	}
	if(contact === "" || contact.length <5){
		errors += "Phone number must have at least 5 characters.<br/>";
	}
	if(address === "" || address.length <6){
		errors += "Address must have at least 6 characters.<br/>";
	}
	if(username === "" || username.length <4){
		errors += "Username must have at least 4 characters.<br/>";
	}
	if(captcha === "" ){
		errors += "Captcha can't be empty.<br/>";
	}
	if(password !== confirmPassword){
		errors += "Password and Confirm PAssword must be equal.<br/>";
	}
	
	if(errors.length !== 0){
		document.getElementById("error").innerHTML = errors;
		return;
	}
	const user = {};
	user.name= name;
	user.password = password;
	user.confirmPassword = confirmPassword;
	user.contact = contact;
	user.address = address;
	user.username = username;
	user.captcha = captcha;
	
	
	fetch("/api/register", {
	    method: 'post',
	    body: JSON.stringify(user),
	    headers: {
	        'Accept': 'application/json',
	        'Content-Type': 'application/json'
	    }
	}).then((response) => {
		if(response.status ===200){
			document.getElementById("error").innerHTML = "User with username " + username + " registered successfully. Please login to continue.";
			clearFields();
			return;
		}
	    return response.json()
	}).then((res) => {
	debugger;
		if(res.errors && res.errors.length> 0){
			document.getElementById("error").innerHTML = res.errors[0].defaultMessage;
			}
			else if(res.error && res.error.length > 0){
			document.getElementById("error").innerHTML = res.message;
			}
	    
	}).catch((error) => {
		
	    console.log(error)
	})
}
</script>
<meta
  http-equiv="Content-Security-Policy"
  content="default-src 'self'; img-src https://*; child-src 'none';" />
  <meta name="referrer" content="strict-origin" />
  
</head>
<body>
<h1>Ecom Register Form</h1>
<form action="/api/register" method="post" enctype="application/json">
<span style="color:red" id = "error"></span>
			<table style="with: 50%">
				<tr>
					<td>First Name</td>
					<td><input type="text" name="name" id="name"/></td>
				</tr>
				
				<tr>
					<td>UserName</td>
					<td><input type="text" name="username" id="username"/></td>
				</tr>
					<tr>
					<td>Password</td>
					<td><input type="password" name="password" id="password"/></td>
				</tr>
				<tr>
					<td>Confirm Password</td>
					<td><input type="password" name="confirmPassword" id="confirmPassword"/></td>
				</tr>
				<tr>
					<td>Address</td>
					<td><input type="text" name="address" id="address"/></td>
				</tr>
				<tr>
					<td>Contact No</td>
					<td><input type="text" name="contact" id="contact"/></td>
				</tr>
				<tr>
					<td>Please Enter Captcha</td>
					<td><img src="data:image/png;base64,<%= request.getSession().getAttribute("captcha") %>"/><br/>
					<input type="text" name="captcha" id="captcha"/>
					</td>
				</tr>
				
				</table>
			<input type="button" onclick="validateAndSubmit()" value="Register"></input></form>
			<a href="/ui/login">Click here to login</a>
</body>
</html>