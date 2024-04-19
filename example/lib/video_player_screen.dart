import 'dart:async';

import 'package:advance_video_stream/advance_video_stream.dart';
import 'package:flutter/material.dart';

class VideoPlayerScreen extends StatefulWidget {
  final String videoId;

  const VideoPlayerScreen({super.key, required this.videoId});

  @override
  State<VideoPlayerScreen> createState() => _VideoPlayerScreenState();
}

class _VideoPlayerScreenState extends State<VideoPlayerScreen> {
  final _advanceVideoStreamPlugin = AdvanceVideoStream();

  int? getPlayerTextureId;

  @override
  void didChangeDependencies() {
    /*Timer(const Duration(seconds: 5), () {
      _advanceVideoStreamPlugin.getPlayer().then((value) {
        debugPrint("VideoPlayerScreen didChangeDependencies getPlayer value $value");

        setState(() {
          getPlayerTextureId = value;
        });
      });
    });*/

    Timer(const Duration(seconds: 10), () {
      _advanceVideoStreamPlugin.setVideoData(videoId: widget.videoId, useHLS: true);
    });

    int? position;

    Timer(const Duration(seconds: 15), () async {
      position = await _advanceVideoStreamPlugin.getCurrentPosition();
      debugPrint("VideoPlayerScreen didChangeDependencies position $position");
    });

    Timer(const Duration(seconds: 25), () {
      _advanceVideoStreamPlugin.setCurrentPosition(position!);
      debugPrint("VideoPlayerScreen didChangeDependencies setCurrentPosition $position");
    });

    super.didChangeDependencies();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: AspectRatio(
        aspectRatio: MediaQuery.of(context).size.aspectRatio,
        child: _advanceVideoStreamPlugin.player(aspectRatio: MediaQuery.of(context).size.aspectRatio, height: 100),
      ),
    );
  }
}
