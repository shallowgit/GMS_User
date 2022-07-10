package gms.cuit.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Gms_Admin implements UserDetails,Serializable {

	/** 用户名 */
    private String admin_Username ;
    /** 密码 */
    private String admin_Password ;

	public Gms_Admin() {
		super();
	}

	public Gms_Admin(String admin_Username, String admin_Password) {
		super();
		this.admin_Username = admin_Username;
		this.admin_Password = admin_Password;
	}

	public String getAdmin_Username() {
		return admin_Username;
	}
	
	public void setAdmin_Username(String admin_Username) {
		this.admin_Username = admin_Username;
	}
	
	public String getAdmin_Password() {
		return admin_Password;
	}

	public void setAdmin_Password(String admin_Password) {
		this.admin_Password = admin_Password;
	}

	@Override
	public String toString() {
		return "Gms_Admin [admin_Username=" + admin_Username + ", admin_Password=" + admin_Password + "]";
	}


	//由于admin和user不在同一个表,所以不需要进行角色的获取,直接返回admin角色
	//获取当前用户对象所具有的角色信息
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
		authorityList.add(new SimpleGrantedAuthority("ROLE_admin"));
		return authorityList;
	}

	//获取当前用户对象密码
	@Override
	public String getPassword() {
		return admin_Password;
	}

	//获取当前用户对象用户名
	@Override
	public String getUsername() {
		return admin_Username;
	}

	//当前账户是否过期
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	//当前账户是否锁定
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	//当前密码是否过期
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	//当前账户是否可用
	@Override
	public boolean isEnabled() {
		return true;
	}
}
