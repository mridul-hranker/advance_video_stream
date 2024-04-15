package com.example.advance_video_stream.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.advance_video_stream.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AdvanceSettingsBottomSheet() : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.advance_settings_bottom_sheet, container, false)
    }
}