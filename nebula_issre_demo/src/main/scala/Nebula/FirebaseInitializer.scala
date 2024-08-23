package Nebula

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase

object FirebaseInitializer {
  def initialize(): Unit = {
    val serviceAccount = getClass.getResourceAsStream("./nebula-cf706-firebase-adminsdk-cjswy-6d96607db5.json")
    //https://nebula-cf706-default-rtdb.firebaseio.com/
    val options: FirebaseOptions = new FirebaseOptions.Builder()
      .setDatabaseUrl("https://nebula-cf706-default-rtdb.firebaseio.com")
      .setServiceAccount(serviceAccount)
      .build
    FirebaseApp.initializeApp(options)
  }
}

