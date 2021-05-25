package com.taskwc2.controller.tasker

//import android.support.v7.app.AppCompatActivity
import android.content.Context
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerAction
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputObject
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputVariable
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputsForConfig
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginRunner
import com.taskwc2.App
import com.taskwc2.R
import com.taskwc2.controller.data.Controller
import com.taskwc2.databinding.ActivityConfigTaskwarriorRunBinding
import java.util.regex.Matcher
import java.util.regex.Pattern


class GetCommandRunner : TaskerPluginRunnerAction<GetCommandInput, GetCommandOutput>() {
    //A custom notification icon is set for the foreground notification the action will have if the app targets API 26 or above
    override val notificationProperties get() = TaskerPluginRunner.NotificationProperties(iconResId = R.drawable.ic_tasks_list) // TODO: check better icon. maybe proper taskwarrior one
    override fun run(context: Context, input: TaskerInput<GetCommandInput>): TaskerPluginResult<GetCommandOutput> {
        var controller: Controller = App.controller()
        val account: String = controller.defaultAccount()
        val acc = controller.accountController(account, false)
        val query = input.regular.command.toString()
//        val query_split = StringTokenizer(query, ' ', '"').tokenArray
        val regex = "\"([^\"]*)\"|(\\S+)"
        val m: Matcher = Pattern.compile(regex).matcher(query)
        var query_split: List<String> = ArrayList()
        while (m.find()) {
            if (m.group(1) != null) {
                query_split += m.group(1)
            } else {
                query_split += m.group(2)
            }
        }
        var results = acc.taskRun(*query_split.toTypedArray())
        return TaskerPluginResultSucess(GetCommandOutput(results.stdout), null)
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


class GetCommandHelper(config: TaskerPluginConfig<GetCommandInput>) : TaskerPluginConfigHelper<GetCommandInput, GetCommandOutput, GetCommandRunner>(config) {
    override val runnerClass = GetCommandRunner::class.java
    override val inputClass = GetCommandInput::class.java
    override val outputClass = GetCommandOutput::class.java

    //splitip output info is added dynamically depending on the split option in the input. Check the GetCommandRunner to check how this is added as the output data
    override fun addOutputs(input: TaskerInput<GetCommandInput>, output: TaskerOutputsForConfig) {
        super.addOutputs(input, output)
    }
}

class ActivityConfigTaskwarriorRun : ActivityConfigTasker<GetCommandInput, GetCommandOutput, GetCommandRunner, GetCommandHelper, ActivityConfigTaskwarriorRunBinding>() {
    //Overrides
    var controller: Controller = App.controller()

    override fun getViewBinding() = ActivityConfigTaskwarriorRunBinding.inflate(layoutInflater)
    override fun getNewHelper(config: TaskerPluginConfig<GetCommandInput>) = GetCommandHelper(config)

    override fun assignFromInput(input: TaskerInput<GetCommandInput>) = input.regular.run {
        binding.editTextCommand.setText(command)
    }

    override val layoutResId = R.layout.activity_config_taskwarrior_run
    override val inputForTasker get() = TaskerInput(GetCommandInput(binding.editTextCommand.text?.toString()))
//    override val inputForTasker get() = TaskerInput(GetCommandInput("next"))


}
