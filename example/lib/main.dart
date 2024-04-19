import 'package:advance_video_stream_example/video_player_screen.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:advance_video_stream/advance_video_stream.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(appBar: AppBar(title: const Text('Plugin example app')), body: const MyColumn()),
    );
  }
}

class MyColumn extends StatelessWidget {
  const MyColumn({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        // Text('Running on: $_platformVersion\n'),
        ElevatedButton(
            onPressed: () {
              Navigator.push(context, MaterialPageRoute(builder: (context) => VideoPlayerScreen(videoId: "21bCrsGt050")));
            },
            child: const Text('Ent.')),
        ElevatedButton(
            onPressed: () {
              Navigator.push(context, MaterialPageRoute(builder: (context) => VideoPlayerScreen(videoId: "ZRtdQ81jPUQ")));
            },
            child: const Text('Ido.')),
        ElevatedButton(
            onPressed: () {
              Navigator.push(context, MaterialPageRoute(builder: (context) => VideoPlayerScreen(videoId: "OIBODIPC_8Y")));
            },
            child: const Text('Yus.')),
        ElevatedButton(
            onPressed: () {
              Navigator.push(context, MaterialPageRoute(builder: (context) => VideoPlayerScreen(videoId: "jfKfPfyJRdk")));
            },
            child: const Text('Liv.')),
      ],
    );
  }
}
