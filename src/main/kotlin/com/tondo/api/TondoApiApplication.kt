package com.tondo.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TondoApiApplication

fun main(args: Array<String>) {
    runApplication<TondoApiApplication>(*args)
}
