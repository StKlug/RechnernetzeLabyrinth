//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.06.06 at 03:58:38 PM CEST 
//


package jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ErrorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOERROR"/>
 *     &lt;enumeration value="ERROR"/>
 *     &lt;enumeration value="AWAIT_LOGIN"/>
 *     &lt;enumeration value="AWAIT_MOVE"/>
 *     &lt;enumeration value="ILLEGAL_MOVE"/>
 *     &lt;enumeration value="TIMEOUT"/>
 *     &lt;enumeration value="TOO_MANY_TRIES"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ErrorType")
@XmlEnum
public enum ErrorType {

    NOERROR,
    ERROR,
    AWAIT_LOGIN,
    AWAIT_MOVE,
    ILLEGAL_MOVE,
    TIMEOUT,
    TOO_MANY_TRIES;

    public String value() {
        return name();
    }

    public static ErrorType fromValue(String v) {
        return valueOf(v);
    }

}
