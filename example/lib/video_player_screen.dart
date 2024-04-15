import 'dart:async';

import 'package:advance_video_stream/advance_video_stream.dart';
import 'package:flutter/material.dart';

class VideoPlayerScreen extends StatefulWidget {
  final String videoId;

  VideoPlayerScreen({super.key, required this.videoId});

  @override
  State<VideoPlayerScreen> createState() => _VideoPlayerScreenState();
}

class _VideoPlayerScreenState extends State<VideoPlayerScreen> {
  final _advanceVideoStreamPlugin = AdvanceVideoStream();

  @override
  void didChangeDependencies() {
    Timer(const Duration(seconds: 3), () {
      _advanceVideoStreamPlugin.setVideoData(videoId: widget.videoId, useHLS: true);
    });
    super.didChangeDependencies();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(body: _advanceVideoStreamPlugin.player(aspectRatio: MediaQuery.of(context).size.aspectRatio));
  }
}
