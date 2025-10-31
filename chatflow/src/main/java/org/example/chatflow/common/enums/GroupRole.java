package org.example.chatflow.common.enums;

import lombok.Getter;

/**
 * 群聊成员角色
 */
@Getter
public enum GroupRole {

    MEMBER(1, "普通成员"),
    ADMIN(2, "管理员"),
    OWNER(3, "群主");

    private final Integer code;
    private final String desc;

    GroupRole(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据角色编码获取枚举
     *
     * @param code 角色编码
     * @return 对应角色，未匹配返回 null
     */
    public static GroupRole fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (GroupRole role : GroupRole.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }
}

