package com.exemplo.winepc

/**
 * Controle do upscaling. Aqui fica apenas o estado (ligado/desligado) e o fator.
 * A aplicação real do shader (ex.: FSR/CAS) precisa ser feita na camada de
 * renderização (GLSurfaceView) que recebe o frame do Wine.
 */
object ScalingRenderer {
    var enabled = false
        private set

    var scale = 1.5f

    fun toggle(): Boolean {
        enabled = !enabled
        return enabled
    }
}
