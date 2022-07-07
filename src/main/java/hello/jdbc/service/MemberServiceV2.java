package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

// 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {



    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    // 계좌 이체 로직 -> 트랜잭션 시작해야 하는 위치
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // connection 얻기
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false); // 트랜잭션 시작

            // 비즈니스 로직
            bizLogic(con,fromId, toId, money);
            con.commit(); // 성공시 커밋

        }catch (Exception e){
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);

        }finally {
            release(con);
        }



        // 커밋 또는 롤백
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

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con,fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);


        validation(toMember);

        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
