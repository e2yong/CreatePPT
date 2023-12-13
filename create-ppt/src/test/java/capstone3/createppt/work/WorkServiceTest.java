package capstone3.createppt.work;

import capstone3.createppt.repository.MemberRepository;
import capstone3.createppt.repository.WorkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WorkServiceTest {

    @Autowired
    WorkRepository workRepository;

    @Autowired
    WorkService workService;

    @Test
    void deleteWork() {
    }
}