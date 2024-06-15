import 'dart:async';

import 'package:advance_video_stream/advance_video_stream.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

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
    Future.delayed(Duration.zero, () => _advanceVideoStreamPlugin.setSurfacePlayerVideoData(videoId: widget.videoId, useHLS: false));

    /*Timer(const Duration(seconds: 5), () {
      _advanceVideoStreamPlugin.getPlayer().then((value) {
        debugPrint("VideoPlayerScreen didChangeDependencies getPlayer value $value");

        setState(() {
          getPlayerTextureId = value;
        });
      });
    });*/

    /*Timer(const Duration(seconds: 10), () {
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
    });*/

    super.didChangeDependencies();
  }

  @override
  void dispose() {
    super.dispose();
    _advanceVideoStreamPlugin.disposeSurfacePlayer();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: AspectRatio(
        aspectRatio: MediaQuery.of(context).size.aspectRatio,
        child: Stack(
          fit: StackFit.expand,
          children: [
            _advanceVideoStreamPlugin.getSurfacePlayer(),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                IconButton(onPressed: () => _advanceVideoStreamPlugin.pauseSurfacePlayer(), icon: const Icon(Icons.pause)),
                IconButton(onPressed: () => _advanceVideoStreamPlugin.playSurfacePlayer(), icon: const Icon(Icons.play_arrow)),
              ],
            )
          ],
        ),
      ),
    );
  }
}
