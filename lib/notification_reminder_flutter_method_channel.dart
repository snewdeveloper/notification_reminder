// import 'package:flutter/foundation.dart';
// import 'package:flutter/services.dart';
//
// import 'notification_reminder_flutter_platform_interface.dart';
//
// /// An implementation of [NotificationReminderFlutterPlatform] that uses method channels.
// class MethodChannelNotificationReminderFlutter extends NotificationReminderFlutterPlatform {
//   /// The method channel used to interact with the native platform.
//   @visibleForTesting
//   final methodChannel = const MethodChannel('notification_reminder_flutter');
//
//   @override
//   Future<String?> getPlatformVersion() async {
//     final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
//     return version;
//   }
// }
