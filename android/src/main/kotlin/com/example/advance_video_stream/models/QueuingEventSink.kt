package com.example.advance_video_stream.models

// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import io.flutter.plugin.common.EventChannel
import java.util.ArrayList

/**
 * An implementation of [EventChannel.EventSink] which can wrap an underlying sink.
 *
 * It delivers messages immediately when downstream is available, but it queues messages before
 * the delegate event sink is set with setDelegate.
 *
 * This class is not thread-safe. All calls must be done on the same thread or synchronized
 * externally.
 */
class QueuingEventSink : EventChannel.EventSink {
    private var delegate: EventChannel.EventSink? = null
    private val eventQueue = ArrayList<Any>()
    private var done = false

    fun setDelegate(delegate: EventChannel.EventSink?) {
        this.delegate = delegate
        maybeFlush()
    }

    override fun endOfStream() {
        enqueue(EndOfStreamEvent())
        maybeFlush()
        done = true
    }

    override fun error(code: String, message: String, details: Any?) {
        enqueue(ErrorEvent(code, message, details))
        maybeFlush()
    }

    override fun success(event: Any?) {
        enqueue(event ?: return)
        maybeFlush()
    }

    private fun enqueue(event: Any) {
        if (done) return
        eventQueue.add(event)
    }

    private fun maybeFlush() {
        delegate?.let { delegate ->
            for (event in eventQueue) {
                when (event) {
                    is EndOfStreamEvent -> delegate.endOfStream()
                    is ErrorEvent -> delegate.error(event.code, event.message, event.details)
                    else -> delegate.success(event)
                }
            }
            eventQueue.clear()
        }
    }

    data class EndOfStreamEvent(val any: Any = Unit)

    data class ErrorEvent(
        val code: String,
        val message: String,
        val details: Any?
    )
}