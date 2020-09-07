
function login(){

	var userEmail = document.getElementById("email_field").value;
	var userPass = document.getElementById("password_field").value;

	var userid=["amit@gmail.com","ujala2yz@gmail.com","ashwin@gmail.com"];
	var paswd=["password","ujala2yz","ashwin"];
	var i,j=0;

	for(i=0;i<userid.length;i++) {
		if(userEmail == userid[i]){
			j=1;
			if(userPass == paswd[i]){
				self.location="index.html";
				break;
			}
			else{
				alert("Wrong Password ");
				break
			}
		}
	}
	if(j==0){
	alert("Username not Found ");	
	}
}

