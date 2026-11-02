# Keep all application models and MainActivity
-keep class com.example.foodapp.** { *; }
-keepclassmembers class com.example.foodapp.** { *; }

# Gson rules
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class com.google.gson.** { *; }

# Coil image loading rules
-keep class io.coilkt.** { *; }
