package hello.member;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class MemberRepository {

    private final JdbcTemplate template;

    public void initTable() {
        template.execute("create table member(member_id varchar primary key , name varchar)");
    }

    public void save(Member member) {
        template.update("insert into Member(member_id, name) values (?,?)", member.getMemberId(), member.getName());
    }

    public Member find(String memberId) {
        return template.queryForObject("select member_id, name from Member where member_id = ?", BeanPropertyRowMapper.newInstance(Member.class), memberId);
    }

    public List<Member> findAll() {
        return template.query("select member_id, name from Member", BeanPropertyRowMapper.newInstance(Member.class));
    }
}
