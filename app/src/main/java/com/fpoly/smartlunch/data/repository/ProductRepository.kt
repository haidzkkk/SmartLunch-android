package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.CategoryResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.Favourite
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size

import com.fpoly.smartlunch.data.network.ProductApi
import io.reactivex.Completable

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.Query
import javax.inject.Inject
import kotlin.random.Random


class ProductRepository @Inject constructor(
    private val api: ProductApi
) {
    private var number = Random.nextInt()

    fun test(): Observable<String> {
        Thread.sleep(2000)
        return Observable.just("Repository test: $number")
    }

    fun getProducts(): Observable<ProductsResponse> = api.getAllProduct().subscribeOn(Schedulers.io())
    fun getOneProducts(id : String): Observable<Product> = api.getOneProduct(id).subscribeOn(Schedulers.io())
    fun getOneSize(id : String): Observable<Size> = api.getOneSize(id).subscribeOn(Schedulers.io())
    fun getSize(): Observable<List<Size>> = api.getAllSize().subscribeOn(Schedulers.io())
    fun getCreateCart(id :String,cart: CartRequest): Observable<CartResponse> = api.getCreateCart(id,cart).subscribeOn(Schedulers.io())
    fun getOneCartById(id : String): Observable<CartResponse> = api.getOneCartById(id).subscribeOn(Schedulers.io())
    fun getClearCart(id :String): Observable<CartResponse> = api.getClearCart(id).subscribeOn(Schedulers.io())
    fun getChangeQuantityCart(id : String,idProduct : String , changeQuantityRequest: ChangeQuantityRequest) : Observable<CartResponse> = api.getChangeQuantityCart(id,idProduct,changeQuantityRequest).subscribeOn(Schedulers.io())
    fun getRemoveGetOneProductCart(id : String, idProduct : String, sizeId : String) : Observable<CartResponse> = api.getRemoveGetOneProductCart(id,idProduct,sizeId).subscribeOn(Schedulers.io())
    fun getAllCategory(): Observable<CategoryResponse> = api.getAllCategory().subscribeOn(Schedulers.io())
    fun getAllProductByIdCategory(id : String): Observable<List<Product>> = api.getAllProductByIdCategory(id).subscribeOn(Schedulers.io())
    fun getViewProduct(id : String) : Completable = api.getViewProduct(id).subscribeOn(Schedulers.io())
    fun getAllFavourite(): Observable<List<Favourite>> = api.getAllFavourite().subscribeOn(Schedulers.io())


}