package com.mimacom.kotlinguild

import java.time.LocalDateTime

/**
 * Acts as entry point into our DSL
 * `inline` - at compile time this code will be pasted into the call side
 * Last argument is a higher order function -> parenthesis are optional
 * Receiver type: dynamic "extension-functions" --- we extend the conference DSL with a function of signature (Void) -> Void
 *    where the implementation is provided at run-time.
 *    Inside the lambda we are in the "scope" of the receiver type.
 * Required params are provided as parameters before the higher-order function
 */
inline fun conference(isImportant: Boolean, config: ConferenceDSL.() -> Unit): Conference {
    val dsl = ConferenceDSL(isImportant).apply(config)
    return Conference(dsl.name, dsl.location).apply {
        dsl.talkList.forEach(this::addTalk)
    }
}

//@DslMarker
class ConferenceDSL(val isImportant: Boolean) {

    private val _talkList = mutableListOf<Talk>()

    val talkList: List<Talk>
        get() = _talkList.toList()

    lateinit var name: String
    lateinit var location: String

    // Refers to our inner DSL which is only accessible from the outer DSL
    val talks = TalkConfigDSL()

    inner class TalkConfigDSL {
        private val _talkList = this@ConferenceDSL._talkList

        // --------------------------------------------------------------------------------

        // Enables us access the `ConferenceDSL#talk` variable inside a receiver-type higher-order function
        operator fun invoke(config: TalkConfigDSL.() -> Unit) {
            this.apply(config)
        }

        // --------------------------------------------------------------------------------

        // Descriptive factory methods
        fun conferenceTalk(topic: String, speaker: String, time: LocalDateTime) {
            _talkList.add(Talk(topic, speaker, time, TalkType.CONFERENCE))
        }

        fun keynoteTalk(topic: String, speaker: String, time: LocalDateTime) {
            _talkList.add(Talk(topic, speaker, time, TalkType.KEYNOTE))
        }

        // --------------------------------------------------------------------------------

        val conferenceTalk: EmptyTalk
            get() = EmptyTalk(TalkType.CONFERENCE)

        val keynoteTalk: EmptyTalk
            get() = EmptyTalk(TalkType.KEYNOTE)

        inner class EmptyTalk(val type: TalkType) {
            // infix - function f with two parameters A and B get invoked _between_ the params: `A f B`
            // "Throw-away" types to slowly build up all the data in a type-safe manner
            infix fun named(name: String) = NamedTalk(this, name)
        }

        inner class NamedTalk(
            val previous: EmptyTalk,
            val talkName: String) {
            infix fun by(speaker: String) = NamedAndAuthoredTalk(this, speaker)
        }

        inner class NamedAndAuthoredTalk(
            val previous: NamedTalk,
            val speaker: String) {
            infix fun at(date: String) =
                _talkList.add(
                    // Re-assemble a whole data-class
                    Talk(previous.talkName, speaker, LocalDateTime.parse(date), previous.previous.type)
                )
        }
        // --------------------------------------------------------------------------------
        // operator overloading defined as member function of DSL -> doesn't pollute the Talk class!
        operator fun Talk.unaryPlus() = _talkList.add(this)
    }
}

class Conference(val name: String, val location: String) {
    private val schedule = mutableListOf<Talk>()

    fun addTalk(t: Talk) {
        schedule.add(t)
    }

    val talks
        get() = schedule.toList()
}

data class Talk(
    val topic: String,
    val speaker: String,
    val time: LocalDateTime,
    val type: TalkType = TalkType.CONFERENCE
)

enum class TalkType {
    CONFERENCE, KEYNOTE
}
