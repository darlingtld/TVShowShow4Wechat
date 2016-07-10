package lingda.tang;

import lingda.tang.engine.MovieEngine;
import lingda.tang.pojo.Show;
import lingda.tang.service.EventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class AppTests {
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    private EventService eventService;

    @Autowired
    private MovieEngine movieEngine;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void simple() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("hello"));
    }

    @Test
    public void testSearchMovies() throws InterruptedException {
        String content = "侏罗纪";
        String fromUserName = "asd";
        String toUserName = "324";
        String emailAddress = "darlingtld@qq.com";
        eventService.sendDownloadLinkMailToUsers(content, emailAddress, fromUserName, toUserName);
        Thread.sleep(100000);
    }

    @Test
    public void testSearchMoviesByDygod() throws InterruptedException {
        String content = "侏罗纪";
        List<Show> showList = movieEngine.searchShows(content);
        System.out.println(showList);
    }
}
