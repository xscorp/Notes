# Android Security Testing Notes

## Broadcast Receivers , Intent Filters:
* Understanding basics of Broadcast Receivers  
https://youtu.be/eExZ56cqPFw

* Understanding broadcast receivers and why implicit broadcasts are restricted in newer android versions  
https://youtu.be/8FJ3oOpHszc

* If broadcast receiver specified in manifest file has at least one intent filter specified, the default value of android:exported will be "true".  
Reference: https://developer.android.com/guide/topics/manifest/receiver-element (See the "android:exported" section)

* A nice read on exploiting exported activities and broadcast receivers in Android using drozer and manual way:  
https://www.linkedin.com/pulse/hacking-android-apps-through-exposed-components-tal-melamed/  

## Launching an app using ADB without knowing the activity name  
An application can be launched without knowing the activity name by simulating the "icon-click" behaviour using **monkey** tool in adb.  
```adb shell monkey -p <package_name> -c android.intent.category.LAUNCHER 1```  
It works by checking and executing the activity that has the intent filter with action **android.intent.action.MAIN** and category **android.intent.category.LAUNCHER**  
Reference: https://stackoverflow.com/questions/29931318/launch-app-via-adb-without-knowing-activity-name  

