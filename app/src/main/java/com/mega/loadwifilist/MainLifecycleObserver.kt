package com.mega.loadwifilist

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MainLifecycleObserver(private val registry : ActivityResultRegistry, private val output: MainFragment.URICallback)
    : DefaultLifecycleObserver {
    lateinit var getContent : ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, ActivityResultContracts.GetContent()) { uri ->
            output.onUriAcquired(uri)
        }
    }

    fun selectFile() {
        getContent.launch("*/*")
    }
}