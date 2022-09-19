package com.mimacom.kotlinguild

import java.time.LocalDateTime

fun main() {
    val guild = Conference("Kotlin Guild", "MS Teams")
    val t1 = Talk("Intro to Kotlin", "Sebastian Brunner", LocalDateTime.parse("2022-01-05T12:00"))
    val t2 = Talk("DSLs in Kotlin", "Hasan Kara", LocalDateTime.now())
    guild.addTalk(t1)
    guild.addTalk(t2)

    // TODO context receiver with location lookup
    // TODO DSL annotations

    conference(isImportant = true) {
        name = "Kotlin Guild"
        location = "MS Teams"

        talks {
            conferenceTalk("Kotlin 101", "Speaker 1", LocalDateTime.parse("2022-01-05T12:00"))
            keynoteTalk("Kotlin 101", "Speaker 1", LocalDateTime.parse("2022-01-05T12:00"))

            conferenceTalk named "Kotlin 101" by "Speaker 1" at "2022-01-05T12:00"
            keynoteTalk named "Kotlin 101" by "Speaker 2" at "2022-01-05T12:00"

            +Talk("Kotlin 101", "Speaker 1", LocalDateTime.parse("2022-01-05T12:00"))
        }

        // `keynoteTalk` not accessible outside of `talks`

        // Same as `talks { ... }`
        talks.conferenceTalk("Kotlin 101", "Speaker 1", LocalDateTime.parse("2022-01-05T12:00"))
    }
}
