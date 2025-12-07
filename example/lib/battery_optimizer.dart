import 'package:flutter/material.dart';
import 'package:notification_reminder_flutter/notification_reminder_flutter.dart';

class BatteryOptimizer extends StatefulWidget {
  const BatteryOptimizer({super.key});

  @override
  State<BatteryOptimizer> createState() => _BatteryOptimizerState();
}

class _BatteryOptimizerState extends State<BatteryOptimizer> {
  @override
  Widget build(BuildContext context) {
    return   Scaffold(
      appBar: AppBar(
        title:  Text('Disable Battery Optimizations Plugin example app'),
      ),
      body: Center(
        child: Column(
          children: <Widget>[
            MaterialButton(
                child: Text("Is Auto Start Enabled"),
                onPressed: () async {
                  bool? isAutoStartEnabled =
                  await NotificationReminderFlutter.isAutoStartEnabled;
                  print(
                      "Auto start is ${isAutoStartEnabled}");
                }),
            MaterialButton(
                child: Text("Is Battery optimization disabled"),
                onPressed: () async {
                  bool? isBatteryOptimizationDisabled =
                  await NotificationReminderFlutter
                      .isBatteryOptimizationDisabled;
                  print(
                      "Battery optimization is ${!isBatteryOptimizationDisabled! ? "Enabled" : "Disabled"}");
                }),
            MaterialButton(
                child: Text("Is Manufacturer Battery optimization disabled"),
                onPressed: () async {
                  bool? isManBatteryOptimizationDisabled =
                  await NotificationReminderFlutter
                      .isManufacturerBatteryOptimizationDisabled;
                  print(
                      "Manufacturer Battery optimization is ${!isManBatteryOptimizationDisabled! ? "Enabled" : "Disabled"}");
                }),
            MaterialButton(
                child: Text("Are All Battery optimizations disabled"),
                onPressed: () async {
                  bool? isAllBatteryOptimizationDisabled =
                  await NotificationReminderFlutter
                      .isAllBatteryOptimizationDisabled;
                  print(
                      "All Battery optimizations are disabled ${isAllBatteryOptimizationDisabled}");
                }),
            MaterialButton(
                child: Text("Enable Auto Start"),
                onPressed: () {
                  NotificationReminderFlutter.showEnableAutoStartSettings(
                      "Enable Auto Start",
                      "Follow the steps and enable the auto start of this app");
                }),
            MaterialButton(
                child: Text("Disable Battery Optimizations"),
                onPressed: () {
                  NotificationReminderFlutter
                      .showDisableBatteryOptimizationSettings();
                }),
            MaterialButton(
                child: Text("Disable Manufacturer Battery Optimizations"),
                onPressed: () {
                  NotificationReminderFlutter
                      .showDisableManufacturerBatteryOptimizationSettings(
                      "Your device has additional battery optimization",
                      "Follow the steps and disable the optimizations to allow smooth functioning of this app");
                }),
            MaterialButton(
                child: Text("Disable all Optimizations"),
                onPressed: () {
                  NotificationReminderFlutter.showDisableAllOptimizationsSettings(
                      "Enable Auto Start",
                      "Follow the steps and enable the auto start of this app",
                      "Your device has additional battery optimization",
                      "Follow the steps and disable the optimizations to allow smooth functioning of this app");
                })
          ],
        ),
      ),
    );
  }
}
