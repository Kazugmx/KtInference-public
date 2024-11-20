package org.guardmantokai.libs

import ai.djl.tensorflow.engine.TfEngine


data class ExecArgs(
    var Epoch: Int = 0,
    var BatchSize: Int = 0,
    var MaxGpus: Int = 0,
    var IsSymbolic: Boolean = false,
    var PreTrained: Boolean = false,
    var OutputDir: String = "",
    var Limit: Long = 0,
    var ModelDir: String = "",
    private var flagInit: Boolean = false
) {
    fun main() = init()
    fun init() {
        if(!flagInit) {
            Epoch = 2
            BatchSize = 2
            MaxGpus = TfEngine.getInstance().gpuCount
            OutputDir = "build/model"
            Limit = Long.MAX_VALUE
            ModelDir = ""
            flagInit = true
        }
    }
}