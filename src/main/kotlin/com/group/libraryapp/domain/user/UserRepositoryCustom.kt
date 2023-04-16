package com.group.libraryapp.domain.user

interface UserRepositoryCustom {
    fun findAllWithHisotries() : List<User>
}