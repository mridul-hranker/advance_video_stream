import 'dart:async';

import 'package:advance_video_stream/advance_video_stream.dart';
import 'package:audio_video_progress_bar/audio_video_progress_bar.dart';
import 'package:flutter/material.dart';

class VideoPlayerScreen extends StatefulWidget {
  final String videoId;

  const VideoPlayerScreen({super.key, required this.videoId});

  @override
  State<VideoPlayerScreen> createState() => _VideoPlayerScreenState();
}

class _VideoPlayerScreenState extends State<VideoPlayerScreen> with SingleTickerProviderStateMixin {
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
  void deactivate() {
    _advanceVideoStreamPlugin.disposeSurfacePlayer();
    super.deactivate();
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, orientation) => Scaffold(
        body: AspectRatio(
          aspectRatio: MediaQuery.of(context).size.aspectRatio,
          child: /*Stack(
            fit: StackFit.expand,
            children: [
              _advanceVideoStreamPlugin.player(aspectRatio: MediaQuery.of(context).size.aspectRatio), //getSurfacePlayer(),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  IconButton(onPressed: () => _advanceVideoStreamPlugin.pause(), icon: const Icon(Icons.pause)),
                  IconButton(onPressed: () => _advanceVideoStreamPlugin.play(), icon: const Icon(Icons.play_arrow)),
                ],
              )
            ],
          ),*/
              Stack(
            children: [
              _advanceVideoStreamPlugin.getSurfacePlayer(aspectRatio: MediaQuery.of(context).size.aspectRatio),
              Positioned(
                left: 0,
                top: 0,
                right: 0,
                bottom: 0,
                child: Container(
                  color: Colors.white.withAlpha(200),
                  padding: EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Row(children: [Text("data")]),
                      Center(
                          child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          IconButton(style: IconButton.styleFrom(iconSize: 50), onPressed: () {}, icon: Icon(Icons.skip_previous)),
                          IconButton(
                              style: IconButton.styleFrom(iconSize: 50),
                              onPressed: () {
                                if (_advanceVideoStreamPlugin.playingSurfacePlayer()) {
                                  _advanceVideoStreamPlugin.pauseSurfacePlayer();
                                } else {
                                  _advanceVideoStreamPlugin.playSurfacePlayer();
                                }
                              },
                              icon: Icon(_advanceVideoStreamPlugin.playingSurfacePlayer() ? Icons.pause : Icons.play_arrow)),
                          IconButton(style: IconButton.styleFrom(iconSize: 50), onPressed: () {}, icon: const Icon(Icons.skip_next)),
                        ],
                      )),
                      Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                        Expanded(
                          child: ProgressBar(
                            total: Duration(minutes: 2),
                            buffered: Duration(minutes: 1),
                            progress: Duration(seconds: 30),
                            timeLabelLocation: TimeLabelLocation.sides,
                            onSeek: (value) {
                              debugPrint("ProgressBar seeked");
                            },
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16.0),
                          child: Icon(Icons.settings),
                        ),
                        Icon(Icons.speed),
                        Padding(
                          padding: const EdgeInsets.only(left: 16.0),
                          child: Icon(Icons.aspect_ratio),
                        )
                      ]),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
