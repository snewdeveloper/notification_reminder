import 'package:flutter_test/flutter_test.dart';
import 'package:notification_reminder_flutter/notification_reminder_flutter.dart';
import 'package:notification_reminder_flutter/notification_reminder_flutter_platform_interface.dart';
import 'package:notification_reminder_flutter/notification_reminder_flutter_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockNotificationReminderFlutterPlatform
    with MockPlatformInterfaceMixin
    implements NotificationReminderFlutterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final NotificationReminderFlutterPlatform initialPlatform = NotificationReminderFlutterPlatform.instance;

  test('$MethodChannelNotificationReminderFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelNotificationReminderFlutter>());
  });

  test('getPlatformVersion', () async {
    NotificationReminderFlutter notificationReminderFlutterPlugin = NotificationReminderFlutter();
    MockNotificationReminderFlutterPlatform fakePlatform = MockNotificationReminderFlutterPlatform();
    NotificationReminderFlutterPlatform.instance = fakePlatform;

    expect(await notificationReminderFlutterPlugin.getPlatformVersion(), '42');
  });
}
