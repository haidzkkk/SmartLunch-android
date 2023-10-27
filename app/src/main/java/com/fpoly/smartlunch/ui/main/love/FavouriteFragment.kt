package com.fpoly.smartlunch.ui.main.love

import android.view.LayoutInflater
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentFavouriteBinding
import com.fpoly.smartlunch.databinding.FragmentProductListBinding
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import javax.inject.Inject


class FavouriteFragment @Inject constructor(): PolyBaseFragment<FragmentFavouriteBinding>() {

    private val favouriteViewModel: FavouriteViewModel by activityViewModel()
    companion object{
        const val TAG = "FavouriteFragment"
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouriteBinding {
        return FragmentFavouriteBinding.inflate(inflater)
    }

    override fun invalidate() : Unit = withState(favouriteViewModel) {

        when(it.Favourite){
            is Success ->{

            }
            else -> {}
        }
    }


}