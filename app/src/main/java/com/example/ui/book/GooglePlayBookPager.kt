package com.example.ui.book

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.util.AudioFeedback
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

/**
 * Google Play Books style ultra-smooth 3D page curl & flip pager with unlimited pages support.
 */
@Composable
fun GooglePlayBookPager(
    pageCount: Int,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    onRequestNewPage: (() -> Unit)? = null,
    soundEffectsEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    pageContent: @Composable (pageIndex: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Drag offset in pixels: negative when dragging left (next page), positive when dragging right (previous page)
    val dragOffset = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Smooth page flip curve
    val smoothFlipEasing = remember { CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE8E5DD)) // Warm ambient desk background
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat()
        val totalHeightPx = constraints.maxHeight.toFloat()

        // Draggable gesture detector
        val draggableState = rememberDraggableState { delta ->
            coroutineScope.launch {
                val newTarget = dragOffset.value + delta
                if (currentPage == 0 && newTarget > 0) {
                    dragOffset.snapTo(newTarget * 0.2f) // resistance at start
                } else {
                    dragOffset.snapTo(newTarget)
                }
            }
        }

        // Tap gestures for side edges (quick turning)
        val edgeTapModifier = Modifier.pointerInput(currentPage, pageCount) {
            detectTapGestures(
                onTap = { offset ->
                    val tapX = offset.x
                    val width = size.width
                    if (tapX < width * 0.15f && currentPage > 0) {
                        // Flip to previous
                        AudioFeedback.playPageFlipSound(soundEffectsEnabled)
                        coroutineScope.launch {
                            dragOffset.snapTo(-10f)
                            dragOffset.animateTo(
                                targetValue = totalWidthPx,
                                animationSpec = tween(320, easing = smoothFlipEasing)
                            )
                            onPageChanged(currentPage - 1)
                            dragOffset.snapTo(0f)
                        }
                    } else if (tapX > width * 0.85f) {
                        // Flip to next
                        if (currentPage < pageCount - 1) {
                            AudioFeedback.playPageFlipSound(soundEffectsEnabled)
                            coroutineScope.launch {
                                dragOffset.snapTo(10f)
                                dragOffset.animateTo(
                                    targetValue = -totalWidthPx,
                                    animationSpec = tween(320, easing = smoothFlipEasing)
                                )
                                onPageChanged(currentPage + 1)
                                dragOffset.snapTo(0f)
                            }
                        } else if (onRequestNewPage != null) {
                            // Infinite page auto-creation
                            AudioFeedback.playPageFlipSound(soundEffectsEnabled)
                            onRequestNewPage()
                        }
                    }
                }
            )
        }

        val modifierDraggable = Modifier.draggable(
            state = draggableState,
            orientation = Orientation.Horizontal,
            onDragStarted = {
                isDragging = true
            },
            onDragStopped = { velocity ->
                isDragging = false
                val currentOffset = dragOffset.value
                val threshold = totalWidthPx * 0.20f

                coroutineScope.launch {
                    if (currentOffset < -threshold || (currentOffset < -20f && velocity < -450f)) {
                        // Flip forward to Next Page
                        if (currentPage < pageCount - 1) {
                            AudioFeedback.playPageFlipSound(soundEffectsEnabled)
                            dragOffset.animateTo(
                                targetValue = -totalWidthPx,
                                animationSpec = tween(290, easing = smoothFlipEasing)
                            )
                            onPageChanged(currentPage + 1)
                            dragOffset.snapTo(0f)
                        } else if (onRequestNewPage != null) {
                            // Swiping forward on last page automatically inserts a new page!
                            AudioFeedback.playPageFlipSound(soundEffectsEnabled)
                            onRequestNewPage()
                            dragOffset.snapTo(0f)
                        } else {
                            dragOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.82f))
                        }
                    } else if (currentOffset > threshold || (currentOffset > 20f && velocity > 450f)) {
                        // Flip backward to Previous Page
                        if (currentPage > 0) {
                            AudioFeedback.playPageFlipSound(soundEffectsEnabled)
                            dragOffset.animateTo(
                                targetValue = totalWidthPx,
                                animationSpec = tween(290, easing = smoothFlipEasing)
                            )
                            onPageChanged(currentPage - 1)
                            dragOffset.snapTo(0f)
                        } else {
                            dragOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.82f))
                        }
                    } else {
                        // Smooth bounce back
                        dragOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.85f))
                    }
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(modifierDraggable)
                .then(edgeTapModifier)
        ) {
            val offsetVal = dragOffset.value
            val progress = (offsetVal / totalWidthPx).coerceIn(-1f, 1f)

            if (progress < 0f) {
                // User is turning Forward (Current Page curls to left, revealing Next Page beneath)
                val nextPageIndex = currentPage + 1
                val turnFraction = abs(progress) // 0f to 1f

                // 1. Underneath Next Page
                if (nextPageIndex < pageCount) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        pageContent(nextPageIndex)

                        // Deep realistic shadow cast by the turning sheet
                        val shadowAlpha = (1f - turnFraction) * 0.42f
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = shadowAlpha),
                                        Color.Black.copy(alpha = shadowAlpha * 0.35f),
                                        Color.Transparent
                                    ),
                                    startX = 0f,
                                    endX = size.width * 0.45f
                                )
                            )
                        }
                    }
                }

                // 2. Currently Turning Page (3D Rotation + Curl Cylindrical Effect)
                val rotationAngle = -turnFraction * 180f
                val spineShadow = turnFraction * 0.45f

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = rotationAngle
                            cameraDistance = 16 * density.density
                            transformOrigin = TransformOrigin(0f, 0.5f) // Hinged at the left spine
                        }
                ) {
                    if (rotationAngle > -90f) {
                        // Front face of current page
                        pageContent(currentPage)

                        // Dynamic 3D lighting gradient: shadow gutter + highlight ridge + curl shadow
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = spineShadow * 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = turnFraction * 0.25f),
                                        Color.White.copy(alpha = turnFraction * 0.30f), // Specular paper sheen on bend
                                        Color.Black.copy(alpha = turnFraction * 0.35f)
                                    ),
                                    startX = 0f,
                                    endX = size.width
                                )
                            )
                        }
                    } else {
                        // Backside of the turned page (Crisp paper reverse with light gradient)
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { rotationY = 180f }, // flip back upright
                            color = Color(0xFFFCFCFB)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawRect(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.30f),
                                                Color.Transparent,
                                                Color.Black.copy(alpha = (1f - turnFraction) * 0.25f)
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

            } else if (progress > 0f) {
                // User is turning Backward (Previous Page curls from left into view over current page)
                val prevPageIndex = currentPage - 1
                val turnFraction = progress // 0f to 1f

                // 1. Underlying Current Page
                Box(modifier = Modifier.fillMaxSize()) {
                    pageContent(currentPage)

                    // Shadow cast by previous page curling in
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = turnFraction * 0.38f),
                                    Color.Transparent
                                ),
                                startX = 0f,
                                endX = size.width * 0.45f
                            )
                        )
                    }
                }

                // 2. Previous Page curling from left
                if (prevPageIndex >= 0) {
                    val rotationAngle = -180f + (turnFraction * 180f) // -180 to 0

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationY = rotationAngle
                                cameraDistance = 16 * density.density
                                transformOrigin = TransformOrigin(0f, 0.5f)
                            }
                    ) {
                        if (rotationAngle > -90f) {
                            pageContent(prevPageIndex)

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = (1f - turnFraction) * 0.35f),
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.22f)
                                        )
                                    )
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer { rotationY = 180f },
                                color = Color(0xFFFCFCFB)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawRect(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.30f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

            } else {
                // Static display of Current Page with corner curl prompt
                Box(modifier = Modifier.fillMaxSize()) {
                    pageContent(currentPage)

                    // Corner Curl Hint (Google Play Books style corner fold visual affordance)
                    PageCornerCurlHint(
                        isLastPage = currentPage >= pageCount - 1,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 14.dp, bottom = 18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Visual corner curl hint on bottom-right of page.
 */
@Composable
private fun PageCornerCurlHint(
    isLastPage: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(26.dp)
    ) {
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            close()
        }

        // Shadow behind the folded corner
        drawPath(
            path = path,
            color = Color(0x28000000)
        )

        val foldPath = Path().apply {
            moveTo(0f, size.height)
            lineTo(size.width, 0f)
            lineTo(size.width - 5.dp.toPx(), size.height - 5.dp.toPx())
            close()
        }

        drawPath(
            foldPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    if (isLastPage) Color(0xFF81D4FA) else Color(0xFFE2E2DE),
                    Color(0xFFFFFFFF)
                ),
                start = Offset(0f, size.height),
                end = Offset(size.width, 0f)
            )
        )
    }
}
