package com.fpoly.smartlunch.ui.main.product

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.databinding.ActivityProdoctBinding
import javax.inject.Inject

class ProductActivity : PolyBaseActivity<ActivityProdoctBinding>(),ProductViewModel.Factory{

    override fun getBinding(): ActivityProdoctBinding =
       ActivityProdoctBinding.inflate(layoutInflater)

    @Inject
    lateinit var productViewModelFactory: ProductViewModel.Factory

    val navigationController : NavController by lazy { Navigation.findNavController(views.root.findViewById(R.id.fragment_component_product)) }
    val productViewModel : ProductViewModel by viewModel  ()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyConponent.inject(this)
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        if (bundle != null) {
            val productId = bundle.getString("id")
            val sizeId = bundle.getString("id_size")
            productViewModel.handle(ProductAction.oneProduct(productId))
            productViewModel.handle(ProductAction.oneSize(sizeId))

        }

        productViewModel.observeViewEvents {
            handleLoginViewEvent(it)
        }

    }
    private fun handleLoginViewEvent(it: ProductEvent?) {
        when(it){
            is ProductEvent.ToFragmentViewEvent -> handleNavigateTo(it.id)
            is ProductEvent.ReturnFragmentViewEvent -> handleReturnNavigation()
            else -> {}
        }
    }
    private fun handleNavigateTo(id : Int) {
        navigationController.navigate(id)
    }
    private fun handleReturnNavigation() {
        navigationController.navigateUp()
    }

    override fun create(initialState: ProductState): ProductViewModel {
        return  productViewModelFactory.create(initialState)
    }

}