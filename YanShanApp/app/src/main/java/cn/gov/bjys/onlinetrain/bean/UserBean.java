package cn.gov.bjys.onlinetrain.bean;

/**
 * Created by dodo on 2018/3/19.
 */

public class UserBean {

    private String icon;
    private long id;
    private String realName;//
    private String userName;//
    private long beanCount;//安全豆
    private String nickname;//

    public long getBeanCount() {
        return beanCount;
    }

    public void setBeanCount(long beanCount) {
        this.beanCount = beanCount;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static class UserParentBean {
        UserBean user;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }
    }
}
