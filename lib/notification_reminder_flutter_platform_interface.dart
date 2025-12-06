// import 'package:plugin_platform_interface/plugin_platform_interface.dart';
//
// import 'notification_reminder_flutter_method_channel.dart';
//
// abstract class NotificationReminderFlutterPlatform extends PlatformInterface {
//   /// Constructs a NotificationReminderFlutterPlatform.
//   NotificationReminderFlutterPlatform() : super(token: _token);
//
//   static final Object _token = Object();
//
//   static NotificationReminderFlutterPlatform _instance = MethodChannelNotificationReminderFlutter();
//
//   /// The default instance of [NotificationReminderFlutterPlatform] to use.
//   ///
//   /// Defaults to [MethodChannelNotificationReminderFlutter].
//   static NotificationReminderFlutterPlatform get instance => _instance;
//
//   /// Platform-specific implementations should set this with their own
//   /// platform-specific class that extends [NotificationReminderFlutterPlatform] when
//   /// they register themselves.
//   static set instance(NotificationReminderFlutterPlatform instance) {
//     PlatformInterface.verifyToken(instance, _token);
//     _instance = instance;
//   }
//
//   Future<String?> getPlatformVersion() {
//     throw UnimplementedError('platformVersion() has not been implemented.');
//   }
// }
