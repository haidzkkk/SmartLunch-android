package com.fpoly.smartlunch.ui.main.profile

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.AddressRequest
import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.UpdateUserRequest
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import java.io.File

sealed class UserViewAction: PolyViewAction {
    object LogOutUser: UserViewAction()
    object GetCurrentUser: UserViewAction()
    data class GetUserById(val id: String): UserViewAction()
    data class DeleteAddressById(val id: String): UserViewAction()
    data class UpdateAddress(val id: String): UserViewAction()
    object GetListAddress: UserViewAction()
    data class GetAddressById(val id: String): UserViewAction()
    data class AddAddress(val addressRequest: AddressRequest): UserViewAction()
    data class ChangePasswordUser(val changePassword: ChangePassword): UserViewAction()
    data class UpdateUser(val updateUser: UpdateUserRequest): UserViewAction()
    data class UploadAvatar(val avatar: File): UserViewAction()

    object GetProvinceAddress : UserViewAction()
    data class GetDistrictAddress(val provinceId: String) : UserViewAction()
    data class GetWardAddress(val districtId: String) : UserViewAction()
}