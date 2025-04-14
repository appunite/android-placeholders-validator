# android-placeholders-validator
A Gradle plugin that validates placeholders in translated strings.xml files by comparing them with the main `strings.xml` file.

# Why may I need it?
If other people translate your app, there is a risk they might modify or misformat string placeholders.\
I’ve encountered situations where this led to the app crashing only for Korean users (sorry, Korean friends!).\
Later, I applied this plugin to my other apps and discovered additional malformed placeholders.\
While these didn’t cause crashes, they did result in incorrectly formatted strings being displayed.\
Of course, it’s also possible to accidentally misformat or modify a placeholder yourself. \
But as programmers, we never make mistakes, right? 😅

# Validation
The plugin compares placeholders in translated strings.xml files with those in your main `strings.xml` file.\
If there is a mismatch, it provides a clear message indicating the exact location of the issue, like this:

```
Affected file: .../app/src/main/res/values-fr/strings.xml Affected key: settings_version. Should be: [%1$s] but is: [] 
Affected file: .../app/src/main/res/values-fr/strings.xml Affected key: post_history_create_date. Should be: [%1$s] but is: [] 
Affected file: .../app/src/main/res/values-de/strings.xml Affected key: settings_version. Should be: [%1$s] but is: [%2$s] 
```

# Usage
In the main `build.gradle` file:

```
buildscript {
  plugins {
    // When not using .toml
    id("com.appunite.placeholdersvalidator") version("1.1.0") apply(false)
  }
}
```

At the top of your app module `build.gradle` file:
```
// When using .toml
alias(libs.plugins.placeholder.validator)
// Without .toml
id("com.appunite.placeholdersvalidator")
```

At the end of your app module `build.gradle` file:

```
placeholdersValidator {
    // Path to your main source set
    resourcesDir = android.sourceSets.getByName("main").res.getSourceFiles()
    // Disable plurals validation. By default it's true and only the first plural option is verified due to 
    // the fact that each language can have different number of options [one, few, many, others]
    ignorePlurals = false
}

tasks.preBuild {
    dependsOn("placeholdersValidatorTask")
}
```

Code below will run the validation before building. You can adjust it to your needs though.
```
tasks.preBuild {
    dependsOn("placeholdersValidatorTask")
}
```
