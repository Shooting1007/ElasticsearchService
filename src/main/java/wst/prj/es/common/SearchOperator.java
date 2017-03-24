package wst.prj.es.common;

/**
 * Created by shuting.wu on 2017/3/20.
 */
public enum SearchOperator {
    /**
     * MUST
     */
    AND {
        @Override
        public String toString() {
            return super.toString();
        }
    },
    /**
     * MUST_NOT
     */
    NOT {
        @Override
        public String toString() {
            return super.toString();
        }
    },
    /**
     * SHOULD
     */
    OR {
        @Override
        public String toString() {
            return super.toString();
        }
    }
}
