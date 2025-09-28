package com.esc.begu.mediapipe

/**
 * Constantes para los índices de landmarks que coinciden con el entrenamiento
 */

object LandmarkIndices {

    // Índices de puntos faciales seleccionados
    val FACE_SELECTED_INDICES = run {
        val contourIndices = listOf(10, 338, 297, 332, 284, 251, 389, 356, 454, 323, 361, 288, 397, 365, 379, 378, 400, 377, 152, 148, 176, 149, 150, 136, 172, 58, 132, 93, 234, 127, 162, 21, 54, 103, 67, 109)
        val leftEyeIndices = listOf(263, 249, 390, 373, 374, 380, 381, 382, 362, 466, 388, 387, 386, 385, 384, 398)
        val rightEyeIndices = listOf(33, 7, 163, 144, 145, 153, 154, 155, 133, 246, 161, 160, 159, 158, 157, 173)
        val mouthIndices = listOf(61, 146, 91, 181, 84, 17, 314, 405, 321, 375, 291, 185, 40, 39, 37, 0, 267, 269, 270, 409, 78, 95, 88, 178, 87, 14, 317, 402, 318, 324, 308, 191, 80, 81, 82, 13, 312, 311, 310, 415)
        val noseIndices = listOf(193, 168, 417, 122, 351, 196, 419, 3, 248, 236, 456, 198, 420, 131, 360, 49, 279, 48, 278, 219, 439, 59, 289, 218, 438, 237, 457, 44, 19, 274)
        val leftEyebrowIndices = listOf(70, 63, 105, 66, 107, 55, 65, 52, 53, 46)
        val rightEyebrowIndices = listOf(300, 293, 334, 296, 336, 285, 295, 282, 283, 276)

        // Combinar todos los índices y eliminar duplicados
        (contourIndices + leftEyeIndices + rightEyeIndices + mouthIndices + noseIndices + leftEyebrowIndices + rightEyebrowIndices).distinct().sorted()
    }

    // Índices de puntos de pose (hombros, codos, muñecas)
    val POSE_SELECTED_INDICES = listOf(11, 13, 15, 12, 14, 16)

    // Dimensiones esperadas
    const val TOTAL_FEATURES = 84 + 316 + 12   // 412 características totales

}