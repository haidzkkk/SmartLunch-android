package com.fpoly.smartlunch.ultis

import com.fpoly.smartlunch.data.model.Banner
import com.fpoly.smartlunch.data.model.Image
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.data.model.TokenResponse
import io.reactivex.Observable

fun getFakeTokenLogin(): Observable<TokenResponse> {
    return Observable.just(TokenResponse("accessToken", "refreshTOken"))
}

fun getTypePayments(): Observable<ArrayList<Menu>> {
    var paymentTypies = arrayListOf<Menu>()
    paymentTypies.add(Menu("0", "Tiền mặt", "Thanh toán khi nhận hàng"))
    paymentTypies.add(Menu("1", "Paypal", "Ví điện tử paypal"))
    paymentTypies.add(Menu("3", "Zalopay", "Ví điện tử zalopay"))
    return Observable.just(paymentTypies)
}

fun getRateComment(): ArrayList<Menu> {
    var rateTypies = arrayListOf<Menu>()
    rateTypies.add(Menu("1", "1 sao", ""))
    rateTypies.add(Menu("2", "2 sao", ""))
    rateTypies.add(Menu("3", "3 sao", ""))
    rateTypies.add(Menu("4", "4 sao", ""))
    rateTypies.add(Menu("5", "5 sao", ""))
    return rateTypies
}

fun getListBanner(): ArrayList<Banner>{
    var list: ArrayList<Banner> = arrayListOf()
    list.add(Banner("0", 0, Image("a", "https://treobangron.com.vn/wp-content/uploads/2022/12/banner-quang-cao-nha-hang-32.jpg"), ""))
    list.add(Banner("1", 0, Image("a", "https://intphcm.com/data/upload/mau-banner-dep.jpg"), ""))
    list.add(Banner("2", 0, Image("a", "https://inan2h.vn/wp-content/uploads/2022/12/in-banner-quang-cao-do-an-10-1.jpg"), ""))
    list.add(Banner("3", 0, Image("a", "https://inan2h.vn/wp-content/uploads/2022/12/in-banner-quang-cao-do-an-6-1.jpg"), ""))

    return list
}