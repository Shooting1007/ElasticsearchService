package com.qishon.es.enums;

/**
 * Created by shuting.wu on 2017/5/12.
 */
public enum SortType {
    /**
     * 按照字段值升序降序排序
     */
    FIELD {
        @Override
        public String toString() {
            return super.toString();
        }
    },
    /**
     * 使用脚本自定义排序
     */
    SCRIPT {
        @Override
        public String toString() {
            return super.toString();
        }
    }
}
