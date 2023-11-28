package com.fpoly.smartlunch.ui.main.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.data.model.UpdateUserRequest
import com.fpoly.smartlunch.databinding.FragmentEditProfileBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ultis.checkPermissionGallery
import com.fpoly.smartlunch.ultis.showDatePickerDialog
import com.fpoly.smartlunch.ultis.showPermissionDeniedToast
import com.fpoly.smartlunch.ultis.showUtilDialog
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileFragment : PolyBaseFragment<FragmentEditProfileBinding>(), TextWatcher {

    private val viewModel: UserViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private var isSendButtonEnabled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        listenEvent()
    }

    private fun setupUI(): Unit = withState(viewModel){
        val user = it.asyncCurrentUser.invoke()
        views.apply {
            appBar.apply {
                btnBackToolbar.visibility = View.VISIBLE
                tvTitleToolbar.text = getString(R.string.edit_profile)
            }
            if (user != null) {
                Glide.with(requireContext())
                    .load(user.avatar?.url)
                    .placeholder(R.drawable.baseline_person_outline_24)
                    .error(R.drawable.baseline_person_outline_24)
                    .into(imgAvatar)
                firstName.setText(user.first_name)
                lastName.setText(user.last_name)
                email.setText(user.email)
                phone.setText(user.phone)
                birthday.setText(user.birthday)
                setGenderRadio(user.gender)
            }
        }
        views.firstName.addTextChangedListener(this)
        views.lastName.addTextChangedListener(this)
        views.email.addTextChangedListener(this)
        views.phone.addTextChangedListener(this)
        views.birthday.addTextChangedListener(this)
    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
           activity?.onBackPressed()
        }
        views.editSubmit.setOnClickListener {
            onSubmitForm()
        }
        views.updateAvatar.setOnClickListener {
            selectImageFromGallery()
        }
        views.birthday.setOnClickListener {
            activity?.showDatePickerDialog {
                views.birthday.setText(it)
            }
        }
    }

    private fun onSubmitForm() {
        val firstName = views.firstName.text.toString()
        val lastName = views.lastName.text.toString()
        val email = views.email.text.toString()
        val phone = views.phone.text.toString()
        val birthday = views.birthday.text.toString()
        val isMale = onRadioButtonClicked()

        val currentUser = withState(viewModel) {
            it.asyncCurrentUser.invoke()
        }
        if (currentUser != null) {
            val updateUser = UpdateUserRequest(
                first_name = firstName,
                last_name = lastName,
                email = email,
                phone = phone,
                birthday = birthday,
                gender = isMale
            )
            viewModel.handle(UserViewAction.UpdateUser(updateUser))
        }
    }

    private fun onRadioButtonClicked(): Boolean {
        val radioGroup = views.gender
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        val selectedRadioButton = views.root.findViewById<RadioButton>(selectedRadioButtonId)
        return selectedRadioButton.id == R.id.male
    }

    private fun setGenderRadio(isMale: Boolean) {
        val views = views
        if (isMale) {
            views.male.isChecked = true
        } else {
            views.female.isChecked = true
        }
    }

    private fun updateSubmitButtonState(){
        val isFieldsFilled = views.run {
            firstName.text?.isNotEmpty() == true &&
                    lastName.text?.isNotEmpty() == true &&
                    email.text?.isNotEmpty() == true &&
                    phone.text?.isNotEmpty() == true &&
                    birthday.text?.isNotEmpty() == true
        }

        isSendButtonEnabled = isFieldsFilled
        views.editSubmit.isEnabled = isSendButtonEnabled
    }

    private fun selectImageFromGallery() {
        checkPermissionGallery { isAllowed ->
            if (isAllowed) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(intent)
            } else {
                showPermissionDeniedToast()
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let { handleGalleryImageSelection(it) }
        }
    }
    private fun handleGalleryImageSelection(uri: Uri) {
        val imageFile = File(uriToFilePath(uri))
        viewModel.handle(UserViewAction.UploadAvatar(imageFile))
    }

    private fun uriToFilePath(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return it.getString(columnIndex)
        }
        return ""
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        homeViewModel.returnVisibleBottomNav(true)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditProfileBinding {
        return FragmentEditProfileBinding.inflate(inflater, container, false)
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncUpdateUser) {
            is Success -> {
                viewModel.handle(UserViewAction.GetCurrentUser)
                activity?.showUtilDialog(
                    Notify(
                        getString(R.string.edit_profile),
                        "${it.asyncUpdateUser.invoke()?.first_name}",
                        getString(R.string.reset_password_success_title),
                        R.raw.animation_successfully
                    )
                )
                activity?.onBackPressed()
                viewModel.handleRemoveAsyncUpdateUser()
            }

            is Fail -> {}
            else -> {}
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        updateSubmitButtonState()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        updateSubmitButtonState()

        if (s != null && s.isNotEmpty()) {
            when (s) {
                views.firstName.text -> views.firstNameTil.error = null
                views.lastName.text -> views.lastNameTil.error = null
                views.email.text -> views.emailTil.error = null
                views.phone.text -> views.phoneTil.error = null
            }
        }
    }
}

