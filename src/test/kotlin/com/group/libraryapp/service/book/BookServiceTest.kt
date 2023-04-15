package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.math.sign

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository
) {

    @AfterEach
    fun clean() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("책 등록이 정상 동작한다.")
    fun saveBookTest() {
        //given
        val bookRequest = BookRequest("이상한 나라의 엘리스", BookType.COMPUTER)

        //when
        bookService.saveBook(bookRequest)

        //then
        val result = bookRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("이상한 나라의 엘리스")
        assertThat(result[0].type).isEqualTo(BookType.COMPUTER)
    }

    @Test
    @DisplayName("책 대출이 정상 동작한다.")
    fun loanBookTest() {
        //given
        bookRepository.save(Book.fixture("이펙티브 자바"))
        val savedUser = userRepository.save(User("박희찬", 30))
        val bookLoanRequest = BookLoanRequest("박희찬", "이펙티브 자바")

        //when
        bookService.loanBook(bookLoanRequest)
        //then
        val result = userLoanHistoryRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].user.id).isEqualTo(savedUser.id)
        assertThat(result[0].bookName).isEqualTo("이펙티브 자바")
        assertThat(result[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    @DisplayName("책이 진작 대출되어 있다면, 신규 대출이 실패한다.")
    fun loanBookFailTest() {
        //given
        bookRepository.save(Book.fixture("이펙티브 자바"))
        val savedUser = userRepository.save(User("박희찬", 30))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, "이펙티브 자바"))
        val bookLoanRequest = BookLoanRequest("박희찬", "이펙티브 자바")

        //when & then
        assertThrows<IllegalArgumentException> {
            bookService.loanBook(bookLoanRequest)
        }.apply {
            assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
        }
    }

    @Test
    @DisplayName("책 반납이 정상 동작한다.")
    fun returnBookTest() {
        //given
        bookRepository.save(Book.fixture("이펙티브 자바"))
        val savedUser = userRepository.save(User("박희찬", 30))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, "이펙티브 자바"))
        val bookLoanRequest = BookReturnRequest("박희찬", "이펙티브 자바")
        //when
        bookService.returnBook(bookLoanRequest)
        //then
        val result = userLoanHistoryRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    @DisplayName("책 대여 권수를 정상 확인한다.")
    fun countLoanedBookTest() {
        //given
        val savedUser = userRepository.save(User("박희찬", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "A"),
                UserLoanHistory.fixture(savedUser, "B", UserLoanStatus.RETURNED),
                UserLoanHistory.fixture(savedUser, "C", UserLoanStatus.RETURNED)
            )
        )
        //when
        val result = bookService.countLoanedBook()
        //then
        assertThat(result).isEqualTo(1)
    }

    @Test
    @DisplayName("분야별 책 권수를 정상 확인한다.")
    fun getBookStatisticsTest() {
        //given
        bookRepository.saveAll(
            listOf(
                Book.fixture("A", BookType.COMPUTER),
                Book.fixture("B", BookType.COMPUTER),
                Book.fixture("C", BookType.SCIENCE),
            )
        )
        //when
        val result = bookService.getBookStatistics()
        //then
        assertThat(result).hasSize(2)
        assertCount(result, BookType.COMPUTER,2L)
        assertCount(result, BookType.SCIENCE,1L)
    }

    private fun assertCount(results : List<BookStatResponse>, type: BookType, count : Long) {
        assertThat(results.first { it.type == type}.count).isEqualTo(count)
    }
}