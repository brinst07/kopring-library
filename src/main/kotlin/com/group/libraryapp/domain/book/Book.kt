package com.group.libraryapp.domain.book

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
@Entity
//constuctor를 생략 가능하지만, 해당 Entity를 생성하는 곳만 추적하고 싶다면 작성해서 추적하면 좋다.
class Book constructor(
    val name: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
){
    init {
        if (name.isBlank()) {
            throw IllegalStateException("이름은 비어 있을 수 없습니다.")
        }
    }
}