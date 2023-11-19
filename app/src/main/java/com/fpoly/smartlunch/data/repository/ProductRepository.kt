package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.Banner
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.CategoryResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.Comment
import com.fpoly.smartlunch.data.model.CommentRequest
import com.fpoly.smartlunch.data.model.Favourite
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.ProductOrder
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.data.network.CommentApi
import com.fpoly.smartlunch.data.network.ProductApi
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

class ProductRepository @Inject constructor(
    private val api: ProductApi,
    private val commentApi: CommentApi,
) {
    private var number = Random.nextInt()

    fun test(): Observable<String> {
        Thread.sleep(2000)
        return Observable.just("Repository test: $number")
    }

    fun getProducts(): Observable<ProductsResponse> = api.getAllProduct().subscribeOn(Schedulers.io())
    fun getOneProducts(id : String): Observable<Product> = api.getOneProduct(id).subscribeOn(Schedulers.io())
    fun getOneSize(id : String): Observable<Size> = api.getOneSize(id).subscribeOn(Schedulers.io())
    fun getSizeProduct(id : String): Observable<ArrayList<Size>> = api.getSizeProduct(id).subscribeOn(Schedulers.io())
    fun getCreateCart(cart: CartRequest): Observable<CartResponse> = api.getCreateCart(cart).subscribeOn(Schedulers.io())
    fun getOneCartById(): Observable<CartResponse> = api.getOneCartById().subscribeOn(Schedulers.io())
    fun getClearCart(): Observable<CartResponse> = api.getClearCart().subscribeOn(Schedulers.io())
    fun getChangeQuantityCart(idProduct : String , changeQuantityRequest: ChangeQuantityRequest) : Observable<CartResponse> = api.getChangeQuantityCart(idProduct,changeQuantityRequest).subscribeOn(Schedulers.io())
    fun getRemoveGetOneProductCart(idProduct : String, sizeId : String) : Observable<CartResponse> = api.getRemoveGetOneProductCart(idProduct,sizeId).subscribeOn(Schedulers.io())
    fun getAllCategory(): Observable<CategoryResponse> = api.getAllCategory().subscribeOn(Schedulers.io())
    fun getAllProductByIdCategory(id : String): Observable<ArrayList<Product>> = api.getAllProductByIdCategory(id).subscribeOn(Schedulers.io())
    fun getViewProduct(id : String) : Completable = api.getViewProduct(id).subscribeOn(Schedulers.io())
    fun getAllFavourite(): Observable<ArrayList<Product>> = api.getAllFavourite().subscribeOn(Schedulers.io())
    fun likeProduct(product: Product): Observable<Favourite> = api.likeProduct(product).subscribeOn(Schedulers.io())
    fun getFavourite(id: String): Observable<Favourite> = api.getFavourite(id).subscribeOn(Schedulers.io())
    fun createOrder(order: OrderRequest)=api.createOrder(order).subscribeOn(Schedulers.io())
    fun updateOrder(order: OrderRequest)=api.updateOrder(order).subscribeOn(Schedulers.io())
    fun updateIsPaymentOrder(id: String, isPayment: Boolean)=api.updateIsPaymentOrder(id, isPayment).subscribeOn(Schedulers.io())
    fun getCoupons(): Observable<ArrayList<CouponsResponse>> = api.getAllCoupons().subscribeOn(Schedulers.io())
    fun applyCoupon(coupons:CouponsRequest): Observable<CartResponse> = api.applyCoupon(coupons).subscribeOn(Schedulers.io())
    fun getAllOrderByUserId(statusId: String): Observable<ArrayList<OrderResponse>> = api.getAllOrderByUserId(statusId).subscribeOn(Schedulers.io())

    fun getCurrentOrder(id: String): Observable<OrderResponse> = api.getCurrentOrder(id).subscribeOn(Schedulers.io())
    fun getTopViewedProducts(): Observable<ArrayList<Product>> = api.getTopViewedProducts().subscribeOn(Schedulers.io())

    fun getCommentProduct(productId: String): Observable<ArrayList<Comment>> = commentApi.getCommentProduct(productId).subscribeOn(Schedulers.io())
    fun getCommentProductLimit(productId: String, limitPosition: Int): Observable<ArrayList<Comment>> = commentApi.getCommentProductLimit(productId, limitPosition).subscribeOn(Schedulers.io())
    fun postMessage(comment: CommentRequest, images: List<Gallery>?): Observable<Comment>{

        val reqBodyProductId: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), comment.productId ?: "")
        val reqBodyOrderId: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), comment.orderId ?: "")
        val reqBodySizeId: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), comment.sizeId ?: "" )
        val reqBodyDescription: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), comment.description ?: "")
        val reqBodyRate: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), (comment.rating).toString())
        val reqBodyImages: ArrayList<MultipartBody.Part> = ArrayList()

        if (images != null){
            val files = images.map { File(it.realPath) }.toList()
            for (file in files){
                if (file != null){
                    val reqBodyImage: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    val multipartBodyImage: MultipartBody.Part = MultipartBody.Part.createFormData("images", file.name, reqBodyImage)
                    reqBodyImages.add(multipartBodyImage)
                }
            }
        }

        return commentApi.postComment(reqBodyProductId, reqBodyOrderId, reqBodySizeId, reqBodyDescription, reqBodyRate, reqBodyImages).subscribeOn(Schedulers.io())
    }

}