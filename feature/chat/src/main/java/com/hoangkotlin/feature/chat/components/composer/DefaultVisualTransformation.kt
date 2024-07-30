package com.hoangkotlin.feature.chat.components.composer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

internal class DefaultVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText = TransformedText(
        text = text,
        offsetMapping = DefaultOffsetMapping()
    )
}

private class DefaultOffsetMapping : OffsetMapping {
    // @param: offset -> offset (cursor position) of original text
    override fun originalToTransformed(offset: Int): Int = offset
    override fun transformedToOriginal(offset: Int): Int = offset
}

