package com.lwj.house.web.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 *
 * @author lwj
 */
@Entity
@Table(name = "support_address")
@Getter
@Setter
public class SupportAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 上一级所属单位
     */
    @Column(name = "belong_to")
    private String belongTo;

    /**
     * 城市英文缩写
     */
    @Column(name = "en_name")
    private String enName;

    /**
     * 城市中文名称
     */
    @Column(name = "cn_name")
    private String cnName;


    private String level;

    /**
     * 行政级别定义
     */
    public enum Level{
        /**
         * 市
         */
        CITY("city"),
        /**
         * 区
         */
        REGION("region"),;

        private String value;

        Level(String value) {
            this.value = value;
        }

        public String getValue(){
            return value;
        }

        public static Level of(String value) {
            for (Level level : Level.values()) {
                if (level.getValue().equals(value)) {
                    return level;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
