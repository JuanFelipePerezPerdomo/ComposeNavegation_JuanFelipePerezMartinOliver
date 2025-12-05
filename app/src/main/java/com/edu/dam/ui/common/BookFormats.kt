package com.edu.dam.ui.common

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val defaultFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@RequiresApi(Build.VERSION_CODES.O)
fun formatBookTimestamp(
    epochMillis: Long,
    formatter: DateTimeFormatter = defaultFormatter
): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(formatter)