package com.taskwc2.controller.tasker

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.os.Build.VERSION
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.support.annotation.RequiresApi
import com.joaomgcd.taskerpluginlibrary.SimpleResult
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoInput
import com.joaomgcd.taskerpluginlibrary.config.*

import com.joaomgcd.taskerpluginlibrary.extensions.requestQuery
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.extensions.requestQuery
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess


class TaskwarriorChangeEventHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperNoOutputOrInput<TaskwarriorChangeEventRunner>(config) {
    override val runnerClass: Class<TaskwarriorChangeEventRunner> get() = TaskwarriorChangeEventRunner::class.java
    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        blurbBuilder.append("Will trigger this app's Tasker event")
    }
}


class TaskwarriorChangeEventRunner : TaskerPluginRunnerActionNoOutputOrInput() {
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<Unit> {
//        if (!context.canDrawOverlays) throw RuntimeException("Have to be able to draw overlays to launch activities from background")
        context.startActivity(Intent(context, ActivityBackgroundWork::class.java))
        return TaskerPluginResultSucess()
    }
}


class ActivityConfigTaskwarriorChangeEvent : Activity(), TaskerPluginConfigNoInput {
    override val context get() = applicationContext
    private val taskerHelper by lazy { TaskwarriorChangeEventHelper(this) }
    private val permissionRequestCode = 12
    private fun finishForTasker() {
        taskerHelper.finishForTasker()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (!Settings.canDrawOverlays(getApplicationContext()) )
//        {
//            "Enable \"Tasker Plugin Sample\" on this list".toToast(this)
//            val uri = Uri.parse("package:$packageName")
//            val intent = Intent(ACTION_MANAGE_OVERLAY_PERMISSION).setData(uri)
//            startActivityForResult(intent, permissionRequestCode)
//        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finishForTasker()
    }
}