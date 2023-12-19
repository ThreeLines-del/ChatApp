package com.example.chatapp.ui

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.Message
import com.example.chatapp.User
import com.example.chatapp.constants.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class MainViewModel: ViewModel() {
    private var _uid = MutableLiveData<String?>()
    val currentUserProfilePic: MutableState<String?> = mutableStateOf(null)

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    val messageList = MutableStateFlow<MutableList<Message>>(mutableListOf())

    val userList = MutableStateFlow<MutableList<User?>>(mutableListOf())

    private val _authenticationStatus = MutableLiveData<AuthenticationStatus>()
    val authenticationStatus: LiveData<AuthenticationStatus> = _authenticationStatus

    val mAuth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    private val _uploadResult = MutableLiveData<UploadResult>()
    val uploadResult: LiveData<UploadResult> get() = _uploadResult

    sealed class UploadResult {
        data class Success(val imageUrl: String) : UploadResult()
        object Failure : UploadResult()
    }
    
    init {
        getUserProfileImage()
        receiveListOfUsersAndAddToHomeScreen(mAuth.currentUser!!.uid)
    }

    fun updateMessage(message: String) {
        _message.value = message
    }

    fun login(email: String, password: String){

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authenticationStatus.value = AuthenticationStatus.SUCCESS
                } else {
                    _authenticationStatus.value = AuthenticationStatus.FAILURE
                }
            }
    }

    fun signUp(email: String, password: String){

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authenticationStatus.value = AuthenticationStatus.SUCCESS
                } else {
                    _authenticationStatus.value = AuthenticationStatus.FAILURE
                }
            }

    }

    fun addUserToDatabase(name: String, email: String, uid: String, profilePic: Uri){
        val path = "profile_images/${mAuth.currentUser!!.uid}.jpg"
        val profileImageRef = storageRef.child(path)

        val userData = mapOf(
            Constants.USER_NAME to name,
            Constants.USER_EMAIL to email,
            Constants.USER_ID to uid,
            Constants.PROFILE_PIC to path
        )
        val uploadTask = profileImageRef.putFile(profilePic)
        uploadTask.addOnSuccessListener {
            firestore.collection(Constants.USERS)
                .document(Constants.USERS_DOCUMENT)
                .collection(Constants.USER_DATA)
                .add(userData)
                .addOnSuccessListener {
                    Log.d("Firestore", "User added successfully.")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error creating user: $e")
                }
        }
            .addOnFailureListener{
                Log.e("Storage", "Error uploading image: $it")
            }
    }

    fun logOut(){
        mAuth.signOut()
    }

    private fun receiveListOfUsersAndAddToHomeScreen(uid: String) {

        firestore.collection(Constants.USERS)
            .document(Constants.USERS_DOCUMENT)
            .collection(Constants.USER_DATA)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    // Handle errors here, if any
                    Log.w(Constants.TAG, "Listen failed.")
                    return@addSnapshotListener
                }

                val usersList = mutableListOf<User?>()

                if (value != null) {
                    for (doc in value) {
                        val userData = doc.data

                        val userName = userData[Constants.USER_NAME] as String
                        val userEmail = userData[Constants.USER_EMAIL] as String
                        val userID = userData[Constants.USER_ID] as String
                        val profilePic = userData[Constants.PROFILE_PIC] as String?

                        val imageInStorage = profilePic?.let { storageRef.child(it) }
                        imageInStorage?.downloadUrl?.addOnSuccessListener { uri ->
                            val imageUri = uri.toString()
                            Log.i("LinesImages", imageUri)
                            val user = User(userName, userEmail, userID, imageUri)
                            if (uid != userID) {
                                usersList.add(user)
                            }
                            updateUsers(usersList)
                        }?.addOnFailureListener {
                            Log.e("ImageDownloadError", it.message.toString())
                        }
                    }
                }
            }
    }

    private fun updateUsers(list:MutableList<User?>){
        userList.value = list
    }

    fun addMessage(senderID: String, receiverID: String, message: String) {
        if (message.isNotEmpty()) {
            val conversationId = generateConversationId(senderID, receiverID)

            val messageData = mapOf(
                Constants.MESSAGE to message,
                Constants.SENDER_ID to senderID,
                Constants.TIMESTAMP to FieldValue.serverTimestamp()
            )

            firestore.collection(Constants.CONVERSATIONS)
                .document(conversationId)
                .collection(Constants.MESSAGES)
                .add(messageData)
                .addOnSuccessListener {
                    _message.value = ""
                    Log.d("Firestore", "Message sent successfully.")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error sending message: $e")
                }
        }
    }

    fun getMessages(senderID: String, receiverID: String) {
        val conversationId = generateConversationId(senderID, receiverID)

        firestore.collection(Constants.CONVERSATIONS)
            .document(conversationId)
            .collection(Constants.MESSAGES)
            .orderBy(Constants.TIMESTAMP) // Order messages by timestamp
            .addSnapshotListener { value, e ->
                if (e != null) {
                    // Handle errors here, if any
                    Log.w(Constants.TAG, "Listen failed.")
                    return@addSnapshotListener
                }

                val messageList = mutableListOf<Message>()

                if (value != null) {
                    for (doc in value) {
                        val messageData = doc.data

                        // Extract message properties from Firestore document data
                        val text = messageData[Constants.MESSAGE] as String
                        val senderID1 = messageData[Constants.SENDER_ID] as String
                        val timestampField = messageData[Constants.TIMESTAMP]

                        // Check if the timestamp field exists and is not null
                        val timestamp = if (timestampField is Timestamp) {
                            timestampField.toDate()
                        } else {
                            // Handle the case where the timestamp is missing or null
                            // For example, you can use the current time as a fallback
                            Calendar.getInstance().time
                        }

                        // Create a Message object
                        val message = Message(text, senderID1, timestamp)

                        // Add the message to the list
                        messageList.add(message)
                    }
                }

                // Update your UI or ViewModel with the list of messages
                updateMessages(messageList)
            }
    }

    private fun updateMessages(list:MutableList<Message>){
        messageList.value = list
    }

    private fun generateConversationId(senderID: String, receiverID: String): String {
        // You can create a unique conversation ID using a combination of user IDs
        return if (senderID < receiverID) "$senderID-$receiverID" else "$receiverID-$senderID"
    }

    private fun getUserProfileImage(){
        firestore.collection(Constants.USERS)
            .document(Constants.USERS_DOCUMENT)
            .collection(Constants.USER_DATA)
            .addSnapshotListener{ value, e ->
                if (e != null) {
                    // Handle errors here, if any
                    Log.w(Constants.TAG, "Listen failed.")
                    return@addSnapshotListener
                }
                if(value != null){
                    for(doc in value){
                        val profilePicData = doc.data
                        val imagePath = profilePicData[Constants.PROFILE_PIC] as String
                        if(imagePath.contains(mAuth.currentUser!!.uid)){
                            val imageInStorage = storageRef.child(imagePath)
                            imageInStorage.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                currentUserProfilePic.value = imageUrl
                            }.addOnFailureListener{
                                Log.e("ImageDownloadError", it.message.toString())
                            }
                        }
                    }
                }
            }
    }
}