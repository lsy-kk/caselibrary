package com.lsykk.caselibrary.service;

import com.alibaba.fastjson.JSONObject;
import com.lsykk.caselibrary.vo.NoticeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/notice/{userId}")
@Component
@Slf4j
public class NoticeWebSocket {

    // sessionId -> session的映射，即每个对话id对应一个对话
    public static Map<String, Session> clients = new ConcurrentHashMap<>();

    // userId -> sessionIds的映射，即每个用户对应若干对话
    public static Map<String, Set<String>> conns = new ConcurrentHashMap<>();

    private String sid = null;

    private String userId;


    /**
     * 连接成功后调用的方法
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        // 随机生成sid
        this.sid = UUID.randomUUID().toString();
        this.userId = userId;
        clients.put(this.sid, session);
        Set<String> clientSet = conns.get(userId);
        if (clientSet == null){
            clientSet = new HashSet<>();
            conns.put(userId, clientSet);
        }
        clientSet.add(this.sid);
        log.info("sid: " + this.sid + ", 连接开启！");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        log.info("sid: " + this.sid + "连接断开！");
        clients.remove(this.sid);
    }

    /**
     * 判断是否连接的方法
     * @return
     */
    public static boolean isServerClose() {
        // 所有连接都关闭了
        if (NoticeWebSocket.clients.values().size() == 0) {
            log.info("已断开");
            return true;
        }
        else {
            log.info("已连接");
            return false;
        }
    }

    /**
     * 发送给（在线的）所有用户
     * @param noticeVo
     */
    public static void sendMessage(NoticeVo noticeVo){
        String message = JSONObject.toJSONString(noticeVo);
        for (Session sess : NoticeWebSocket.clients.values()) {
            try {
                sess.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送通知给userId对应的用户
     * @param userId
     * @param noticeVo
     */
    public static void sendMessageByUserId(String userId, NoticeVo noticeVo) {
        if (StringUtils.isBlank(userId)){
            return;
        }
        String message = JSONObject.toJSONString(noticeVo);
        Set<String> clientSet = conns.get(userId);
        if (clientSet != null) {
            // 遍历set, 每一台设备（该用户参与的每一个对话）都发送
            for (String sid : clientSet) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        session.getBasicRemote().sendText(message);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        //log.info("收到来自用户 "+this.userId+"的信息: "+message);
    }

    /**
     * 发生错误时的回调函数
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        log.info("发生错误");
        error.printStackTrace();
    }

}
