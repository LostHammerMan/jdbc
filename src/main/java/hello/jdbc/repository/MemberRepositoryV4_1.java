package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

// 트랜잭션 - 예외 누수 문제 해결
// 체크 예외 -> 런타임 예외 변경
/*
 *  MemberRepository 인터페이스 사용
 *  throws SQLException 제거
 */

@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository{

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override // 컴파일러가 오류 잡아줌
    public Member save(Member member){
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null; // DB에 쿼리를 날림

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId()); // 파라미터 바이딩(values (?, ?))
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        }catch (SQLException e){
            throw new MyDbException(e);
        }finally {

           close(con, pstmt, null);
        }

    }

    // 조회 기능
    @Override
    public Member findById(String memberId){

        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;



        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery(); // select query를 담고있는 통

            if (rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;

            }else {
                // 예외가 터졌을 떄 어디에서 문제가 발생한지 알 수 있도록 구체적 작성
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            throw new MyDbException(e);

        }finally {
            close(con, pstmt, rs);
        }


    }



    // 수정
    @Override
    public void update(String memberId, int money){
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);

        }catch (SQLException e){
            throw new MyDbException(e);

        }finally {
            close(con, pstmt, null);
        }
    }


    // 삭제
    @Override
    public void delete(String memberId){
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            throw new MyDbException(e);

        }finally {
            close(con, pstmt, null);
        }


    }

    private void close(Connection con, Statement stmt, ResultSet rs){

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        // 트랜잭션 동기화를 사용하려면 DataSourceUtils 를 사용해야 함
        DataSourceUtils.releaseConnection(con, dataSource);

    }

    private Connection getConnection() throws SQLException {

        // 트랜잭션 동기화를 사용하려면 DataSourceUtils 를 사용해야 함
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }

}
