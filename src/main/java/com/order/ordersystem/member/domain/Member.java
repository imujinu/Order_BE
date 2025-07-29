package com.order.ordersystem.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// jpql을 제외하고 모든 조회쿼리에  where del_yn  = "N"을 붙이는 효과
@Where(clause = "del_yn ='N'")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Builder.Default
    private String delYn = "N";


    public void deleteMember(String delYn) {
        this.delYn=delYn;
    }

}
