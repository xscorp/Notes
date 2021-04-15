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

## Understanding keystores
Every apk needs to be signed before it can be installed on an android device. Signing the application often makes use of a file called "keystore". A keystore is a container that is used to store cryptographic keys. Why? To mitigate the danger of leaking keys. Every cryptographic key related to the applications is put inside the Keystore and the keystore is used to authenticate everywhere. While creating a keystore, the developer has to specify a password for keystore. Once the keystore is created, keys can be stored inside it by specifying a name for the key, 
