package org.example.chatflow.common.constants;

import org.apache.commons.lang3.StringUtils;

import javax.naming.ldap.PagedResultsControl;

/**
 * @author by zzr
 */
public class OssConstant {
    public static final String BASE_URL = "https://chat-flow.oss-cn-guangzhou.aliyuncs.com";

    public static final String DEFAULT_AVATAR = "/default-avatar/default.jpg";

    public static String buildFullUrl(String url) {
        return OssConstant.BASE_URL + url;
    }
}
