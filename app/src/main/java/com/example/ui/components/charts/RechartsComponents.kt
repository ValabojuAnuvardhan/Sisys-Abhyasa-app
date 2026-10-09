package com.example.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusPill
import com.example.ui.theme.*
import kotlin.math.*

/**
 * Data slice for Donut / Pie chart (Recharts style).
 */
data class ChartSlice(
    val label: String,
    val value: Float,
    val color: Color,
    val secondaryText: String = ""
)

/**
 * Data item for interactive Bar Chart (Recharts / D3 style).
 */
data class BarChartItem(
    val id: String,
    val label: String,
    val completionPercentage: Float, // 0..100
    val readinessScore: Float, // 0..100
    val primaryColor: Color,
    val secondaryColor: Color = PrimaryTeal,
    val meta: String = ""
)

/**
 * Data point for Radar / Spider Skill Matrix (Recharts RadarChart style).
 */
data class RadarDataPoint(
    val dimension: String,
    val score: Float, // 0..100
    val benchmark: Float = 75f // Target benchmark
)

/**
 * Interactive Recharts / D3-inspired Donut Chart with animated sweep,
 * center career readiness index metric, and active arc highlight on tap.
 */
@Composable
fun RechartsDonutChart(
    slices: List<ChartSlice>,
    centerScoreText: String,
    centerLabelText: String,
    modifier: Modifier = Modifier,
    thickness: Float = 42f,
    onSliceSelected: ((ChartSlice) -> Unit)? = null
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val total = remember(slices) { slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f) }

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(slices) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .testTag("chart_donut_container"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(slices) {
                    detectTapGestures { tapOffset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = tapOffset.x - center.x
                        val dy = tapOffset.y - center.y
                        val distance = sqrt(dx * dx + dy * dy)
                        val radius = min(size.width, size.height) / 2f

                        if (distance in (radius - thickness * 1.5f)..radius) {
                            var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            if (angle < 0) angle += 360f
                            // Normalized to -90 starting angle
                            var normalizedAngle = (angle + 90f) % 360f

                            var accumulatedAngle = 0f
                            slices.forEachIndexed { index, slice ->
                                val sweep = (slice.value / total) * 360f
                                if (normalizedAngle in accumulatedAngle..(accumulatedAngle + sweep)) {
                                    selectedIndex = if (selectedIndex == index) null else index
                                    onSliceSelected?.invoke(slice)
                                    return@detectTapGestures
                                }
                                accumulatedAngle += sweep
                            }
                        } else {
                            selectedIndex = null
                        }
                    }
                }
        ) {
            val canvasSize = min(size.width, size.height)
            val strokeWidth = thickness
            val arcSize = Size(canvasSize - strokeWidth, canvasSize - strokeWidth)
            val topLeft = Offset(
                (size.width - arcSize.width) / 2f,
                (size.height - arcSize.height) / 2f
            )

            // Draw subtle background track ring
            drawArc(
                color = BorderLight,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            var startAngle = -90f
            slices.forEachIndexed { index, slice ->
                val targetSweep = (slice.value / total) * 360f
                val sweep = targetSweep * animationProgress.value
                val isSelected = selectedIndex == index
                val currentStroke = if (isSelected) strokeWidth + 8f else strokeWidth

                // Dynamic arc with shadow/glow simulation
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            slice.color.copy(alpha = if (isSelected) 1f else 0.85f),
                            slice.color
                        )
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweep.coerceAtLeast(1.5f),
                    useCenter = false,
                    topLeft = if (isSelected) {
                        Offset(
                            topLeft.x - 4f,
                            topLeft.y - 4f
                        )
                    } else topLeft,
                    size = if (isSelected) {
                        Size(arcSize.width + 8f, arcSize.height + 8f)
                    } else arcSize,
                    style = Stroke(
                        width = currentStroke,
                        cap = StrokeCap.Round
                    )
                )

                startAngle += (slice.value / total) * 360f
            }
        }

        // Center typography (D3/Recharts standard KPI center)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = centerScoreText,
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                color = PrimaryDarkTeal
            )
            Text(
                text = centerLabelText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                color = SecondaryText
            )
            if (selectedIndex != null) {
                val selectedSlice = slices.getOrNull(selectedIndex ?: 0)
                if (selectedSlice != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = selectedSlice.color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${selectedSlice.label}: ${selectedSlice.value.toInt()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = selectedSlice.color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive D3-inspired Multi-Bar Chart comparing project completion percentage
 * alongside verified readiness score with animated transitions and tooltips.
 */
@Composable
fun RechartsBarChart(
    items: List<BarChartItem>,
    modifier: Modifier = Modifier,
    onBarClick: ((BarChartItem) -> Unit)? = null
) {
    var activeItem by remember { mutableStateOf<BarChartItem?>(null) }
    val progressAnimation = remember { Animatable(0f) }

    LaunchedEffect(items) {
        progressAnimation.snapTo(0f)
        progressAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier
            .testTag("chart_bar_container")
            .padding(vertical = 8.dp)
    ) {
        // Legend row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChartLegendPill(color = PrimaryDarkTeal, label = "Task Completion %")
            Spacer(modifier = Modifier.width(12.dp))
            ChartLegendPill(color = InfoBlue, label = "Readiness Benchmark")
        }

        // Custom Canvas drawing bars with coordinate axes, gridlines, and tooltips
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(items) {
                    detectTapGestures { tapOffset ->
                        val barGroupWidth = size.width / items.size.coerceAtLeast(1)
                        val tappedIndex = (tapOffset.x / barGroupWidth).toInt()
                        if (tappedIndex in items.indices) {
                            val item = items[tappedIndex]
                            activeItem = if (activeItem?.id == item.id) null else item
                            onBarClick?.invoke(item)
                        }
                    }
                }
        ) {
            val chartHeight = size.height - 35f // leave bottom for labels
            val chartWidth = size.width

            // Draw horizontal gridlines (0%, 25%, 50%, 75%, 100%)
            for (level in 0..4) {
                val y = chartHeight - (chartHeight * (level / 4f))
                drawLine(
                    color = BorderLight,
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )
            }

            if (items.isEmpty()) return@Canvas

            val groupWidth = chartWidth / items.size
            val barWidth = min(groupWidth * 0.32f, 28f)
            val spacing = 6f

            items.forEachIndexed { index, item ->
                val groupCenterX = (index * groupWidth) + (groupWidth / 2f)
                val bar1X = groupCenterX - barWidth - (spacing / 2f)
                val bar2X = groupCenterX + (spacing / 2f)

                val compHeight = (chartHeight * (item.completionPercentage / 100f)) * progressAnimation.value
                val readHeight = (chartHeight * (item.readinessScore / 100f)) * progressAnimation.value

                val isSelected = activeItem?.id == item.id

                // Bar 1: Completion Percentage
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            if (isSelected) PrimaryDarkTeal else item.primaryColor,
                            item.primaryColor.copy(alpha = 0.75f)
                        )
                    ),
                    topLeft = Offset(bar1X, chartHeight - compHeight),
                    size = Size(barWidth, compHeight.coerceAtLeast(3f)),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Bar 2: Career Readiness Score
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            if (isSelected) InfoBlue else item.secondaryColor,
                            item.secondaryColor.copy(alpha = 0.65f)
                        )
                    ),
                    topLeft = Offset(bar2X, chartHeight - readHeight),
                    size = Size(barWidth, readHeight.coerceAtLeast(3f)),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Active indicator line under selected project
                if (isSelected) {
                    drawLine(
                        color = PrimaryDarkTeal,
                        start = Offset(groupCenterX - groupWidth * 0.4f, chartHeight + 2f),
                        end = Offset(groupCenterX + groupWidth * 0.4f, chartHeight + 2f),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Baseline axis line
            drawLine(
                color = BorderColor,
                start = Offset(0f, chartHeight),
                end = Offset(chartWidth, chartHeight),
                strokeWidth = 1.5f
            )
        }

        // Bottom project label row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEach { item ->
                val isSelected = activeItem?.id == item.id
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            activeItem = if (activeItem?.id == item.id) null else item
                            onBarClick?.invoke(item)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) PrimaryDarkTeal else SecondaryText,
                        maxLines = 1
                    )
                    Text(
                        text = "${item.completionPercentage.toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isSelected) PrimaryDarkTeal else MutedText
                    )
                }
            }
        }

        // Active project tooltip card (D3 Tooltip overlay equivalent)
        if (activeItem != null) {
            val item = activeItem!!
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = PrimarySoftTeal,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryDarkTeal.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chart_active_tooltip")
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.meta.ifEmpty { item.label },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryDarkTeal,
                            maxLines = 1
                        )
                        Text(
                            text = "Completion: ${item.completionPercentage.toInt()}% • Readiness Index: ${item.readinessScore.toInt()}/100",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryText
                        )
                    }
                    StatusPill(
                        text = if (item.completionPercentage >= 75) "INTERVIEW READY" else "IN PROGRESS",
                        color = if (item.completionPercentage >= 75) SuccessGreen else WarningAmber
                    )
                }
            }
        }
    }
}

