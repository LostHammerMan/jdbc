package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;


/*
 *  JdbcTemplate 사용
 *
 */

@Slf4j
public class MemberRepositoryV5 implements MemberRepository{


    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override // 컴파일러가 오류 잡아줌
    public Member save(Member member){
        String sql = "insert into member(member_id, money) values (?, ?)";
        template.update(sql, member.getMemberId(), member.getMoney());

        return member;

//        Connection con = null;
//        PreparedStatement pstmt = null; // DB에 쿼리를 날림

//        try{
//            con = getConnection();
//            pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, member.getMemberId()); // 파라미터 바이딩(values (?, ?))
//            pstmt.setInt(2, member.getMoney());
//            pstmt.executeUpdate();
//            return member;
//        }catch (SQLException e){
//           DataAccessException ex =  exTranslator.translate("save", sql, e);
//           throw ex;
//
//        }finally {
//           close(con, pstmt, null);
//        }

    }

    // 조회 기능
    @Override
    public Member findById(String memberId){

        String sql = "select * from member where member_id = ?";
        Member member = template.queryForObject(sql, memberRowMapper(), memberId);
        return member;

//        Connection con = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;



//        try {
//            con = getConnection();
//            pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, memberId);
//            rs = pstmt.executeQuery(); // select query를 담고있는 통
//
//            if (rs.next()){
//                Member member = new Member();
//                member.setMemberId(rs.getString("member_id"));
//                member.setMoney(rs.getInt("money"));
//                return member;
//
//            }else {
//                // 예외가 터졌을 떄 어디에서 문제가 발생한지 알 수 있도록 구체적 작성
//                throw new NoSuchElementException("member not found memberId=" + memberId);
//            }
//
//        } catch (SQLException e) {
//            throw new MyDbException(e);
//
//        }finally {
//            close(con, pstmt, rs);
//        }


    }





    // 수정
    @Override
    public void update(String memberId, int money){
        String sql = "update member set money=? where member_id=?";
        template.update(sql, money, memberId); // 업데이트 된 row 수 반환
//        Connection con = null;
//        PreparedStatement pstmt = null;
//
//        try{
//            con = getConnection();
//            pstmt = con.prepareStatement(sql);
//            pstmt.setInt(1, money);
//            pstmt.setString(2, memberId);
//            int resultSize = pstmt.executeUpdate();
//            log.info("resultSize={}", resultSize);
//
//        }catch (SQLException e){
//            throw new MyDbException(e);
//
//        }finally {
//            close(con, pstmt, null);
//        }
    }


    // 삭제
    @Override
    public void delete(String memberId){
        String sql = "delete from member where member_id = ?";
        template.update(sql, memberId);

//        Connection con = null;
//        PreparedStatement pstmt = null;
//
//        try{
//            con = getConnection();
//            pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, memberId);
//            pstmt.executeUpdate();
//        }catch (SQLException e){
//            throw new MyDbException(e);
//
//        }finally {
//            close(con, pstmt, null);
//        }


    }

    private RowMapper<Member> memberRowMapper(){
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }



//    private void close(Connection con, Statement stmt, ResultSet rs){
//
//        JdbcUtils.closeResultSet(rs);
//        JdbcUtils.closeStatement(stmt);
//
//        // 트랜잭션 동기화를 사용하려면 DataSourceUtils 를 사용해야 함
//        DataSourceUtils.releaseConnection(con, dataSource);
//
//    }
//
//    private Connection getConnection() throws SQLException {
//
//        // 트랜잭션 동기화를 사용하려면 DataSourceUtils 를 사용해야 함
//        Connection con = DataSourceUtils.getConnection(dataSource);
//        log.info("get connection={}, class={}", con, con.getClass());
//        return con;
//    }

}
