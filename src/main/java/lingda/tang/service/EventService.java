package lingda.tang.service;


import lingda.tang.engine.MovieEngine;
import lingda.tang.mail.SimpleMailSender;
import lingda.tang.pojo.BTLink;
import lingda.tang.pojo.Show;
import lingda.tang.pojo.message.req.TextMessage;
import lingda.tang.util.MessageUtil;
import lingda.tang.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by darlingtld on 2015/4/26.
 */
@Service
public class EventService {

    @Autowired
    private MovieEngine movieEngine;

    public String sendDownloadLinkMailToUsers(final String name, final String emailAddress, String fromUserName, String toUserName) {
        new Thread() {
            @Override
            public void run() {
                List<Show> showList = movieEngine.searchShows(name);
                sendMail(name, showList, emailAddress);
            }
        }.start();
        String respContent = String.format("您搜索的 %s 会在稍后将下载链接发送到邮箱\n%s", name, emailAddress);
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(fromUserName);
        textMessage.setFromUserName(toUserName);
        textMessage.setCreateTime(new Date().getTime());
        textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
        textMessage.setContent(respContent);
        return MessageUtil.messageToXml(textMessage);
    }

    private void sendMail(String name, List<Show> showList, String emailAddress) {
        String subject = String.format("%s 下载地址", name);
        StringBuffer htmlContent = new StringBuffer();
        if (showList.isEmpty()) {
            htmlContent.append(String.format("<font color='red' size='5'>%s</font>", Strings.NO_MOVIE_FOUND));
        } else {
            for (Show show : showList) {
                System.out.println(show);
                htmlContent.append(String.format("<font color='blue' size='6' face='verdana'>影片名称 ：%s</font><br>", show.getName()));
                for (BTLink btLink : show.getBtLinkList()) {
                    htmlContent.append(String.format("<font color='green' size='5'>描述 ：%s</font><br>", btLink.getDescription()));
                    htmlContent.append(String.format("<font color='gray' size='4'>下载地址 ：</font><a href='%s'>%s</a><br><br>", btLink.getUrl(), btLink.getUrl()));
                }
                htmlContent.append("<br>");
            }
        }
        System.out.println(subject);
        System.out.println(htmlContent.toString());
        SimpleMailSender.send(emailAddress, subject, htmlContent.toString());
    }
}
