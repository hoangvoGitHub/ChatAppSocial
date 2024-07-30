package com.hoangkotlin.chatappsocial.core.network.model.request

/**
 * The `QueryMessagesDirection` enum defines a set of constants used to specify the direction of querying messages based on their IDs.
 * Each constant in this enum is associated with a specific string value that represents the query parameter used for message retrieval.
 */
enum class QueryMessagesDirection(private val value: String) {
    /**
     * Represents a query condition where the message timestamp is greater than a specified value.
     *
     * Value: "time_gt"
     */
    NEWER_THAN("time_gt"),

    /**
     * Represents a query condition where the message timestamp is greater than or equal to a specified value.
     *
     * Value: "time_gte"
     */
    NEWER_THAN_OR_EQUAL("time_gte"),

    /**
     * Represents a query condition where the message timestamp is less than a specified value.
     *
     * Value: "time_lt"
     */
    OLDER_THAN("time_lt"),

    /**
     * Represents a query condition where the message timestamp is less than or equal to a specified value.
     *
     * Value: "time_lte"
     */
    OLDER_THAN_OR_EQUAL("time_lte"),

    /**
     * Represents a query condition that retrieves messages both newer and older than a specified message timestamp.
     *
     * Value: "time_around"
     */
    AROUND_TIME("time_around");

    /**
     * Returns the string value associated with the enum constant.
     *
     * @return the string value of the enum constant.
     */

    override fun toString(): String {
        return value
    }
}