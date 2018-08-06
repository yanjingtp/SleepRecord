package cn.yanjingtp.sleeprecord.application

import android.app.Application
import com.tinkerpatch.sdk.TinkerPatch
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //我们可以从这里获取Tinker加载过程的信息
        val tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike()

        //初始化TinkerPatch SDK,更多配置可以参照API章节的，初始化SDK
        TinkerPatch.init(tinkerApplicationLike)
                .reflectPatchLibrary()
                .setPatchRollbackOnScreenOff(true)
                .setPatchRestartOnSrceenOff(true)
                .setFetchPatchIntervalByHours(1)

        //每隔1个小时（通过setFetchPatchIntervalByHours设置）去访问后台是否有更新，通过handler实现轮询的效果
        TinkerPatch.with().fetchPatchUpdateAndPollWithInterval()

        //每次登陆时都检查是否有更新
        TinkerPatch.with().fetchPatchUpdate(true)
    }
}