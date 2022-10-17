# android-placeholders-validator
Gradle plugin which validates placeholders from translated strings.xml files by comparing them with main strings.xml file.

# Why may I need it?
If other people make translations for your app there is a risk that they may modify and malform string placeholders.\
I struggled with such situations which eventually led to app crashing only for Korean people (sorry Korean people).\
Then I applied the plugin in my other apps and it turned out that some placeholders are also malformed! Luckily it didn't
make the app crashing, but not luckily, displayed strings were incorrectly formatted.
Of course you could also malform or accidentaly modify such placeholder by yourself, but as we programmers don't make mistakes,
it is less probable, isn't it? :sweat_smile:

# Validation
The plugin compares placeholders from translated strings.xml files with placeholders from your main strings.xml file.
When there is no match, you get clear message with exact place of issue, like this one:

```
Affected file: .../app/src/main/res/values-fr/strings.xml Affected key: settings_version. Should be: [%1$s] but is: [] 
Affected file: .../app/src/main/res/values-fr/strings.xml Affected key: post_history_create_date. Should be: [%1$s] but is: [] 
Affected file: .../app/src/main/res/values-de/strings.xml Affected key: settings_version. Should be: [%1$s] but is: [%2$s] 
```

# Usage
In main build.grale file:

```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.appunite.placeholdersvalidator:placeholders-validator:1.0.1"
  }
}
```

At the end of your app module build.gradle file:

```
apply plugin: "com.appunite.placeholdersvalidator"
placeholdersValidator {
    resourcesDir = android.sourceSets.main.res.sourceFiles
}
preBuild.dependsOn placeholdersValidatorTask

```

Code below will run the validation before building. You can adjust it to your needs though.
```
preBuild.dependsOn placeholdersValidatorTask
```
