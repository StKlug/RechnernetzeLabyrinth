//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.06.06 at 03:58:38 PM CEST 
//


package jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for treasureType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="treasureType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Start01"/>
 *     &lt;enumeration value="Start02"/>
 *     &lt;enumeration value="Start03"/>
 *     &lt;enumeration value="Start04"/>
 *     &lt;enumeration value="sym01"/>
 *     &lt;enumeration value="sym02"/>
 *     &lt;enumeration value="sym03"/>
 *     &lt;enumeration value="sym04"/>
 *     &lt;enumeration value="sym05"/>
 *     &lt;enumeration value="sym06"/>
 *     &lt;enumeration value="sym07"/>
 *     &lt;enumeration value="sym08"/>
 *     &lt;enumeration value="sym09"/>
 *     &lt;enumeration value="sym10"/>
 *     &lt;enumeration value="sym11"/>
 *     &lt;enumeration value="sym12"/>
 *     &lt;enumeration value="sym13"/>
 *     &lt;enumeration value="sym14"/>
 *     &lt;enumeration value="sym15"/>
 *     &lt;enumeration value="sym16"/>
 *     &lt;enumeration value="sym17"/>
 *     &lt;enumeration value="sym18"/>
 *     &lt;enumeration value="sym19"/>
 *     &lt;enumeration value="sym20"/>
 *     &lt;enumeration value="sym21"/>
 *     &lt;enumeration value="sym22"/>
 *     &lt;enumeration value="sym23"/>
 *     &lt;enumeration value="sym24"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "treasureType")
@XmlEnum
public enum TreasureType {

    @XmlEnumValue("Start01")
    START_01("Start01"), //$NON-NLS-1$
    @XmlEnumValue("Start02")
    START_02("Start02"), //$NON-NLS-1$
    @XmlEnumValue("Start03")
    START_03("Start03"), //$NON-NLS-1$
    @XmlEnumValue("Start04")
    START_04("Start04"), //$NON-NLS-1$
    @XmlEnumValue("sym01")
    SYM_01("sym01"), //$NON-NLS-1$
    @XmlEnumValue("sym02")
    SYM_02("sym02"), //$NON-NLS-1$
    @XmlEnumValue("sym03")
    SYM_03("sym03"), //$NON-NLS-1$
    @XmlEnumValue("sym04")
    SYM_04("sym04"), //$NON-NLS-1$
    @XmlEnumValue("sym05")
    SYM_05("sym05"), //$NON-NLS-1$
    @XmlEnumValue("sym06")
    SYM_06("sym06"), //$NON-NLS-1$
    @XmlEnumValue("sym07")
    SYM_07("sym07"), //$NON-NLS-1$
    @XmlEnumValue("sym08")
    SYM_08("sym08"), //$NON-NLS-1$
    @XmlEnumValue("sym09")
    SYM_09("sym09"), //$NON-NLS-1$
    @XmlEnumValue("sym10")
    SYM_10("sym10"), //$NON-NLS-1$
    @XmlEnumValue("sym11")
    SYM_11("sym11"), //$NON-NLS-1$
    @XmlEnumValue("sym12")
    SYM_12("sym12"), //$NON-NLS-1$
    @XmlEnumValue("sym13")
    SYM_13("sym13"), //$NON-NLS-1$
    @XmlEnumValue("sym14")
    SYM_14("sym14"), //$NON-NLS-1$
    @XmlEnumValue("sym15")
    SYM_15("sym15"), //$NON-NLS-1$
    @XmlEnumValue("sym16")
    SYM_16("sym16"), //$NON-NLS-1$
    @XmlEnumValue("sym17")
    SYM_17("sym17"), //$NON-NLS-1$
    @XmlEnumValue("sym18")
    SYM_18("sym18"), //$NON-NLS-1$
    @XmlEnumValue("sym19")
    SYM_19("sym19"), //$NON-NLS-1$
    @XmlEnumValue("sym20")
    SYM_20("sym20"), //$NON-NLS-1$
    @XmlEnumValue("sym21")
    SYM_21("sym21"), //$NON-NLS-1$
    @XmlEnumValue("sym22")
    SYM_22("sym22"), //$NON-NLS-1$
    @XmlEnumValue("sym23")
    SYM_23("sym23"), //$NON-NLS-1$
    @XmlEnumValue("sym24")
    SYM_24("sym24"); //$NON-NLS-1$
    private final String value;

    TreasureType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TreasureType fromValue(String v) {
        for (TreasureType c: TreasureType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
