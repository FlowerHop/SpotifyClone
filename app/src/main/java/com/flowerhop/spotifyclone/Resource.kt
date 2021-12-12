package com.flowerhop.spotifyclone

data class Resource<out T>(val state: State, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?) = Resource(State.SUCCESS, data, null)

        fun <T> error(message: String?, data: T?) = Resource(State.ERROR, data, message)

        fun <T> loading(data: T?) = Resource(State.LOADING, data, null)
    }
}

enum class State {
    SUCCESS, ERROR, LOADING
}