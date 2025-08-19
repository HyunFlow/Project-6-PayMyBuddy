package com.openclassrooms.pay_my_buddy.domain.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.model.UserRelation;
import com.openclassrooms.pay_my_buddy.domain.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UserRelationRepositoryTest {

    @Autowired
    UserRelationRepository userRelationRepo;

    @Autowired
    UserRepository userRepo;

    @Test
    void uniqueConstraint_preventsDuplicates() {
        // given
        User a = User.builder()
            .id(null)
            .username("A")
            .email("a@email.com")
            .password("password")
            .role(Role.USER)
            .build();

        User b = User.builder()
            .id(null)
            .username("B")
            .email("b@email.com")
            .password("password")
            .role(Role.USER)
            .build();

        userRepo.save(a);
        userRepo.save(b);

        // when
        UserRelation r1 = new UserRelation();
        r1.setDefiningUser(a);
        r1.setRelatedUser(b);

        userRelationRepo.save(r1);
        userRelationRepo.flush();

        // then
        UserRelation duplication = new UserRelation();
        duplication.setDefiningUser(a);
        duplication.setRelatedUser(b);

        assertThatThrownBy(() -> {
            userRelationRepo.save(duplication);
            userRelationRepo.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
