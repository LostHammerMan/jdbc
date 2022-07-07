package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    // 계좌 이체 로직 -> 트랜잭션 시작해야 하는 위치
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // 시작
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);


        // 예외확인 케이스 추가
        validation(toMember);

        memberRepository.update(toId, toMember.getMoney() + money);

        // 커밋 또는 롤백
    }

    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
