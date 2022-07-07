package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

// 트랜잭션 - 트랜잭션 매니저
// DataSourceUtils.getConnection()
//         DataSourceUtils.releaseConnection()

@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
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
            log.error("db error", e);
            throw e;
        }finally {

           close(con, pstmt, null);
        }

    }

    // 조회 기능
    public Member findById(String memberId) throws SQLException {

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
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, rs);
        }


    }


    public Member findById(Connection con, String memberId) throws SQLException {

        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;



        try {
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
            log.error("db error", e);
            throw e;
        }finally {

            // 하나의 세션에서 transaction이 이뤄줘야 하므로 connection을 여기서 닫지 않는다
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
          //  JdbcUtils.closeConnection(con);
        }


    }

    // 수정
    public void update(String memberId, int money) throws SQLException {
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
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    // 수정(connection 유지)
    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);

        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }


    // 삭제
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
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
