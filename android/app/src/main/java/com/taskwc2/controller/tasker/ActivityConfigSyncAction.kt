package com.taskwc2.controller.tasker

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigNoInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.taskwc2.App
import com.taskwc2.controller.data.Controller
import org.kvj.bravo7.log.Logger
import org.kvj.bravo7.util.Tasks.SimpleTask

class SyncActionHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<SyncActionRunner>(config) {
    override val runnerClass: Class<SyncActionRunner> get() = SyncActionRunner::class.java
    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        blurbBuilder.append("Sync with the Taskserver")
    }
}

class ActivityConfigSyncAction : Activity(), TaskerPluginConfigNoInput {
    override val context get() = applicationContext
    private val taskerHelper by lazy { SyncActionHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskerHelper.finishForTasker()
    }
}

class SyncActionRunner : TaskerPluginRunnerActionNoOutputOrInput() {
    var controller: Controller = App.controller()
    var logger = Logger.forInstance(this)

    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<Unit> {
        val account: String = controller.defaultAccount()
        val acc = controller.accountController(account, false)
        acc.taskSync()
        return TaskerPluginResultSucess()
    }
}
