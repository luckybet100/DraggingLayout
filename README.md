# android custom elements

This is a collection of some android custom elements, which could be easy used in your projects.

In **example** folder you can see usage of some of them

## Installing

Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency:

```
dependencies {
	implementation 'com.github.luckybet100:android-custom-elements:+'
}
```

## Features

- [X] ***Dragging layout***
    - custom layout based on FrameLayout which provides changing elements position, size and rotation with fingers