package com.taskwc2.controller.tasker

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerAction
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputForConfig
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputsForConfig
import com.joaomgcd.taskerpluginlibrary.input.*
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputObject
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputVariable
import com.joaomgcd.taskerpluginlibrary.output.runner.TaskerOutputForRunner
import com.joaomgcd.taskerpluginlibrary.output.runner.TaskerOutputsForRunner
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner
import com.taskwc2.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class GetCommandRunner : TaskerPluginRunnerAction<GetCommandInput, GetCommandOutput>() {

    //A custom notification icon is set for the foreground notification the action will have if the app targets API 26 or above
    override val notificationProperties get() = TaskerPluginRunner.NotificationProperties(iconResId = R.drawable.ic_tasks_list) // TODO: check better icon. maybe proper taskwarrior one
    override fun run(context: Context, input: TaskerInput<GetCommandInput>): TaskerPluginResult<GetCommandOutput> {
        var cmdStdout = with(URL("https://api.ipify.org").openConnection() as HttpURLConnection) {
            BufferedReader(InputStreamReader(inputStream)).use { it.readLine() }
        }
        return TaskerPluginResultSucess(GetCommandOutput(cmdStdout), null)
    }

}


@TaskerInputRoot
class GetCommandInput @JvmOverloads constructor(
        @field:TaskerInputField("command", R.string.command) var command: String? = null,
)


@TaskerOutputObject
class GetCommandOutput(
        @get:TaskerOutputVariable(VAR_STDOUT, R.string.stdout, R.string.stdout_description) var cmdStdout: String?
){
    companion object {
        const val VAR_STDOUT = "stdout"
    }
}


class GetIPHelper(config: TaskerPluginConfig<GetCommandInput>) : TaskerPluginConfigHelper<GetCommandInput, GetCommandOutput, GetCommandRunner>(config) {
    override val runnerClass = GetCommandRunner::class.java
    override val inputClass = GetCommandInput::class.java
    override val outputClass = GetCommandOutput::class.java

    //splitip output info is added dynamically depending on the split option in the input. Check the GetCommandRunner to check how this is added as the output data
    override fun addOutputs(input: TaskerInput<GetCommandInput>, output: TaskerOutputsForConfig) {
        super.addOutputs(input, output)
    }
}

class ActivityConfigTaskwarriorRun : ActivityConfigTasker<GetCommandInput, GetCommandOutput, GetCommandRunner, GetIPHelper>() {
    //Overrides
    override fun getNewHelper(config: TaskerPluginConfig<GetCommandInput>) = GetIPHelper(config)

    override fun assignFromInput(input: TaskerInput<GetCommandInput>) = input.regular.run {
        editTextCommand.setText(command)
    }

    override val inputForTasker get() = TaskerInput(GetCommandInput(editTextCommand.text?.toString()))
    override val layoutResId = R.layout.activity_config_taskwarrior_run


}