/**
 * Recharts Radar / Spider Chart rendering multi-axis engineering dimensions:
 * Architecture, System Design, Concurrency, Testing & CI, API Security, and Database Optimization.
 */
@Composable
fun RechartsRadarChart(
    points: List<RadarDataPoint>,
    modifier: Modifier = Modifier
) {
    val progressAnimation = remember { Animatable(0f) }
    LaunchedEffect(points) {
        progressAnimation.snapTo(0f)
        progressAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .testTag("chart_radar_container"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (min(size.width, size.height) / 2f) - 34f
            val n = points.size.coerceAtLeast(3)
            val angleStep = (2 * PI / n).toFloat()

            // Draw concentric spider web polygons (25%, 50%, 75%, 100%)
            for (level in 1..4) {
                val r = radius * (level / 4f)
                val path = Path()
                for (i in 0 until n) {
                    val angle = -PI.toFloat() / 2f + (i * angleStep)
                    val x = center.x + r * cos(angle)
                    val y = center.y + r * sin(angle)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(
                    path = path,
                    color = BorderLight,
                    style = Stroke(width = 1f)
                )
            }

            // Draw spokes from center
            for (i in 0 until n) {
                val angle = -PI.toFloat() / 2f + (i * angleStep)
                val endX = center.x + radius * cos(angle)
                val endY = center.y + radius * sin(angle)
                drawLine(
                    color = BorderLight,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1f
                )
            }

            // Benchmark Polygon (Target standard 75%)
            val benchmarkPath = Path()
            for (i in 0 until n) {
                val point = points[i]
                val r = radius * (point.benchmark / 100f)
                val angle = -PI.toFloat() / 2f + (i * angleStep)
                val x = center.x + r * cos(angle)
                val y = center.y + r * sin(angle)
                if (i == 0) benchmarkPath.moveTo(x, y) else benchmarkPath.lineTo(x, y)
            }
            benchmarkPath.close()
            drawPath(
                path = benchmarkPath,
                color = WarningAmber.copy(alpha = 0.4f),
                style = Stroke(
                    width = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            )

            // Current Verified Skill Polygon
            val verifiedPath = Path()
            val pointCoords = mutableListOf<Offset>()

            for (i in 0 until n) {
                val point = points[i]
                val r = radius * (point.score / 100f) * progressAnimation.value
                val angle = -PI.toFloat() / 2f + (i * angleStep)
                val x = center.x + r * cos(angle)
                val y = center.y + r * sin(angle)
                pointCoords.add(Offset(x, y))
                if (i == 0) verifiedPath.moveTo(x, y) else verifiedPath.lineTo(x, y)
            }
            verifiedPath.close()

            // Translucent fill
            drawPath(
                path = verifiedPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        PrimaryDarkTeal.copy(alpha = 0.45f),
                        PrimaryTeal.copy(alpha = 0.15f)
                    ),
                    center = center,
                    radius = radius
                ),
                style = Fill
            )

            // Outer stroke
            drawPath(
                path = verifiedPath,
                color = PrimaryDarkTeal,
                style = Stroke(width = 2.5f)
            )

            // Draw vertex nodes
            pointCoords.forEach { pt ->
                drawCircle(
                    color = SurfaceWhite,
                    radius = 5.5f,
                    center = pt
                )
                drawCircle(
                    color = PrimaryDarkTeal,
                    radius = 3.5f,
                    center = pt
                )
            }
        }
    }
}

