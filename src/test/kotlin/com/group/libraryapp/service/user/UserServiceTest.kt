package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import com.group.libraryapp.service.user.UserService
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

//constructor 앞에 @Autowired를 붙임으로써 중복된 코드를 해결할 수 있다.
@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService
) {
    @AfterEach
    fun clean() {
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("유저 저장이 정상 동작한다.")
    fun saveUserTest() {
        //given
        val userCreateRequest = UserCreateRequest("박희찬", null)

        //when
        userService.saveUser(userCreateRequest)

        //then
        val results = userRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results.get(0).name).isEqualTo("박희찬")
        assertThat(results.get(0).age).isNull()
    }

    @Test
    @DisplayName("유저 조회가 정상적으로 동작한다.")
    fun getUsersTest() {
        //given
        userRepository.saveAll(
            listOf(
                User("A", 20),
                User("B", null)
            )
        )
        //when
        val results = userService.getUsers()
        //then
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder("A", "B")
        assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)
    }

    @Test
    @DisplayName("유저 업데이트가 정상적으로 동작한다.")
    fun updateUserNameTest() {
        //given
        val user = userRepository.save(User("A", null))
        val request = UserUpdateRequest(user.id, "B")
        //when
        userService.updateUserName(request)
        //then
        val result = userRepository.findAll()[0]
        assertThat(result.name).isEqualTo("B")
    }

    @Test
    @DisplayName("유저 삭제가 정상적으로 동작한다.")
    fun deleteUserTest() {
        //given
        val user = userRepository.save(User("A", null))
        //when
        userService.deleteUser(user.name)
        //then
        assertThat(userRepository.findAll()).isEmpty()
    }
}