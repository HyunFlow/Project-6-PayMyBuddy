package com.openclassrooms.pay_my_buddy.application.service.relation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.model.UserRelation;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountType;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRelationRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import com.openclassrooms.pay_my_buddy.web.dto.transaction.RelationOptionDTO;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RelationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRelationRepository relationRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private RelationService relationService;

    private User me, target;
    private Account myAccount, targetAccount;
    private UserRelation myRelation;

    @BeforeEach
    public void setUp() {
        me = User.builder()
            .id(1)
            .email("me@email.com")
            .build();

        target = User.builder()
            .id(2)
            .email("target@email.com")
            .build();

        myAccount = new Account();
        myAccount.setAccountId(1);
        myAccount.setUser(me);

        targetAccount = new Account();
        targetAccount.setAccountId(2);
        targetAccount.setUser(target);

        myRelation = new UserRelation();
        myRelation.setDefiningUser(me);
        myRelation.setRelatedUser(target);
        myRelation.setRelationName("BENEFICIARY");
    }

    @Test
    void addBeneficiary_success_saveRelationOnce() {
        // given
        String myEmail = "me@email.com";
        String targetEmail = "target@email.com";

        when(userRepository.findByEmail(myEmail)).thenReturn(Optional.of(me));
        when(userRepository.findByEmail(targetEmail)).thenReturn(Optional.of(target));
        when(relationRepository.existsByDefiningUserIdAndRelatedUserId(me.getId(),
            target.getId())).thenReturn(false);

        //when
        relationService.addBeneficiary(myEmail, targetEmail);

        //then
        ArgumentCaptor<UserRelation> captor = ArgumentCaptor.forClass(UserRelation.class);
        verify(relationRepository, times(1)).save(captor.capture());
        UserRelation saved = captor.getValue();

        assertThat(saved.getDefiningUser()).isEqualTo(me);
        assertThat(saved.getRelatedUser()).isEqualTo(target);
        assertThat(saved.getRelationName()).isEqualTo("BENEFICIARY");

        verify(relationRepository, times(1)).existsByDefiningUserIdAndRelatedUserId(1, 2);
        verifyNoMoreInteractions(relationRepository);
    }

    @Test
    void addBeneficiary_alreadyExistRelation_noSave() {
        // given
        String myEmail = "me@email.com";
        String targetEmail = "target@email.com";

        when(userRepository.findByEmail(myEmail)).thenReturn(Optional.of(me));
        when(userRepository.findByEmail(targetEmail)).thenReturn(Optional.of(target));
        when(relationRepository.existsByDefiningUserIdAndRelatedUserId(1, 2)).thenReturn(true);

        // when
        relationService.addBeneficiary(myEmail, targetEmail);

        // then
        verify(relationRepository, never()).save(any());
    }

    @Test
    void addBeneficiary_useSameEmailForTarget_throwsException() {
        // given
        String myEmail = "me@email.com";
        String targetEmail = "me@email.com";

        when(userRepository.findByEmail(myEmail)).thenReturn(Optional.of(me));
        when(userRepository.findByEmail(targetEmail)).thenReturn(Optional.of(me));

        // when
        IllegalArgumentException e =
            assertThrows(IllegalArgumentException.class,
                () -> relationService.addBeneficiary(myEmail, targetEmail));

        // then
        assertTrue(e.getMessage().contains("Vous ne pouvez pas vous ajouter vous‑même."));
        verify(relationRepository, never()).save(any());
    }

    @Test
    void addBeneficiary_currentUserNotFound_throwsException() {
        // given
        String myEmail = "unknown@email.com";
        String targetEmail = "target@email.com";
        when(userRepository.findByEmail(myEmail)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException e =
            assertThrows(IllegalArgumentException.class,
                () -> relationService.addBeneficiary(myEmail, targetEmail));

        // then
        assertTrue(e.getMessage().contains("Utilisateur introuvable."));
        verify(relationRepository, never()).save(any());
    }

    @Test
    void addBeneficiary_targetUserNotFound_throwsException() {
        // given
        String myEmail = "me@email.com";
        String targetEmail = "unknown@email.com";
        when(userRepository.findByEmail(myEmail)).thenReturn(Optional.of(me));

        // when
        IllegalArgumentException e =
            assertThrows(IllegalArgumentException.class,
                () -> relationService.addBeneficiary(myEmail, targetEmail));

        // then
        assertTrue(e.getMessage().contains("Aucun utilisateur avec cet e-mail."));
        verify(relationRepository, never()).save(any());
    }

    @Test
    void getRelationOptions_whenInputAccountIdExist_shouldReturnListOfRelation() {
        // given
        Integer inputAccId = 1;
        when(accountRepository.findById(inputAccId)).thenReturn(Optional.of(myAccount));

        Integer ownerId = myAccount.getUser().getId();
        when(relationRepository.findAllByDefiningUserId(ownerId)).thenReturn(List.of(myRelation));

        Integer targetUserId = target.getId();
        when(accountRepository.findByUserIdAndAccountType(targetUserId, AccountType.CHECKING)).thenReturn(Optional.of(targetAccount));

        // when
        List<RelationOptionDTO> result = relationService.getRelationOptions(inputAccId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountId()).isEqualTo(targetAccount.getAccountId());
        assertThat(result.get(0).getEmail()).isEqualTo(target.getEmail());

        verify(accountRepository).findById(1);
        verify(relationRepository).findAllByDefiningUserId(me.getId());
        verify(accountRepository).findByUserIdAndAccountType(target.getId(), AccountType.CHECKING);
    }

    @Test
    void getRelationOptions_skipsIfNoAccountFound() {
        // given
        Integer inputAccId = 1;
        when(accountRepository.findById(inputAccId)).thenReturn(Optional.of(myAccount));
        when(relationRepository.findAllByDefiningUserId(me.getId()))
            .thenReturn(List.of(myRelation));
        when(accountRepository.findByUserIdAndAccountType(target.getId(), AccountType.CHECKING))
            .thenReturn(Optional.empty());
        when(accountRepository.findTopByUserIdOrderByAccountIdAsc(target.getId()))
            .thenReturn(Optional.empty());

        // when
        List<RelationOptionDTO> result = relationService.getRelationOptions(inputAccId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void getRelationOptions_accountNotFound_throws() {
        // given
        Integer inputAccId = 1;
        when(accountRepository.findById(inputAccId)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> relationService.getRelationOptions(inputAccId));

        // then
        assertTrue(e.getMessage().contains("compte introuvable"));
    }


}
