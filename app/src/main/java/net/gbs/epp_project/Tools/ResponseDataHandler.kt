package net.gbs.epp_project.Tools

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.gbs.epp_project.Base.BaseRepository
import net.gbs.epp_project.Base.BaseResponse
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.Network.ApiFactory.ApiFactory
import net.gbs.epp_project.Network.ApiInterface.ApiInterface
import net.gbs.epp_project.R
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import retrofit2.Response

class ResponseDataHandler<T : BaseResponse<D>, D>(
    val response: Response<T>,
    private val responseData: SingleLiveEvent<D>,
    private val statusWithMessage: SingleLiveEvent<StatusWithMessage>,
    val application: Application,
) {
    @OptIn(DelicateCoroutinesApi::class)
    fun handleData(apiName:String?) {
        try {
            if (response.isSuccessful) {
                if (response.body()?.responseStatus?.isSuccess!!) {
                    responseData.postValue(response.body()?.getData()!!)
                    statusWithMessage.postValue(
                        StatusWithMessage(
                            Status.SUCCESS,
                            response.body()?.responseStatus?.statusMessage!!
                        )
                    )
                } else {
                    if (USER?.isShowErrorMessage != null) {
                        if (USER?.isShowErrorMessage!!) {
                            if (response.body()?.responseStatus?.errorMessage != null) {
                                statusWithMessage.postValue(
                                    StatusWithMessage(
                                        Status.ERROR,
                                        response.body()?.responseStatus?.errorMessage!!
                                    )
                                )
                            } else {
                                statusWithMessage.postValue(
                                    StatusWithMessage(
                                        Status.ERROR,
                                        response.body()?.responseStatus?.statusMessage!!
                                    )
                                )
                            }
                        } else {
                            statusWithMessage.postValue(
                                StatusWithMessage(
                                    Status.ERROR,
                                    response.body()?.responseStatus?.statusMessage!!
                                )
                            )
                        }
                    } else {
                        statusWithMessage.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                }
            } else {
                statusWithMessage.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, response.message()
//                        application.getString(
//                            R.string.error_in_getting_data
//                        )
                    )
                )

            }

        } catch (ex: Exception) {
            statusWithMessage.postValue(
                StatusWithMessage(
                    Status.NETWORK_FAIL,
//                    application.getString(
//                        R.string.error_in_getting_data
//                    )
                    ex.message.toString()
                )
            )
            Log.e(TAG, "handleData: ", ex)
        }
    }

}