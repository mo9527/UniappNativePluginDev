@echo off
chcp 65001
echo copy aar file to app directory
xcopy E:\02_code\03_android_space\Android-SDK@4.57.82388_20250321\Android-SDK@4.57.82388_20250321\UniPlugin-Hello-AS\wanyi_uniapp_plugin\build\outputs\aar\wanyi-uniapp-plugins-release.aar E:\02_code\05_uniapp_project\demoapp\DeliverRobotControlApp\nativeplugins\WanyiUniappPlugins\android /y /e
echo copy uniapp resources file to app directory
xcopy E:\02_code\05_uniapp_project\demoapp\DeliverRobotControlApp\unpackage\resources\*.* E:\02_code\03_android_space\Android-SDK@4.57.82388_20250321\Android-SDK@4.57.82388_20250321\UniPlugin-Hello-AS\app\src\main\assets\apps /y /e

echo copy finished