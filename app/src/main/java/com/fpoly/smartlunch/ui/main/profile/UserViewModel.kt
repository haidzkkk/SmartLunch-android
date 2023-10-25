package com.fpoly.smartlunch.ui.main.profile

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.UpdateUserRequest
import com.fpoly.smartlunch.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UserViewModel @AssistedInject constructor(
    @Assisted state: UserViewState,
    val repository: UserRepository
) : PolyBaseViewModel<UserViewState, UserViewAction, UserViewEvent>(state) {

    init {
        handleCurrentUser()
    }

    override fun handle(action: UserViewAction) {
        when (action) {
            is UserViewAction.GetCurrentUser -> handleCurrentUser()
            is UserViewAction.GetUserById -> handleGetUserById(action.id)
            is UserViewAction.ChangePasswordUser -> handleChangePassword(action.changePassword)
            is UserViewAction.UpdateUser -> handleUpdateUser(action.updateUser)
            is UserViewAction.UploadAvatar -> handleUploadAvatar(action.avatar)
        }
    }

    private fun handleUploadAvatar(avatar: File) {
        val requestFile = avatar.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val avatarMultipartBody =
            MultipartBody.Part.createFormData("images", avatar.name, requestFile)
        setState { copy(asyncUpdateUser = Loading()) }
        repository.uploadAvatar(avatarMultipartBody).execute {
            copy(asyncUpdateUser = it)
        }
    }

    private fun handleUpdateUser(updateUser: UpdateUserRequest) {
        setState { copy(asyncUpdateUser = Loading()) }
        repository.updateUser(updateUser).execute {
            copy(asyncUpdateUser = it)
        }
    }

    private fun handleChangePassword(changePassword: ChangePassword) {
        setState { copy(asyncChangePassword = Loading()) }
        repository.changePassword(changePassword).execute {
            copy(asyncChangePassword = it)
        }
    }

    fun handleRemoveAsyncChangePassword() {
        setState { copy(asyncChangePassword = Uninitialized) }
    }

    fun handleRemoveAsyncUpdateUser() {
        setState { copy(asyncUpdateUser = Uninitialized) }
    }

    private fun handleGetUserById(id: String) {
        setState { copy(asyncUserSearching = Loading()) }
        repository.getUserById(id).execute {
            copy(asyncUserSearching = it)
        }
    }

    private fun handleCurrentUser() {
        setState { copy(asyncCurrentUser = Loading()) }
        repository.getCurrentUser().execute {
            copy(asyncCurrentUser = it)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: UserViewState): UserViewModel
    }

    companion object : MvRxViewModelFactory<UserViewModel, UserViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: UserViewState
        ): UserViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}