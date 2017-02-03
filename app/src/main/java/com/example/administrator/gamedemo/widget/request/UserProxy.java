package com.example.administrator.gamedemo.widget.request;

import android.content.Context;


import com.example.administrator.gamedemo.model.Students;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserProxy {

	public static final String TAG = "UserProxy";
	
	private Context mContext;
	
	public UserProxy(Context context){
		this.mContext = context;
	}
	
	public void signUp(String userName,String password,String email){
		Students user = new Students();
		user.setUsername(userName);
		user.setPassword(password);
		user.setEmail(email);
		user.signUp(new SaveListener<Students>() {
			@Override
			public void done(Students s, BmobException e) {
				if(e==null){
					if(signUpLister != null){
						signUpLister.onSignUpSuccess();
					}else{
					}
				}else{
					if(signUpLister != null){
						signUpLister.onSignUpFailure(e.toString());
					}else{
					}
				}
			}
		});
	}
	
	public interface ISignUpListener{
		void onSignUpSuccess();
		void onSignUpFailure(String msg);
	}
	private ISignUpListener signUpLister;
	public void setOnSignUpListener(ISignUpListener signUpLister){
		this.signUpLister = signUpLister;
	}
	
	
	public Students getCurrentUser(){
		Students user = BmobUser.getCurrentUser(Students.class);
		if(user != null){
			return user;
		}else{
		}
		return null;
	}
	
	public void login(String userName,String password){
		final BmobUser user = new BmobUser();
		user.setUsername(userName);
		user.setPassword(password);
		user.login(new SaveListener<BmobUser>() {

			@Override
			public void done(BmobUser bmobUser, BmobException e) {
				if(e==null){
					if(loginListener != null){
						loginListener.onLoginSuccess();
					}else{
					}
				}else{
					if(loginListener != null){
						loginListener.onLoginFailure(e.toString());
					}else{
					}
				}
			}
		});
	}
	
	public interface ILoginListener{
		void onLoginSuccess();
		void onLoginFailure(String msg);
	}
	private ILoginListener loginListener;
	public void setOnLoginListener(ILoginListener loginListener){
		this.loginListener  = loginListener;
	}
	
	public void logout(){
		BmobUser.logOut();
	}
	
	public void update(String... args){
		Students user = getCurrentUser();
		user.setUsername(args[0]);
		user.setEmail(args[1]);
		user.setPassword(args[2]);
		user.setSex(args[3]);
		user.setMyself_speak(args[4]);
		//..
		user.update(new UpdateListener() {
			@Override
			public void done(BmobException e) {
				if(e == null){
					if(updateListener != null){
						updateListener.onUpdateSuccess();
					}else{
					}
				}else{
					if(updateListener != null){
						updateListener.onUpdateFailure(e.toString());
					}else{
					}
				}
			}
		});
	}
	
	public interface IUpdateListener{
		void onUpdateSuccess();
		void onUpdateFailure(String msg);
	}
	private IUpdateListener updateListener;
	public void setOnUpdateListener(IUpdateListener updateListener){
		this.updateListener = updateListener;
	}
	
	public void resetPassword(String email){
		BmobUser.resetPasswordByEmail(email, new UpdateListener() {
			@Override
			public void done(BmobException e) {
				if(e==null){
					if(resetPasswordListener != null){
						resetPasswordListener.onResetSuccess();
					}else{
					}
				}else{
					if(resetPasswordListener != null){
						resetPasswordListener.onResetFailure(e.toString());
					}else{
					}
				}
			}
		});
	}
	public interface IResetPasswordListener{
		void onResetSuccess();
		void onResetFailure(String msg);
	}
	private IResetPasswordListener resetPasswordListener;
	public void setOnResetPasswordListener(IResetPasswordListener resetPasswordListener){
		this.resetPasswordListener = resetPasswordListener;
	}

}
