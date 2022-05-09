package org.zerhusen.model.primary;

import javax.persistence.*;

@Table(name = "user_authority")
public class UserAuthority {
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "authority_id")
    private Integer authorityId;

    /**
     * @return user_id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return authority_id
     */
    public Integer getAuthorityId() {
        return authorityId;
    }

    /**
     * @param authorityId
     */
    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }
}