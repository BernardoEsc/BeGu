/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esc.begu.mediapipe

import androidx.lifecycle.ViewModel

/**
 *  This ViewModel is used to store hand, face and pose landmarker helper settings
 */

class MultiMainViewModel : ViewModel() {

    private var _delegate: Int = MultiLandmarkerHelper.DELEGATE_CPU
    private var _minDetectionConfidence: Float =
        MultiLandmarkerHelper.DEFAULT_DETECTION_CONFIDENCE
    private var _minTrackingConfidence: Float = MultiLandmarkerHelper
        .DEFAULT_TRACKING_CONFIDENCE
    private var _minPresenceConfidence: Float = MultiLandmarkerHelper
        .DEFAULT_PRESENCE_CONFIDENCE
    private var _maxHands: Int = MultiLandmarkerHelper.DEFAULT_NUM_HANDS
    private var _maxFaces: Int = MultiLandmarkerHelper.DEFAULT_NUM_FACES

    val currentDelegate: Int get() = _delegate
    val currentMinDetectionConfidence: Float
        get() =
            _minDetectionConfidence
    val currentMinTrackingConfidence: Float
        get() =
            _minTrackingConfidence
    val currentMinPresenceConfidence: Float
        get() =
            _minPresenceConfidence
    val currentMaxHands: Int get() = _maxHands
    val currentMaxFaces: Int get() = _maxFaces

    fun setDelegate(delegate: Int) {
        _delegate = delegate
    }

    fun setMinDetectionConfidence(confidence: Float) {
        _minDetectionConfidence = confidence
    }
    fun setMinTrackingConfidence(confidence: Float) {
        _minTrackingConfidence = confidence
    }
    fun setMinPresenceConfidence(confidence: Float) {
        _minPresenceConfidence = confidence
    }

    fun setMaxFaces(maxResults: Int) {
        _maxFaces = maxResults
    }

    fun setMaxHands(maxResults: Int) {
        _maxHands = maxResults
    }

}