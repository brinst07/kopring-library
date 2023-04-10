package com.group.libraryapp.caculator
//backing property convention
//생성자 쪽에 _를 붙인다.

//backing property를 사용하는 것보다 public으로 열어두고 팀내 convention을 setter를 사용하지 않는 쪽으로 진행하는 것이 더 좋다.
class Calculator(
//    private var _number: Int
    var number: Int
) {
    //custom getter를 생성한다.
//    val number: Int
//        get() = this._number

    fun add(operand: Int) {
        this.number += operand
    }

    fun minus(operand: Int) {
        this.number -= operand
    }

    fun multiply(operand: Int) {
        this.number *= operand
    }

    fun divide(operand: Int) {
        if (operand == 0) {
            throw IllegalArgumentException("0으로 나눌 수 없습니다.")
        }
        this.number /= operand
    }

}