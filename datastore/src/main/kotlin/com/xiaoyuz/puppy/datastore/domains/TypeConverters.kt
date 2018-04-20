package com.xiaoyuz.puppy.datastore.domains

import com.xiaoyuz.puppy.common.exceptions.FormatException
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class VideoSourceConverter : AttributeConverter<VideoSource, Int> {
    override fun convertToDatabaseColumn(attribute: VideoSource): Int {
        return attribute.value
    }

    override fun convertToEntityAttribute(dbData: Int?): VideoSource {
        return if (dbData == null) throw FormatException("Format is null") else VideoSource.values()
                .firstOrNull { it.value == dbData } ?: throw FormatException("Format $dbData is illegal")
    }
}

@Converter(autoApply = true)
class VideoTypeConverter : AttributeConverter<VideoType, Int> {
    override fun convertToDatabaseColumn(attribute: VideoType): Int {
        return attribute.value
    }

    override fun convertToEntityAttribute(dbData: Int?): VideoType {
        return if (dbData == null) throw FormatException("Format is null") else VideoType.values()
                .firstOrNull { it.value == dbData } ?: throw FormatException("Format $dbData is illegal")
    }
}