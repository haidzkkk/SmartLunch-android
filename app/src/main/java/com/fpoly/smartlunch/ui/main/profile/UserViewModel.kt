package com.fpoly.smartlunch.ui.main.profile

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.AddressRequest
import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.UpdateUserRequest
import com.fpoly.smartlunch.data.repository.UserRepository
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ui.payment.PaymentViewEvent
import com.fpoly.smartlunch.ui.payment.address.AddAddressFragment
import com.fpoly.smartlunch.ui.payment.address.DetailAddressFragment
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
        handleGetListAddress()
    }

    override fun handle(action: UserViewAction) {
        when (action) {
            is UserViewAction.LogOutUser -> handleLogoutUser()
            is UserViewAction.GetCurrentUser -> handleCurrentUser()
            is UserViewAction.GetUserById -> handleGetUserById(action.id)
            is UserViewAction.ChangePasswordUser -> handleChangePassword(action.changePassword)
            is UserViewAction.UpdateUser -> handleUpdateUser(action.updateUser)
            is UserViewAction.UploadAvatar -> handleUploadAvatar(action.avatar)
            is UserViewAction.GetListAddress -> handleGetListAddress()
            is UserViewAction.GetAddressById -> handleGetAddressById(action.id)
            is UserViewAction.AddAddress -> handleAddAddress(action.addressRequest)
            is UserViewAction.UpdateAddress -> handleUpdateAddress(action.id)
            is UserViewAction.DeleteAddressById -> handleDeleteAddressById(action.id)

            is UserViewAction.GetAddressAdmin -> handleGetAddressAdmin()

            is UserViewAction.GetProvinceAddress -> handleGetListProvince()
            is UserViewAction.GetDistrictAddress -> handleGetListDistrict(action.provinceId)
            is UserViewAction.GetWardAddress -> handleGetListWard(action.districtId)
            else -> {}
        }
    }
    private fun handleLogoutUser() {
        setState { copy(asyncLogout = Loading()) }
        repository.logout().execute {
            copy(asyncLogout = it)
        }
    }
    private fun handleDeleteAddressById(id: String) {
        setState { copy(asyncDeleteAddress = Loading()) }
        repository.deleteAddress(id).execute {
            copy(asyncDeleteAddress = it)
        }
    }

    private fun handleAddAddress(addressRequest: AddressRequest) {
        setState { copy(asyncCreateAddress = Loading()) }
        repository.createAddress(addressRequest).execute {
            copy(asyncCreateAddress = it)
        }
    }

    private fun handleGetAddressById(id: String) {
        setState { copy(asyncAddress = Loading()) }
        repository.getAddressById(id).execute {
            copy(asyncAddress = it)
        }
    }

    private fun handleUpdateAddress(id: String) {
        setState { copy(asyncUpdateAddress = Loading()) }
        repository.getUpdateById(id).execute {
            copy(asyncUpdateAddress = it)
        }
    }
    private fun handleGetListAddress() {
        setState { copy(asyncListAddress = Loading()) }
        repository.getAllAddressByUser().execute {
            copy(asyncListAddress = it)
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

    private fun handleGetAddressAdmin() {
        setState { copy(asyncAddressAdmin = Loading()) }
        repository.getAddressAdmin().execute {
            copy(asyncAddressAdmin = it)
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



    private fun handleGetListProvince() {
        setState {
            copy(
                asyncListProvince = Loading(),
                asyncListDistrict = Uninitialized,
                asyncListWard = Uninitialized
            )
        }
        repository.getProvince().execute {
            copy(asyncListProvince = it)
        }
    }

    private fun handleGetListDistrict(provinceId: String) {
        setState { copy(asyncListDistrict = Loading(), asyncListWard = Uninitialized) }
        repository.getDistrict(provinceId).execute {
            copy(asyncListDistrict = it)
        }
    }

    private fun handleGetListWard(districtId: String) {
        setState { copy(asyncListWard = Loading()) }
        repository.getWard(districtId).execute {
            copy(asyncListWard = it)
        }
    }

    fun returnDetailAddressFragment() {
        _viewEvents.post(UserViewEvent.ReturnFragment(DetailAddressFragment::class.java))
    }

    fun returnAddAddressFragment() {
        _viewEvents.post(UserViewEvent.ReturnFragment(AddAddressFragment::class.java))
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