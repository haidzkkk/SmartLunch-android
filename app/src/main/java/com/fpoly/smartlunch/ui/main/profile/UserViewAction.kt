package com.fpoly.smartlunch.ui.main.profile

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.AddressRequest
import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.UpdateUserRequest
import java.io.File

sealed class UserViewAction: PolyViewAction {
    object GetCurrentUser: UserViewAction()
    data class GetUserById(val id: String): UserViewAction()
    data class DeleteAddressById(val id: String): UserViewAction()
    data class GetAddressById(val id: String): UserViewAction()
    data class AddAddress(val addressRequest: AddressRequest): UserViewAction()
    data class ChangePasswordUser(val changePassword: ChangePassword): UserViewAction()
    data class UpdateUser(val updateUser: UpdateUserRequest): UserViewAction()
    data class UploadAvatar(val avatar: File): UserViewAction()
}