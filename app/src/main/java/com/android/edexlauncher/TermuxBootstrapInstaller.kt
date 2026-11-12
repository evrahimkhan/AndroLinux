package com.android.edexlauncher

import android.content.Context
import java.io.File

object TermuxBootstrapInstaller {
    fun extractBootstrap(context: Context) {
        val filesDir = context.filesDir
        val usrDir = File(filesDir, "usr")
        if (usrDir.exists()) return

        usrDir.mkdirs()
        // In a complete standalone build, the bootstrap tar.xz / zip bundled in assets
        // is extracted here to populate usr/bin, usr/lib, etc.
    }
}
