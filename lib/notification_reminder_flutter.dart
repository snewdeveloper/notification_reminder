
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';


class NotificationReminderFlutter {
  // Future<String?> getPlatformVersion() {
  //   return NotificationReminderFlutterPlatform.instance.getPlatformVersion();
  // }
  static const MethodChannel _channel = MethodChannel('notification_reminder_flutter');


  static Future<bool?> showEnableAutoStartSettings(
      String dialogTitle, String dialogBody) async {
    return await _channel.invokeMethod(
        'showEnableAutoStart', <dynamic>[dialogTitle, dialogBody]);
  }

  static Future<bool?> showDisableManufacturerBatteryOptimizationSettings(
      String dialogTitle, String dialogBody) async {
    return await _channel.invokeMethod('showDisableManBatteryOptimization',
        <dynamic>[dialogTitle, dialogBody]);
  }

  static Future<bool?> showDisableBatteryOptimizationSettings() async {
    return await _channel.invokeMethod('showDisableBatteryOptimization');
  }

  static Future<bool?> showDisableAllOptimizationsSettings(
      String autoStartTitle,
      String autoStartBody,
      String manBatteryTitle,
      String manBatteryBody) async {
    return await _channel.invokeMethod('disableAllOptimizations', <dynamic>[
      autoStartTitle,
      autoStartBody,
      manBatteryTitle,
      manBatteryBody
    ]);
  }

  static Future<bool?> get isAutoStartEnabled async {
    return await _channel.invokeMethod("isAutoStartEnabled");
  }

  static Future<bool?> get isBatteryOptimizationDisabled async {
    return await _channel.invokeMethod("isBatteryOptimizationDisabled");
  }

  static Future<bool?> get isManufacturerBatteryOptimizationDisabled async {
    return await _channel.invokeMethod("isManBatteryOptimizationDisabled");
  }

  static Future<bool?> get isAllBatteryOptimizationDisabled async {
    return await _channel.invokeMethod("isAllOptimizationsDisabled");
  }

  static Future<bool> checkOverlayPermission() async {
    final isGranted = await Permission.systemAlertWindow.isGranted;
    return isGranted;
  }
  /// Opens OEM-specific settings (Xiaomi, Vivo, Oppo etc.)
  /// to allow auto-start, floating window, and battery optimization exemptions.
  static Future<void> enableBackgroundPopupPermissions() async {
    try {
      await _channel.invokeMethod('enableBackgroundPopupPermissions');
    } catch (e) {
      print("Unexpected error: $e");
    }
  }

  static Future<void> subscribeUserToTopicOnce() async {
    try {
      await _channel.invokeMethod('subscribeUserToTopicOnce');
    } catch (e) {
      print("Unexpected error: $e");
    }
  }
  static Future<void> requestBasicPermissions() async {
    try {
      await _channel.invokeMethod('requestBasicPermissions');
    } catch (e) {
      print("Unexpected error: $e");
    }
  }

  static Future<void> requestOverlayPermission() async {
    bool overlayGranted = await checkOverlayPermission();
    if (!overlayGranted) {
      final result = await Permission.systemAlertWindow.request();
      if (result.isGranted) {
        debugPrint("Permission for overlay granted!!!");
      } else {
        // optional: open settings if denied
        await openAppSettings();
      }
    }
  }

  // MIUI permissions
  static Future<void> miuiAutoStart()async{
    try {
      await _channel.invokeMethod('openMiuiAutostart');
    } catch (e) {
      print("Unexpected error: $e");
    }
  }
  static Future<void> openMiuiPopupPermission()async{
    try {
      await _channel.invokeMethod('openMiuiPopupPermission');
    } catch (e) {
      print("Unexpected error: $e");
    }
  }
  static Future<void> openMiuiBatterySettings()async{
    try {
      await _channel.invokeMethod('openMiuiBatterySettings');
    } catch (e) {
      print("Unexpected error: $e");
    }
  }
  static Future<void> openMiuiRestrictedSettings()async{
    try {
      await _channel.invokeMethod('openMiuiRestrictedSettings');
    } catch (e) {
      print("Unexpected error: $e");
    }
  }

  static Future<void> openVivoAutostartPermission()async{
    try {
      await _channel.invokeMethod('requestVivoAutostartPermission');
    } catch (e) {
      print("Unexpected error: $e");
    }
  }

}
