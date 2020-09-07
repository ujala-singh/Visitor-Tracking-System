var rootRef = firebase.database().ref().child("Visitor_Details");

rootRef.on("child_added", snap => {
  var name=snap.child("name").val();
  var phone_number=snap.child("no").val();
  var organisation_name=snap.child("oname").val();
  var purpose=snap.child("purpose").val();
  var uid=snap.child("uid").val();
  var visitor_email=snap.child("visitor_email").val();
  var gender=snap.child("gender").val();
  var type=snap.child("type").val();
  var rating=snap.child("rating").val();
  var comment=snap.child("comment").val();
  var reason=snap.child("reason").val();
  var location=snap.child("location").val();


  console.log(snap.val());



  $("#table_body").append("<tr><td>" + name + "</td><td>" + phone_number +
    "</td><td>" + organisation_name + "</td><td>" + purpose + "</td><td>" + uid +
    "</td><td>" + visitor_email + "</td><td>" + gender + "</td><td>" + type + "</td><td>" + rating + "</td><td>" + comment +"</td><td>" + location + "</td><td>"+reason +"</td></tr>");

  

});


