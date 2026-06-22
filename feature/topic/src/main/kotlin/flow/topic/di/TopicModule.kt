package flow.topic.di

import flow.topic.TopicViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val topicModule = module {
    viewModelOf(::TopicViewModel)
}
