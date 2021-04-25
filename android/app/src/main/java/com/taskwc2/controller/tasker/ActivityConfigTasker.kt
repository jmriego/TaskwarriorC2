package com.taskwc2.controller.tasker

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.viewbinding.ViewBinding
import com.joaomgcd.taskerpluginlibrary.SimpleResultError
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner
import com.taskwc2.databinding.ActivityConfigTaskwarriorRunBinding
import org.kvj.bravo7.log.Logger

/**
 * Base class for all the ConfigActivities in this example. This is totally optional. You can use any base class you want as long as it implements TaskerPluginConfig
 */
abstract class ActivityConfigTasker<TInput : Any, TOutput : Any, TActionRunner : TaskerPluginRunner<TInput, TOutput>, THelper : TaskerPluginConfigHelper<TInput, TOutput, TActionRunner>, B : ViewBinding> : Activity(), TaskerPluginConfig<TInput> {
    abstract fun getNewHelper(config: TaskerPluginConfig<TInput>): THelper
    abstract val layoutResId: Int

    protected val taskerHelper by lazy { getNewHelper(this) }
    lateinit var binding: B

    open val isConfigurable = true
    override val context get() = applicationContext
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var logger = Logger.forInstance(this)
        logger.d("binding initialized")
        binding = getViewBinding()
        val view = binding.root
        setContentView(view)

        if (!isConfigurable) {
            taskerHelper.finishForTasker()
            return
        }
        taskerHelper.onCreate()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            val result = taskerHelper.onBackPressed()
            if (result is SimpleResultError) {
                alert("Warning", "Settings are not valid:\n\n${result.message}")
            }
            return result.success
        } else super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
    }

    abstract fun getViewBinding(): B
}


abstract class ActivityConfigTaskerNoOutput<TInput : Any, TActionRunner : TaskerPluginRunner<TInput, Unit>, THelper : TaskerPluginConfigHelper<TInput, Unit, TActionRunner>> : ActivityConfigTasker<TInput, Unit, TActionRunner, THelper, ViewBinding>()

abstract class ActivityConfigTaskerNoInput<TOutput : Any, TActionRunner : TaskerPluginRunner<Unit, TOutput>, THelper : TaskerPluginConfigHelper<Unit, TOutput, TActionRunner>> : ActivityConfigTasker<Unit, TOutput, TActionRunner, THelper, ViewBinding>() {
    override fun assignFromInput(input: TaskerInput<Unit>) {}
    override val inputForTasker = TaskerInput(Unit)
    override val layoutResId = 0
    override val isConfigurable = false
}

abstract class ActivityConfigTaskerNoOutputOrInput<TActionRunner : TaskerPluginRunner<Unit, Unit>, THelper : TaskerPluginConfigHelper<Unit, Unit, TActionRunner>> : ActivityConfigTaskerNoInput<Unit, TActionRunner, THelper>()