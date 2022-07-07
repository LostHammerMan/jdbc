package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

// 트랜잭션 - 트랜잭션 템플릿
@Slf4j
//@RequiredArgsConstructor
public class MemberServiceV3_2 {



//    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;

    // 트랜잭션 템플릿
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository){
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    // 계좌 이체 로직 -> 트랜잭션 시작해야 하는 위치
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // connection 얻기
//        Connection con = dataSource.getConnection();


        txTemplate.executeWithoutResult((status) -> {

            try {
                // 비즈니스 로직
                bizLogic(fromId, toId, money);
            }catch (SQLException e){
                throw new IllegalStateException(e);
            }
        });
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); // 커넥션 풀 고려
                con.close();
            }catch (Exception e){
                log.info("error", e); // exception을 로그로 남기는 경우 error={} 사용 안함, 바로 error
            }
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);


        validation(toMember);

        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
