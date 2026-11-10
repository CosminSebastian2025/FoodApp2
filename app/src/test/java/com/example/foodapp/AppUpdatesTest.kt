package com.example.foodapp

import com.example.foodapp.data.AppUpdates
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdatesTest {

    @Test
    fun newerPatchVersionIsDetected() {
        assertTrue(AppUpdates.isNewer("1.1.8", "1.1.7"))
    }

    @Test
    fun comparisonIsNumericNotLexical() {
        assertTrue(AppUpdates.isNewer("1.1.10", "1.1.9"))
    }

    @Test
    fun sameVersionIsNotNewer() {
        assertFalse(AppUpdates.isNewer("1.1.7", "1.1.7"))
    }

    @Test
    fun olderVersionIsNotNewer() {
        assertFalse(AppUpdates.isNewer("1.0.9", "1.1.0"))
    }

    @Test
    fun tagPrefixIsIgnored() {
        assertTrue(AppUpdates.isNewer("v1.2", "1.1.7"))
    }
}
