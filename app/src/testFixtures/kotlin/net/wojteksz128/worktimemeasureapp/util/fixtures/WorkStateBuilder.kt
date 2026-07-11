@file:Suppress("unused")

package net.wojteksz128.worktimemeasureapp.util.fixtures

import net.wojteksz128.worktimemeasureapp.model.WorkState
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

fun aLoadingState() = WorkState.Loading

fun aNotStartedState(
    block: WorkStateBuilder<WorkState.NotStarted>.() -> Unit = {},
): WorkState.NotStarted = WorkStateBuilder(WorkState.NotStarted::class).apply(block).build()

fun aInProgressState(
    block: WorkStateBuilder<WorkState.InProgress>.() -> Unit = {},
): WorkState.InProgress = WorkStateBuilder(WorkState.InProgress::class)
    .apply {
        workDay = aWorkDay {
            events(aNotEndedComeEvent())
        }
        block()
    }.build()

fun aFinishedState(
    block: WorkStateBuilder<WorkState.Finished>.() -> Unit = {},
): WorkState.Finished = WorkStateBuilder(WorkState.Finished::class).apply(block).build()

class WorkStateBuilder<T : WorkState.Loaded>(private val stateClass: KClass<T>) {

    var currentTime = TestFixtures.DEFAULT_NOW
    var workDay = aWorkDay()
    var workTimeRequirements = aWorkTimeRequirements()

    fun build(): T {
        val constructor = stateClass.primaryConstructor
            ?: throw IllegalArgumentException("Primary constructor for ${stateClass.qualifiedName} not found")

        val builderProperties = this::class.memberProperties
            .associate { property ->
                property.getter.isAccessible = true
                property.name to property.getter.call(this)
            }

        val argumentMap = constructor.parameters.mapNotNull { parameter ->
            if (builderProperties.containsKey(parameter.name))
                parameter to builderProperties[parameter.name]
            else if (parameter.isOptional)
                null
            else if (parameter.type.isMarkedNullable)
                parameter to null
            else
                throw IllegalArgumentException(
                    "Builder not support filling '${parameter.name}', but required by the ${stateClass.qualifiedName} constructor"
                )
        }.toMap()

        return constructor.callBy(argumentMap)
    }
}