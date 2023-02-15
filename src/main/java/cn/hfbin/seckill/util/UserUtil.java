package cn.hfbin.seckill.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hfbin.seckill.entity.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class UserUtil {
	
	private static void createUser(int count) throws Exception{
		List<User> users = new ArrayList<User>(count);
		//生成用户
		for(int i=0;i<count;i++) {
			User user = new User();
			//user.setId((int)10000000000L+i);
			user.setLoginCount(1);
			user.setUserName("user"+i);
			user.setRegisterDate(new Date());
			user.setPhone((18077200000L+i)+"");
			user.setLastLoginDate(new Date());
			user.setSalt("9d5b364d");
			user.setHead("");
			user.setPassword(MD5Util.inputPassToDbPass("123456", user.getSalt()));
			users.add(user);
		}
//		System.out.println("create user");
//		//插入数据库
//		Connection conn = DBUtil.getConn();
//		String sql = "INSERT INTO `seckill`.`user` (`user_name`, `phone`, `password`, `salt`, `head`, `login_count`," +
//				" `register_date`, `last_login_date`)values(?,?,?,?,?,?,?,?)";
//		PreparedStatement pstmt = conn.prepareStatement(sql);
//		for(int i=0;i<users.size();i++) {
//			User user = users.get(i);
//			//pstmt.setLong(1, user.getId());
//			pstmt.setString(1, user.getUserName());
//			pstmt.setString(2, user.getPhone());
//			pstmt.setString(3, user.getPassword());
//			pstmt.setString(4, user.getSalt());
//			pstmt.setString(5, user.getHead());
//			pstmt.setInt(6, user.getLoginCount());
//			pstmt.setTimestamp(7, new Timestamp(user.getRegisterDate().getTime()));
//			pstmt.setTimestamp(8, new Timestamp(user.getRegisterDate().getTime()));
//			pstmt.addBatch();
//		}
//		pstmt.executeBatch();
//		pstmt.close();
//		conn.close();
//		System.out.println("insert to db");
		//登录，生成token
		String urlString = "http://localhost:8888/user/login";
		File file = new File("D:/tokens.txt");
		if(file.exists()) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for(int i=0;i<users.size();i++) {
			User user = users.get(i);

			byte buff[] = new byte[1024];
			int len = 0;
			System.out.println("user: " + user.getPhone() );
			// 第二种请求获取数据
			Document document = null;
			try {
				document = Jsoup.connect(urlString).data("mobile",user.getPhone(),"password",MD5Util.inputPassToFormPass("123456")).ignoreContentType(true).post();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			Pattern pattern = Pattern.compile("(\\{\"code).*?(true})",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(document.html());
			System.out.println(document.html());
			matcher.find();
			JSONObject jo = JSON.parseObject(matcher.group());

			String token = jo.getString("data");
			System.out.println("create token : " + user.getId());

			String row = user.getId()+","+token;
			raf.seek(raf.length());
			raf.write(row.getBytes());
			raf.write("\r\n".getBytes());
			System.out.println("write to file : " + user.getId());
		}
		raf.close();
		
		System.out.println("over");
	}
	
	public static void main(String[] args)throws Exception {
		createUser(5000);
	}
}
