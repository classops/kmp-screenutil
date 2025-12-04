package io.github.classops

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min
import kotlin.math.roundToInt

// Reference: https://pub.dev/packages/flutter_screenutil
// Use .w .h .r for width, height, and minimum adaptation
// .sw .sh represent screen width and height, 1.sw is screen width, 0.5.sh is half screen width
// .rsp represents font scaling


val Float.w: Dp
    @Composable get() = (this * LocalScreenScaler.current.wScale).dp

val Float.h: Dp
    @Composable get() = (this * LocalScreenScaler.current.hScale).dp


val Float.r: Dp
    @Composable get() = (this * min(
        this * LocalScreenScaler.current.wScale,
        this * LocalScreenScaler.current.hScale
    )).dp

inline val Int.w: Dp
    @Composable get() = this.toFloat().w

inline val Int.h: Dp
    @Composable get() = this.toFloat().h

inline val Int.r: Dp
    @Composable get() = this.toFloat().r

val Number.rsp: TextUnit
    @Composable get() = (this.toFloat() * LocalScreenScaler.current.wScale).sp

val Number.sw: Dp
    @Composable get() = (this.toFloat() * deviceWidthDp).dp

val Number.sh: Dp
    @Composable get() = (this.toFloat() * deviceHeightDp).dp


private val deviceWidthDp: Int
    @Composable get() = (LocalWindowInfo.current.containerSize.width / LocalDensity.current.density).toInt()

private val deviceHeightDp: Int
    @Composable get() = (LocalWindowInfo.current.containerSize.height / LocalDensity.current.density).toInt()


/**
 * Screen information
 * Do not use deviceWidthDp deviceHeightDp directly as they may not be set
 */
data class ScreenScaler(
    val designWidthDp: Int,
    val designHeightDp: Int,
    val deviceWidthDp: Int = designWidthDp,
    val deviceHeightDp: Int = designHeightDp,
    val wScale: Float = 1f,
    val hScale: Float = 1f,
) {
    companion object {
        internal val NO_SCALER = ScreenScaler(1, 1)
    }
}

/**
 * Screen adaptation: Calculate scale based on design draft size
 * @param designWidthDp Design draft width (default 360dp)
 * @param designHeightDp Design draft height (default 800dp)
 */
@Composable
fun rememberScreenScaler(
    designWidthDp: Int = 360,
    designHeightDp: Int = 710
): ScreenScaler {
    val config = LocalDensity.current
    val windowInfo = LocalWindowInfo.current.containerSize

    val density = config.density
    val widthDp = (windowInfo.width / density).roundToInt()
    val heightDp = (windowInfo.height / density).roundToInt()
    val fontScale = config.fontScale

    val scaleW = widthDp.toFloat() / designWidthDp
    val scaleH = heightDp.toFloat() / designHeightDp

    return remember(widthDp, heightDp, density, fontScale) {
        ScreenScaler(designWidthDp, designHeightDp, widthDp, heightDp, scaleW, scaleH)
    }
}

private val LocalScreenScaler = compositionLocalOf {
    ScreenScaler.NO_SCALER
}

/**
 * Screen adaptation initialization
 * @param designSize Design draft size
 * @param enableFontScale Whether to enable font scaling (enabled by default)
 * @param content Content
 */
@Composable
fun ScreenUtil(
    designSize: IntSize,
    enableFontScale: Boolean = true,
    content: @Composable () -> Unit
) {
    val screenScaler = rememberScreenScaler(designSize.width, designSize.height)
    if (enableFontScale) {
        CompositionLocalProvider(
            LocalScreenScaler provides screenScaler,
            content = content
        )
    } else {
        // Set fontScale to 1f to avoid system font scaling
        val density = LocalDensity.current
        CompositionLocalProvider(
            LocalScreenScaler provides screenScaler,
            LocalDensity provides Density(density.density, fontScale = 1f),
            content = content
        )
    }
}