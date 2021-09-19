package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
// 화면에서 받는 용도
// 왜냐하면 domain Member의 형식과 다르기 때문
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수입니다.")
    private String name;

    private String city;

    private String street;

    private String zipcode;
}
