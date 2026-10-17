package com.exemplo.winepc

import android.content.Context
import android.util.DisplayMetrics
import java.io.File

/**
 * Inicia o Wine (ARM64, via Box64) com o .exe escolhido.
 * O runtime deve estar em filesDir/runtime/ com:
 *   bin/box64, bin/wine64 e lib/
 */
class WineLauncher(private val context: Context) {

    private val runtimeDir = File(context.filesDir, "runtime")
    private val prefixDir = File(context.filesDir, "wineprefix")
    private val logFile = File(context.filesDir, "wine.log")

    fun run(exe: File, fullscreen: Boolean, scaling: Boolean) {
        val box64 = File(runtimeDir, "bin/box64")
        val wine = File(runtimeDir, "bin/wine64")

        if (!box64.exists() || !wine.exists()) {
            throw IllegalStateException(
                "Runtime não encontrado em ${runtimeDir.path}. Veja o README."
            )
        }
        prefixDir.mkdirs()

        val dm: DisplayMetrics = context.resources.displayMetrics
        val size = if (fullscreen) "${dm.widthPixels}x${dm.heightPixels}" else "1280x720"

        // "explorer /desktop=" cria um desktop virtual do Wine na resolução escolhida.
        val cmd = listOf(
            box64.absolutePath,
            wine.absolutePath,
            "explorer", "/desktop=WinePC,$size",
            exe.absolutePath
        )

        val pb = ProcessBuilder(cmd)
        pb.environment().apply {
            put("WINEPREFIX", prefixDir.absolutePath)
            put("WINEDEBUG", "-all")
            put("BOX64_LOG", "0")
            put("LD_LIBRARY_PATH", "${runtimeDir.absolutePath}/lib")
            put("WINEESYNC", "1")
            if (scaling) put("WINEPC_UPSCALE", "1") // flag lida pela camada de renderização
        }
        pb.directory(exe.parentFile)
        pb.redirectErrorStream(true)
        pb.redirectOutput(logFile)
        pb.start()
    }
}
