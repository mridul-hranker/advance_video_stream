import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

import 'advance_video_stream_platform_interface.dart';

/// An implementation of [AdvanceVideoStreamPlatform] that uses method channels.
class MethodChannelAdvanceVideoStream extends AdvanceVideoStreamPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('advance_video_stream');

  @override
  SurfacePlayer getSurfacePlayer() {
    return SurfacePlayer(methodChannel: methodChannel, aspectRatio: 16 / 9);
  }

  @override
  Future<void> disposeSurfacePlayer() async {
    await methodChannel.invokeMethod<void>('disposeSurfacePlayer');
  }

  @override
  Future<void> playSurfacePlayer() async {
    await methodChannel.invokeMethod<void>('playSurfacePlayer');
  }

  @override
  Future<void> pauseSurfacePlayer() async {
    await methodChannel.invokeMethod<void>('pauseSurfacePlayer');
  }

  @override
  Future<void> setSurfacePlayerVideoData(String videoId, bool useHLS) async {
    await methodChannel.invokeMethod<void>('setSurfacePlayerVideoData', {"videoId": videoId, "useHLS": useHLS});
  }

  @override
  Future<void> play() async {
    await methodChannel.invokeMethod<void>('play');
  }

  @override
  Future<void> pause() async {
    await methodChannel.invokeMethod<void>('pause');
  }

  @override
  Future<void> setVideoData(String videoId, bool useHLS) async {
    await methodChannel.invokeMethod<void>('setVideoData', {"videoId": videoId, "useHLS": useHLS});
  }

  @override
  Future<int?> getCurrentPosition() async {
    return await methodChannel.invokeMethod<int>('getCurrentPosition');
  }

  @override
  Future<int?> setCurrentPosition(int position) async {
    return await methodChannel.invokeMethod<int>('setCurrentPosition', {"position": position});
  }

  @override
  Future<void> changeOrientation(bool isLandscape) async {
    await methodChannel.invokeMethod<void>('changeOrientation', {"isLandscape": isLandscape});
  }

  @override
  AdvancePlayer player(double aspectRatio, double? height) {
    return AdvancePlayer(methodChannel: methodChannel, aspectRatio: aspectRatio, height: height);
  }
}

class AdvancePlayer extends StatelessWidget {
  final MethodChannel methodChannel;
  final double aspectRatio;
  final double? height;

  const AdvancePlayer({super.key, required this.methodChannel, required this.aspectRatio, this.height});

  @override
  Widget build(BuildContext context) {
    return Material(
      child: SizedBox(
        height: height,
        child: AspectRatio(
          aspectRatio: aspectRatio,
          child: PlatformViewLink(
            viewType: 'ExoPlayer',
            surfaceFactory: (context, controller) => AndroidViewSurface(
              controller: controller as AndroidViewController,
              hitTestBehavior: PlatformViewHitTestBehavior.opaque,
              gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
            ),
            onCreatePlatformView: (PlatformViewCreationParams params) {
              return PlatformViewsService.initSurfaceAndroidView(
                id: params.id,
                viewType: params.viewType,
                layoutDirection: TextDirection.ltr,
                onFocus: () => params.onFocusChanged(true),
              )
                ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
                ..create();
            },
          ),
        ),
      ),
    );
  }
}

class SurfacePlayer extends StatefulWidget {
  final MethodChannel methodChannel;
  final double aspectRatio;
  final double? height;

  const SurfacePlayer({super.key, required this.methodChannel, required this.aspectRatio, this.height});

  @override
  State<SurfacePlayer> createState() => _SurfacePlayerState();
}

class _SurfacePlayerState extends State<SurfacePlayer> {
  int? textureId;

  @override
  void initState() {
    super.initState();

    widget.methodChannel.invokeMethod<int?>('getSurfacePlayer').then((value) => setState(() => textureId = value ?? -86));
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      child: SizedBox(
        height: widget.height,
        child: AspectRatio(
          aspectRatio: widget.aspectRatio,
          child: (textureId != null)
              ? (textureId != -86)
                  ? Texture(textureId: textureId!)
                  : Container(color: Colors.red)
              : const Center(child: CircularProgressIndicator()),
        ),
      ),
    );
  }
}
