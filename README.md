## Compose Multiplatform ScreenUtil

ScreenUtil is a Compose Multiplatform library designed to easily adapt their UI to different screen sizes. Inspired by [flutter_screenutil](https://pub.dev/packages/flutter_screenutil).

## Features

1. Automatic screen adaptation based on design draft size
2. Support for width `.w`, height `.h`, and responsive scaling `.r`
3. `1.sw` and `1.sh` Based on screen width and height.(e.g., 0.5.sw = screen half width, 0.3.sh = 30% screen height)
4. `rsp` Font size scaling support 

## Setup

Add the dependency to your `build.gradle.kts` file:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.classops:kmp-screenutil:1.0.0")
            }
        }
    }
}
```

## Usage

### 1. Initialize ScreenUtil

Wrap your app's content with the [ScreenUtil] composable and specify your design draft size:

```kotlin
@Composable
fun App() {
    // design draft size
    ScreenUtil(designSize = IntSize(360, 710)) {
        // Your app content here
        MyScreen()
    }
}
```

### 2. Use Extension Properties

Once initialized, you can use the following extension properties:

#### Dimension Extensions

- `Float.w` / `Int.w` - Scales based on width ratio
- `Float.h` / `Int.h` - Scales based on height ratio
- `Float.r` / `Int.r` - Scales based on the minimum of width and height ratios (responsive)

Example:
```kotlin
Box(
    modifier = Modifier
        .size(100.w, 50.h) // Width scaled to 100dp equivalent, height to 50dp equivalent
        .padding(10.r)     // Responsive padding
)
```

#### Font Size Extensions

- `Number.rsp` - Scales font size responsively

Example:
```kotlin
Text(
    text = "Hello World",
    fontSize = 16.rsp
)
```

#### Screen Proportions

- `Number.sw` - Percentage of screen width (e.g., 0.5.sw = half screen width)
- `Number.sh` - Percentage of screen height (e.g., 0.3.sh = 30% of screen height)

Example:
```kotlin
Box(
    modifier = Modifier
        .width(0.8.sw)  // 80% of screen width
        .height(0.6.sh) // 60% of screen height
)
```

### Customization Options

You can customize the behavior of ScreenUtil:

```kotlin
ScreenUtil(
    designSize = IntSize(390, 844), // iPhone 14 Pro design size
    enableFontScale = false          // Disable system font scaling
) {
    // Your content here
}
```

## How It Works

ScreenUtil calculates scaling factors by comparing the actual device screen size to your design draft size:

- `wScale` = deviceWidth / designWidth
- `hScale` = deviceHeight / designHeight

These scales are then applied to your dimensions when using the extension properties, ensuring consistent visual proportions across devices of different sizes.

## Supported Platforms

As a Compose Multiplatform library, ScreenUtil works on:
- Android
- iOS
- Desktop (Windows, macOS, Linux)
- Web

## Example

```kotlin
@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.r),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource("logo.png"),
            modifier = Modifier.size(80.r),
            contentDescription = "App Logo"
        )
        
        Spacer(modifier = Modifier.height(20.h))
        
        Text(
            text = "Welcome",
            fontSize = 24.rsp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(30.h))
        
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Username") },
            modifier = Modifier.width(0.8.sw)
        )
        
        Spacer(modifier = Modifier.height(15.h))
        
        Button(
            onClick = { /* Handle login */ },
            modifier = Modifier
                .width(0.8.sw)
                .height(50.h)
        ) {
            Text(
                text = "Login",
                fontSize = 18.rsp
            )
        }
    }
}