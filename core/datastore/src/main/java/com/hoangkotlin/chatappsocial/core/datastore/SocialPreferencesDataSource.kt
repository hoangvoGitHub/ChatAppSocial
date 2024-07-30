package com.hoangkotlin.chatappsocial.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.hoangkotlin.chatappsocial.core.model.AppUserData
import com.hoangkotlin.chatappsocial.core.model.ChatAppUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


// login -> store
// what to store -> store a list of user identities  [username (must), password(optional), image(must)]
// currentUser
/* UserPreferences( Set<UserCredential>

 */
private const val TAG = "SocialPreferencesDataSo"

class SocialPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {

    val appUserData: Flow<AppUserData> = userPreferences.data
        .map { userPreferences ->
            AppUserData(
                currentUser = userPreferences.currentUserOrNull?.asChatAppUser(),
                appUsers = userPreferences.userCredentialsMap.mapValues { entry ->
                    entry.value.asChatAppUser()
                }
            )
        }

    suspend fun token(): String? {
        return appUserData.distinctUntilChanged().first().currentUser?.token
    }

    suspend fun addUser(chatAppUser: ChatAppUser) {
        try {
            userPreferences.updateData {

                it.copy {
                    userCredentials.put(chatAppUser.username, chatAppUser.asUserCredential())
                }
            }
        } catch (ioException: IOException) {
            Log.e("SocialPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun signInWith(chatAppUser: ChatAppUser, isRemembered: Boolean = false) {
        try {
            userPreferences.updateData {
                it.copy {
                    currentUser = chatAppUser.asUserCredential()
                    if (isRemembered) {
                        userCredentials.put(chatAppUser.username, chatAppUser.asUserCredential())
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("SocialPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun signInWith(
        username: String,
        password: String?,
        token: String,
        isRemembered: Boolean = false
    ) {
        try {
            val userCredential = UserCredential.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setToken(token)
                .build()

            userPreferences.updateData {
                it.copy {
                    clearCurrentUser()
                    currentUser = userCredential
                    if (isRemembered) {
                        userCredentials.put(
                            username, UserCredential.newBuilder()
                                .setUsername(username)
                                .setPassword(password)
                                .setToken(token)
                                .build()
                        )
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("SocialPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun updateUserData(
        imagePath: String? = null,
        chatUserId: String? = null
    ) {
        try {
            userPreferences.updateData {
                if (it.currentUserOrNull?.image == imagePath &&
                    it.currentUserOrNull?.chatUserId == chatUserId
                ) this.userPreferences
                it.copy {
                    val userCredential = UserCredential.newBuilder()
                        .setUsername(currentUser.username)
                        .setPassword(currentUser.password)
                        .setToken(currentUser.token)
                        .setImage(imagePath ?: currentUser.image)
                        .setChatUserId(chatUserId ?: currentUser.chatUserId)
                        .build()
                    clearCurrentUser()
                    currentUser = userCredential
                }
            }
        } catch (ioException: IOException) {
            Log.e("SocialPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun deleteUser(username: String) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.userCredentials.remove(username)
                    if (currentUser.username?.equals(username) == true) {
                        clearCurrentUser()
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("SocialPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun signOut() {
        try {
            userPreferences.updateData {
                it.toBuilder().clearCurrentUser().build()
//                it.copy {
//                    clearCurrentUser()
//                }
            }
        } catch (ioException: IOException) {
            Log.e("SocialPreferences", "Failed to update user preferences", ioException)
        }
    }
}

fun ChatAppUser.asUserCredential(): UserCredential {
    return UserCredential.newBuilder()
        .setUsername(username)
        .setPassword(password)
        .setImage(imagePath)
        .setToken(token)
        .setChatUserId(chatUserId)
        .build()
}

fun UserCredential.asChatAppUser(): ChatAppUser {
    return ChatAppUser(
        username = username, password = password, imagePath = image, token = token,
        chatUserId = chatUserId
    )
}



