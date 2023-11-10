package com.fpoly.smartlunch.ultis

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
    paymentTypies.add(Menu("2", "Momo", "Ví điện tử momo"))
    paymentTypies.add(Menu("3", "Zalopay", "Ví điện tử zalopay"))
    paymentTypies.add(Menu("4", "Vnpay", "Ví điện tử vnpay"))
    return Observable.just(paymentTypies)

}