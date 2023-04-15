package com.group.libraryapp.domain.book

import javax.persistence.*

@Entity
//constuctor를 생략 가능하지만, 해당 Entity를 생성하는 곳만 추적하고 싶다면 작성해서 추적하면 좋다.
class Book constructor(
    val name: String,
    @Enumerated(EnumType.STRING)
    val type: BookType,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) {
    init {
        if (name.isBlank()) {
            throw IllegalStateException("이름은 비어 있을 수 없습니다.")
        }
    }
    //test code를 위한 companion object
    companion object {
        fun fixture(
            name: String = "책 이름",
            type: BookType = BookType.COMPUTER,
            id: Long? = null
        ): Book {
            return Book(
                name = name,
                type = type,
                id = id
            )
        }
    }
}