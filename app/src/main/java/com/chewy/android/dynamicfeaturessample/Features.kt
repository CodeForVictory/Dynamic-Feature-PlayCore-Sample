package com.chewy.android.dynamicfeaturessample

import android.content.Context

sealed class Feature : FeatureInformation {
    class TheMeg(context: Context) : Feature() {
        override val name: String = context.getString(R.string.feature_the_meg)
        override val dependencies: List<Class<*>>
            get() = listOf()
    }
}

fun feature(context: Context, name: String): Feature = allFeatures(context).first { feature -> feature.name == name }

fun featureNames(context: Context): List<String> = allFeatures(context).map { feature -> feature.name }

fun featureNames(features: List<Feature>): List<String> = features.map { feature -> feature.name }

fun allFeatures(context: Context): List<Feature> = listOf(Feature.TheMeg(context))

interface FeatureInformation {
    val name: String
    val dependencies: List<Class<*>>
        get() = listOf()
}

enum class FeatureDataState {
    LOADING,
    SUCCESSFULLY_LOADED,
    ERROR,
}