/**
 * Recharts style Area / Line Sparkline visualizing career readiness velocity over time.
 */
@Composable
fun RechartsReadinessVelocityChart(
    timelinePoints: List<Pair<String, Int>>, // Date to Score
    modifier: Modifier = Modifier
) {
    val progressAnimation = remember { Animatable(0f) }
    LaunchedEffect(timelinePoints) {
        progressAnimation.snapTo(0f)
        progressAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            if (timelinePoints.size < 2) return@Canvas

            val chartWidth = size.width
            val chartHeight = size.height - 24f
            val maxScore = 100f
            val minScore = 40f
            val scoreRange = maxScore - minScore

            val stepX = chartWidth / (timelinePoints.size - 1)

            val linePath = Path()
            val fillPath = Path()

            val points = timelinePoints.mapIndexed { index, pair ->
                val x = index * stepX
                val normalizedScore = (pair.second - minScore) / scoreRange
                val y = chartHeight - (normalizedScore * chartHeight * progressAnimation.value)
                Offset(x, y)
            }

            // Draw horizontal threshold line (Industry Benchmark: 78)
            val benchmarkY = chartHeight - (((78f - minScore) / scoreRange) * chartHeight)
            drawLine(
                color = WarningAmber.copy(alpha = 0.6f),
                start = Offset(0f, benchmarkY),
                end = Offset(chartWidth, benchmarkY),
                strokeWidth = 1.2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            linePath.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, chartHeight)
            fillPath.lineTo(points.first().x, points.first().y)

            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                val cpx = (prev.x + curr.x) / 2f
                linePath.cubicTo(cpx, prev.y, cpx, curr.y, curr.x, curr.y)
                fillPath.cubicTo(cpx, prev.y, cpx, curr.y, curr.x, curr.y)
            }

            fillPath.lineTo(points.last().x, chartHeight)
            fillPath.close()

            // Gradient Area Fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryDarkTeal.copy(alpha = 0.35f),
                        PrimaryDarkTeal.copy(alpha = 0.02f)
                    ),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            // Curving Line
            drawPath(
                path = linePath,
                color = PrimaryDarkTeal,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )

            // Sparkline Point Markers
            points.forEach { pt ->
                drawCircle(color = SurfaceWhite, radius = 5f, center = pt)
                drawCircle(color = PrimaryDarkTeal, radius = 3.5f, center = pt)
            }
        }

        // Timeline X-axis Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            timelinePoints.forEach { (date, score) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MutedText
                    )
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = PrimaryDarkTeal
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartLegendPill(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = SecondaryText
        )
    }
}
