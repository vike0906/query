package com.vike.query.pojo;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author: lsl
 * @createDate: 2019/10/22
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "xml")
@Data
public class WxMessage {

    @XmlElement(name = "ToUserName")
    private String toUserName;
    @XmlElement(name = "FromUserName")
    private String fromUserName;
    @XmlElement(name = "CreateTime")
    private String createTime;
    @XmlElement(name = "MsgType")
    private String msgType;
    @XmlElement(name = "Content")
    private String content;
    @XmlElement(name = "Event")
    private String event;
    @XmlElement(name = "MsgId")
    private String msgId;

}
