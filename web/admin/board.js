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

  console.log(snap.val());


    var x = document.getElementById("table_whole").rows.length;
    document.getElementById("board_one").innerHTML = x;

});



  
