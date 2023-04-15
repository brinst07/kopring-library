package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
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
    private val userService: UserService,
    private val userLoanHistoryRepository : UserLoanHistoryRepository
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
        //한번 저장하고 난 다음에는 절대 id가 null이 아니기에 단언처리 해준다.
        //Java UserUpdateRequest의 long이라서 발생하는 문제
        val request = UserUpdateRequest(user.id!!, "B")
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

    @Test
    @DisplayName("대출 기록이 없는 유저도 응답에 포함된다.")
    fun getUserLoanHistoriesTest1() {
        //given
        userRepository.save(User("A",null))
        //when
        val result = userService.getUserLoanHistories()
        //then
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("A")
        assertThat(result[0].books).isEmpty()
    }

    @Test
    @DisplayName("대출 기록이 많은 유저의 응답이 정상 동작한다.")
    fun getUserLoanHistoriesTest2() {
        //given
        val savedUser = userRepository.save(User("A", null))
        userLoanHistoryRepository.saveAll(listOf(
            UserLoanHistory.fixture(savedUser,"책1", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUser,"책2", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUser,"책3", UserLoanStatus.RETURNED),
        ))

        //when
        val result = userService.getUserLoanHistories()
        //then
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("A")
        assertThat(result[0].books).hasSize(3)
        assertThat(result[0].books).extracting("name").containsExactlyInAnyOrder("책1","책2","책3")
        assertThat(result[0].books).extracting("isReturn").containsExactlyInAnyOrder(false,false,true)
    }

    @Test
    @DisplayName("대출 기록 종합 테스트")
    fun getUserLoanHistoriesTest3() {
        //given
        val savedUsers = userRepository.saveAll(
            listOf(
                User("A", null),
                User("B", null),
            )
        )
        userLoanHistoryRepository.saveAll(listOf(
            UserLoanHistory.fixture(savedUsers[0],"책1", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUsers[0],"책2", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUsers[0],"책3", UserLoanStatus.RETURNED),
        ))

        //when
        val result = userService.getUserLoanHistories()
        //then
        assertThat(result).hasSize(2)
        val userAResult = result.first { it.name == "A" }
        assertThat(userAResult.name).isEqualTo("A")
        assertThat(userAResult.books).hasSize(3)
        assertThat(userAResult.books).extracting("name").containsExactlyInAnyOrder("책1","책2","책3")
        assertThat(userAResult.books).extracting("isReturn").containsExactlyInAnyOrder(false,false,true)

        val userBResult = result.first { it.name == "B" }
        assertThat(userBResult.books).isEmpty()
    }
}