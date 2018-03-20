package dev.olog.msc.pro

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.billingclient.api.*
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

interface IBilling {

    fun isPremium(): Boolean
    fun observeIsPremium(): Observable<Boolean>
    fun purchasePremium()

}

private const val PRO_VERSION_ID = "pro_version"
private const val DEFAULT_PREMIUM = true
private const val DEFAULT_TRIAL = true

class BillingImpl @Inject constructor(
        private val activity: AppCompatActivity

) : IBilling, PurchasesUpdatedListener, DefaultLifecycleObserver {

    private var isConnected = false

    private val premiumPublisher = BehaviorSubject.createDefault(DEFAULT_PREMIUM)
    private val trialPublisher = BehaviorSubject.createDefault(DEFAULT_TRIAL)

    private var isTrialState by Delegates.observable(DEFAULT_TRIAL, { _, _, new ->
        trialPublisher.onNext(new)
    })

    private var isPremiumState by Delegates.observable(DEFAULT_PREMIUM, { _, _, new ->
        premiumPublisher.onNext(new)
    })


    private val billingClient = BillingClient.newBuilder(activity)
            .setListener(this)
            .build()

    private var countDownDisposable : Disposable? = null

    init {
        activity.lifecycle.addObserver(this)
        startConnection { checkPurchases() }

        val packageInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
        val firstInstallTime = packageInfo.firstInstallTime
        val trialTime = TimeUnit.HOURS.toMillis(1L)
        if (System.currentTimeMillis() - firstInstallTime < trialTime){
            isTrialState = true
            countDownDisposable = Observable.interval(5, TimeUnit.MINUTES)
                    .map { System.currentTimeMillis() - firstInstallTime < trialTime }
                    .doOnNext { isTrialState = it }
                    .takeWhile { it }
                    .subscribe({}, Throwable::printStackTrace)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (billingClient.isReady){
            billingClient.endConnection()
        }
        countDownDisposable.unsubscribe()
    }

    private fun startConnection(func: (() -> Unit)?){
        if (isConnected){
            func?.invoke()
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(responseCode: Int) {
                println("onBillingSetupFinished with response code:$responseCode")

                if (responseCode == BillingClient.BillingResponse.OK){
                    isConnected = true
                }
                func?.invoke()
            }
            override fun onBillingServiceDisconnected() {
                isConnected = false
            }
        })
    }

    private fun checkPurchases(){
        val purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        if (purchases.responseCode == BillingClient.BillingResponse.OK){
            isPremiumState = isProBought(purchases.purchasesList)
        }
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        when (responseCode){
            BillingClient.BillingResponse.OK -> {
                println("purchased")
                isPremiumState = isProBought(purchases)
            }
            BillingClient.BillingResponse.USER_CANCELED -> {
                println("user cancelled purchasing flow")
                // Handle an error caused by a user cancelling the purchase flow.
            }
            else -> Log.w("Billing", "billing response code=$responseCode")
        }
    }

    private fun isProBought(purchases: MutableList<Purchase>?): Boolean {
//        return purchases?.firstOrNull { it.sku == PRO_VERSION_ID } != null
        return true
    }

    override fun isPremium(): Boolean = isTrialState || isPremiumState

    override fun observeIsPremium(): Observable<Boolean> {
        return Observables.combineLatest(premiumPublisher, trialPublisher,
                { premium, trial ->
                    println("premium $premium")
                    println("trial $trial")
                    premium || trial
                })
    }

    override fun purchasePremium() {
        startConnection {
            val params = BillingFlowParams.newBuilder()
                    .setSku(PRO_VERSION_ID)
                    .setType(BillingClient.SkuType.INAPP)
                    .build()

            billingClient.launchBillingFlow(activity, params)
        }
    }
}