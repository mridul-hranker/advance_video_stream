import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

import 'advance_video_stream_method_channel.dart';
import 'advance_video_stream_platform_interface.dart';

class AdvanceVideoStream {
  //Surface Player
  /// Not to use this for now this is for testing only this will be live later
  SurfacePlayerUI getSurfacePlayer({required double aspectRatio}) {
    return SurfacePlayerUI(aspectRatio: aspectRatio);
  }

  Future<void> disposeSurfacePlayer() {
    return AdvanceVideoStreamPlatform.instance.disposeSurfacePlayer();
  }

  Future<void> playSurfacePlayer() {
    return AdvanceVideoStreamPlatform.instance.playSurfacePlayer();
  }
  bool playingSurfacePlayer() {
    return true;
  }

  Future<void> pauseSurfacePlayer() {
    return AdvanceVideoStreamPlatform.instance.pauseSurfacePlayer();
  }

  Future<void> setSurfacePlayerVideoData({required String videoId, bool useHLS = false}) {
    return AdvanceVideoStreamPlatform.instance.setSurfacePlayerVideoData(videoId, useHLS);
  }

  //Standard Player
  Future<void> play() {
    return AdvanceVideoStreamPlatform.instance.play();
  }

  Future<void> pause() {
    return AdvanceVideoStreamPlatform.instance.pause();
  }

  Future<void> setVideoData({required String videoId, bool useHLS = false}) {
    return AdvanceVideoStreamPlatform.instance.setVideoData(videoId, useHLS);
  }

  Future<int?> getVideoLength() {
    return AdvanceVideoStreamPlatform.instance.getVideoLength();
  }

  Future<int?> getCurrentPosition() {
    return AdvanceVideoStreamPlatform.instance.getCurrentPosition();
  }

  Future<void> setCurrentPosition(int position) {
    return AdvanceVideoStreamPlatform.instance.setCurrentPosition(position);
  }

  Future<void> changeOrientation(bool isLandscape) async {
    return AdvanceVideoStreamPlatform.instance.changeOrientation(isLandscape);
  }

  AdvancePlayer player({required double aspectRatio, double? height}) {
    return AdvanceVideoStreamPlatform.instance.player(aspectRatio, height);
  }
}

class SurfacePlayerUI extends StatefulWidget {
  final double aspectRatio;

  const SurfacePlayerUI({super.key, required this.aspectRatio});

  @override
  State<SurfacePlayerUI> createState() => _SurfacePlayerUIState();
}

class _SurfacePlayerUIState extends State<SurfacePlayerUI> with SingleTickerProviderStateMixin {
  Timer? _uiHideTimer;

  late AnimationController _animationController;
  late Animation<int> alpha;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(vsync: this, duration: const Duration(milliseconds: 300));

    final Animation<double> curve = CurvedAnimation(parent: _animationController, curve: Curves.easeOut);
    alpha = IntTween(begin: 0, end: 150).animate(curve);
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  void startTimerToHideUI() {
    _uiHideTimer = Timer(const Duration(seconds: 2), () {
      _animationController.reverse();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Stack(children: [
      Positioned(left: 0, top: 0, right: 0, bottom: 0, child: AdvanceVideoStreamPlatform.instance.getSurfacePlayer(widget.aspectRatio)),
      AnimatedBuilder(
        animation: alpha,
        builder: (context, child) => GestureDetector(
          onTap: () {
            if (!_animationController.isAnimating) {
              if (_animationController.value == 0) {
                _animationController.forward();
                startTimerToHideUI();
              } else {
                _uiHideTimer?.cancel();
                _animationController.reverse();
              }
            }
          },
          /*child: Container(
            color: Colors.black.withAlpha(alpha.value),
            child: Column(
              children: [
                const Row(children: [Text("data")]),
                Spacer(),
                const Row(children: [Text("data"), LinearProgressIndicator(value: 0.5), Icon(Icons.settings), Icon(Icons.settings)]),
              ],
            ),
          ),*/
        ),
      ),
    ]);
  }
}
