import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:notification_reminder_flutter/notification_reminder_flutter.dart';
import 'package:notification_reminder_flutter_example/battery_optimizer.dart';
import 'package:permission_handler/permission_handler.dart';

import 'firebase_options.dart';

void main() async{
    WidgetsFlutterBinding.ensureInitialized();
    await Permission.phone.request();
    // await Permission.systemAlertWindow.request();

    await Firebase.initializeApp(
      options: DefaultFirebaseOptions.currentPlatform,
    );
    runApp(const MaterialApp(home: MyApp()));
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _notificationReminderFlutterPlugin = NotificationReminderFlutter();
  bool _overlayGranted = false;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    getOverlayStatus();
    NotificationReminderFlutter.subscribeUserToTopicOnce();
  }
  getOverlayStatus()async {
    _overlayGranted = await NotificationReminderFlutter.checkOverlayPermission();
    setState(() {
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }



  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Notification reminder example app'),
        ),
        body: Center(
          child:   Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
              Text('Plugin Call listener example'),
          const Text(
            'Allow this app to display popups over other apps',
            style: TextStyle(fontSize: 16),
            textAlign: TextAlign.center,
          ),
                const SizedBox(height: 20),
                ElevatedButton(onPressed: (){
                  NotificationReminderFlutter.requestBasicPermissions();
                },
                    child:Text("Request basic permissions")
                ),
          const SizedBox(height: 20),

          ElevatedButton(onPressed: (){
            Navigator.of(context).push(MaterialPageRoute(builder: (context)=>BatteryOptimizer()));
          },
              child:Text("battery optimizer")
          ),
          SwitchListTile(
            title: const Text('Display over other apps'),
            value: _overlayGranted,
            onChanged: _overlayGranted
                ? null // user can’t turn it off from app
                : (value) async{
              if (value){
                await NotificationReminderFlutter.requestOverlayPermission();
                await getOverlayStatus();
              }
            },
          ),])
        ),
      ),
    );
  }
}
