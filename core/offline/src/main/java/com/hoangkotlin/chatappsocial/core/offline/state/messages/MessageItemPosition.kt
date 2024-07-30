package com.hoangkotlin.chatappsocial.core.offline.state.messages

 sealed class MessageItemGroupPosition {

    /**
     * Message that is the first message in the group at the top.
     */
    data object Top : MessageItemGroupPosition()

    /**
     * Message that has another message both at the top and bottom of it.
     */
    data object Middle : MessageItemGroupPosition()

    /**
     * Message that's the last message in the group, at the bottom.
     */
    data object Bottom : MessageItemGroupPosition()

    /**
     * Message that is not in a group.
     */
    data object None : MessageItemGroupPosition()
}
