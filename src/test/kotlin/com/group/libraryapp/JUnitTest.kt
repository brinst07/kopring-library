package com.group.libraryapp

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JUnitTest {
    //companion object -> java static과 같은 역할을 함
    companion object {
        //@JvmStatic을 붙여줘야 한다.
        @JvmStatic
        fun beforeAll() {
            println("모든 테스트 시작 전")
        }

        @JvmStatic
        fun afterAll() {
            println("모든 테스트 종료 후")
        }
    }

    @BeforeEach
    fun beforeEach() {
        println("각 테스트 시작 전")
    }

    @AfterEach
    fun afterEach() {
        println("각 테스트 종료 후")
    }
    @Test
    fun test1() {
        println("테스트 1")
    }

    @Test
    fun test2() {
        println("테스트 2")
    }
}