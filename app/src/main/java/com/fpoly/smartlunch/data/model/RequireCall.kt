package com.fpoly.smartlunch.data.model

object RequireCallType{
    const val START_CALL = "start_call"
    const val CREATE_OFFER = "create_offer"
    const val CREATE_ANSWER = "create_answer"
    const val CREATE_STOP = "create_stop"
    const val ICE_CANDIDATE = "ice_candidate"

    const val CALL_RESPONSE = "call_response"
    const val OFFER_RECEIVED = "offer_received"
    const val ANSWER_RECEIVED = "answer_received"
    const val STOP_RECEIVED = "stop_received"

    const val OK = "ok"
    const val NO = "no"

}

data class RequireCall(
    val type: String? = null,
    var myUserId: String? = null,
    var targetUserId: String? = null,
    val data: Any? = null
)