package lingda.tang.service;

import lingda.tang.pojo.message.req.TextMessage;
import lingda.tang.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tangld on 2015/5/19.
 */
@Service
public class WechatService {

    @Autowired
    private EventService eventService;

    public String processRequest(HttpServletRequest request) {
        String fromUserName;
        String toUserName;
        try {
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            fromUserName = requestMap.get("FromUserName");
            toUserName = requestMap.get("ToUserName");

            String msgType = requestMap.get("MsgType");

            String content = requestMap.containsKey("Content") ? requestMap.get("Content").trim() : "";

            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                String eventType = requestMap.get("Event");
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    String respContent = "您好，欢迎来到灵达电影爬爬看！\n回复影片名字和邮箱地址，下载链接马上到~\n" +
                            "格式：\n侏罗纪>john@163.com";
                    TextMessage textMessage = new TextMessage();
                    textMessage.setToUserName(fromUserName);
                    textMessage.setFromUserName(toUserName);
                    textMessage.setCreateTime(new Date().getTime());
                    textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    textMessage.setContent(respContent);
                    return MessageUtil.messageToXml(textMessage);
                } else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                }
            } else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                String[] strings = content.split(">");
                if (strings.length != 2) {
                    String respContent = "请回复您想要看的影片名字和您的邮箱~~\n格式：\n侏罗纪>john@163.com";
                    TextMessage textMessage = new TextMessage();
                    textMessage.setToUserName(fromUserName);
                    textMessage.setFromUserName(toUserName);
                    textMessage.setCreateTime(new Date().getTime());
                    textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    textMessage.setContent(respContent);
                    return MessageUtil.messageToXml(textMessage);
                } else {
                    String name = strings[0];
                    String emailAddress = strings[1];
                    if ("".equals(name.trim())) {
                        String respContent = "请输入非空的关键字";
                        TextMessage textMessage = new TextMessage();
                        textMessage.setToUserName(fromUserName);
                        textMessage.setFromUserName(toUserName);
                        textMessage.setCreateTime(new Date().getTime());
                        textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        textMessage.setContent(respContent);
                        return MessageUtil.messageToXml(textMessage);
                    } else if (!isEmailFormat(emailAddress)) {
                        String respContent = "请输入合法的邮箱地址";
                        TextMessage textMessage = new TextMessage();
                        textMessage.setToUserName(fromUserName);
                        textMessage.setFromUserName(toUserName);
                        textMessage.setCreateTime(new Date().getTime());
                        textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        textMessage.setContent(respContent);
                        return MessageUtil.messageToXml(textMessage);
                    } else {
                        return eventService.sendDownloadLinkMailToUsers(name, emailAddress, fromUserName, toUserName);
                    }
                }
            }

            String respContent = "请回复您想要看的影片名字和您的邮箱~~\n格式：\n侏罗纪>john@163.com";
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setContent(respContent);
            return MessageUtil.messageToXml(textMessage);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private boolean isEmailFormat(String email) {
        boolean tag = true;
        final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }
}
