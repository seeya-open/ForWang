package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bean.SubjectInfo;

@Controller
public class FunctionLogin {
	
	@Autowired
	ServletContext context;
	
	String jsessionid="";
	
	String username="";
	
	String base_dir="http://210.42.121.241";
	
	@RequestMapping(value="fun",method=RequestMethod.GET)
	public String getLogin(@ModelAttribute("model") ModelMap model) throws IOException{
		int httpResult=0;
		
		URL home=new URL("http://210.42.121.241");
		URLConnection homeconn=home.openConnection();
		homeconn.connect();
		String temp=homeconn.getHeaderField("Set-Cookie").split(" ")[0];
		jsessionid=temp.split(";")[0];
		
		System.out.println(jsessionid);
		
		
		URL url=new URL("http://210.42.121.241/servlet/GenImg");
		URLConnection connection=url.openConnection();
		connection.setRequestProperty("Connection", "keep-alive");
		
		//connection.setRequestProperty("Cookie", jsessionid);
		connection.connect();
		HttpURLConnection httpconn=(HttpURLConnection) connection;
		
		httpResult=httpconn.getResponseCode();
		if(httpResult==HttpURLConnection.HTTP_OK){
			for(String key:connection.getHeaderFields().keySet())
				System.out.println(key+":"+connection.getHeaderField(key));
			System.out.println("set-cookie"+":"+connection.getHeaderField("Set-Cookie"));
			jsessionid=connection.getHeaderField("Set-Cookie").split(";")[0];
			
			BufferedInputStream bis=new BufferedInputStream(connection.getInputStream());
			File file=new File(context.getRealPath("/").concat("genImg").concat(File.separator+"gen.jpg"));
			System.out.println("file: "+file.getAbsolutePath()+" : "+file.getCanonicalPath());
			BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
			byte[] buffer=new byte[1024];
			int num=-1;
			while((num=bis.read(buffer))!=-1){
				bos.write(buffer);
			}
			bos.flush();
			bos.close();
			bis.close();
			
		}
		else 
			throw new IOException("未获得验证码图片");
		
		return "function";
	}
	
	@RequestMapping(value="fun",method=RequestMethod.POST)
	public void getLogin(@RequestParam("username")String username,@RequestParam("password") String password
							,@RequestParam("xdvfb")String yanzheng,HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("utf8");
		response.setContentType("text/html");
		if(!jsessionid.equals("")){
			URL url=new URL(base_dir+"/servlet/Login");
			URLConnection connection=url.openConnection();
			HttpURLConnection httpconn=(HttpURLConnection) connection;
			httpconn.setRequestMethod("POST");
			httpconn.setRequestProperty("Connection", "keep-alive");
			httpconn.setRequestProperty("Cookie", jsessionid);
			
			//httpconn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
			System.out.println(jsessionid);
			
			httpconn.setDoOutput(true);
			StringBuffer params=new StringBuffer();
			params.append("id=").append(username).append("&").append("pwd=").append(password)
				.append("&").append("xdvfb=").append(yanzheng);
			httpconn.getOutputStream().write(params.toString().getBytes());
			//httpconn.connect();
			if(httpconn.getResponseCode()==HttpURLConnection.HTTP_OK){

				//System.out.println(httpconn.getResponseMessage());
				
				//URL stu_index=new URL(base_dir+"/stu/stu_index.jsp");
				//HttpURLConnection connection2=(HttpURLConnection) stu_index.openConnection();
				SimpleDateFormat sdf =
				        new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss 'GMT+0800 (中国标准时间)'",Locale.ENGLISH);
				Date date=new Date();
				String urlString="http://210.42.121.241/servlet/Svlt_QueryStuScore?year=0&term=&learnType=&scoreFlag=0&t="+
						URLEncoder.encode(sdf.format(date),"utf-8");
				URL url1=new URL(urlString);
				HttpURLConnection connection2=(HttpURLConnection) url1.openConnection();
				connection2.setRequestProperty("Connection", "keep-alive");
				connection2.addRequestProperty("Cookie", jsessionid);
				
				//connection2.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
				
				System.out.println(jsessionid);
				connection2.connect();
				BufferedInputStream bis=new BufferedInputStream(connection2.getInputStream());
				byte[] buffer=new byte[1024];
				String charset=connection2.getHeaderField("Content-Type").split(";")[1].split("=")[1];
				int num=-1;
				String buffer2="";
				while((num=bis.read(buffer))>0){

					//oStream.write(buffer,0,num);
					buffer2+=new String(buffer,0,num,charset);
					
					//oStream.flush();
				}
				System.out.println(buffer2);
				String regex="<td *>([^<]*?)</td>";
				Pattern pattern=Pattern.compile(regex);
	
				Matcher matcher=pattern.matcher(buffer2);
				
				ArrayList<SubjectInfo> subjectInfos=new ArrayList<SubjectInfo>();
				int i=0;
				while(matcher.find()){
					SubjectInfo info=new SubjectInfo();
					
					info.setId(matcher.group(1));
					i++;
					while(i%10!=0&&matcher.find())
					{
						String content=matcher.group(1);
						//System.out.println(content);
						switch(i%10){
						case 1:
							info.setName(content);
							break;
						case 2:
							info.setType(content);
							break;
					
						case 3:
							info.setCredit(Double.valueOf(content));
							break;
						case 4:
							info.setTeacher(content);
							break;
						case 5:
							info.setAcademy(content);
							break;
						case 6:
							info.setStudy_type(content);
							break;
						case 7:
							info.setYear(content);
							break;
						case 8:
							info.setSemester(content);
							break;
						case 9:
							if(!content.equals(""))
								info.setGrade(Double.valueOf(content));
							else
								info.setGrade(0);
							break;
						default:
								break;
						}
						i++;
					}
					subjectInfos.add(info);
				}
				bis.close();
				Writer writer=response.getWriter();
				//System.out.println("Subject");
				double zhuanye=0;
				double gonggong=0;
				double zfuture=0;
				double gfuture=0;
				System.out.println("课程数: "+subjectInfos.size());
				for(SubjectInfo info:subjectInfos)
				{
					//writer.write(info.toString());
					//System.out.println(info.getGrade());
					if(info.getType().equals("专业必修")&&(Math.abs(info.getGrade())>0.1))
					{
						zhuanye+=info.getCredit();
						
					}else if(info.getType().equals("公共必修")&&(Math.abs(info.getGrade())>0.01))
					{
						gonggong+=info.getCredit();
					}
					else if(info.getType().equals("专业必修")&&(Math.abs(info.getGrade())<0.01))
					{
						zfuture+=info.getCredit();
					}
					else if(info.getType().equals("公共必修")&&(Math.abs(info.getGrade())<0.01))
					{
						gfuture+=info.getCredit();
					}
					else {
						
						System.out.println(info);
					}
				}
				double gpa=get_GPA(subjectInfos);
				writer.write("你已修公共必修学分: "+gonggong+"\n你已修专业必修学分: "+zhuanye+"\n你在修公共必修课学分: "+gfuture+"\n你在修专业必修课学分: "+zfuture+", 你的绩点为: "+gpa);
				writer.flush();
				writer.close();
				

				
			}
			else {
				//System.out.println(httpconn.getResponseMessage());
				System.out.println(httpconn.getResponseCode());
			}
		}
		
	}
	private double get_GPA(List<SubjectInfo> infos){
		
		double total_credit=0;
		double total_score=0;
		for(SubjectInfo info:infos){
			String type=info.getType();
			double grade=info.getGrade();
			double credit=info.getCredit();
			if(grade!=0&&(type.equals("专业必修")||type.equals("公共必修")))
			{
				total_credit+=credit;
				if(grade>=90){
					total_score+=credit*4.0;
				}else if(grade>=85){
					total_score+=credit*3.7;
				}else if(grade>=82){
					total_score+=credit*3.3;
				}else if(grade>=78){
					total_score+=credit*3.0;
				}else if(grade>=75){
					total_score+=credit*2.7;
				}else if(grade>=72){
					total_score+=credit*2.3;
				}else if(grade>=68){
					total_score+=credit*2.0;
				}else if(grade>=64){
					total_score+=credit*1.5;
				}else if(grade>=60){
					total_score+=credit*1.0;
				}else{
					total_score+=credit*0;
				}
			}
		}
		if(Math.abs(total_credit)<0.001){
			return 0.0;
		}
		return total_score/total_credit;
	}
	
	@RequestMapping(value="/GenImg",method=RequestMethod.GET)
	public void genImg (HttpServletResponse response) throws IOException{
		System.out.println("genimg");
		File file=new File(context.getRealPath("/").concat("genImg").concat(File.separator+"gen.jpg"));
		BufferedInputStream bis=new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream bos=new BufferedOutputStream(response.getOutputStream());
		int num=-1;
		byte[] buffer=new byte[1024];
		while((num=bis.read(buffer))!=-1)
		{
			bos.write(buffer);
		}
		bos.flush();
		bis.close();
		bos.close();
	}
	
	@RequestMapping(value="check_lessons",method=RequestMethod.GET)
	public void Lesson_Infos(HttpServletResponse response) throws IOException{
		
		System.out.println("----Check_Lessons----");
		
		String chooseLsn="http://210.42.121.241/stu/choose_PubLsn_list.jsp";
		
		String chooseLsnByPage="http://210.42.121.241/stu/choose_PubLsn_list.jsp?XiaoQu=0&credit=0&keyword=&pageNum=%d";
		
		final Writer writer=response.getWriter();
		final List<List<String>> list=Collections.synchronizedList(new ArrayList<List<String>>());
		
		ExecutorService es=Executors.newCachedThreadPool();
		
		
		
		for(int i=0;i<27;i++){
			
			
			URL url;
			if(i==0)
				url=new URL(chooseLsn);
			else {
				String temp=String.format(chooseLsnByPage, i+1);
				url=new URL(temp);
			}
			//System.out.println(i+" : "+jsessionid);
			if(jsessionid.equals(""))
			{
				writer.write("jsessionid有问题");
				break;
			}
			final URL fUrl=url;
			final int pos=i;
			
			es.execute(new Runnable() {
				int j=0;
				public void run() {
					// TODO Auto-generated method stub
					try{
						
						URLConnection connection=fUrl.openConnection();
						
						connection.setRequestProperty("Cookie", jsessionid);
						connection.connect();
						
						BufferedInputStream bis=new BufferedInputStream(connection.getInputStream());
						byte[] buffer=new byte[1024];
						StringBuilder sb=new StringBuilder();
						int num=-1;
						String charset=connection.getHeaderField("Content-Type").split(";")[1].split("=")[1];
						while((num=bis.read(buffer))!=-1)
						{
							sb.append(new String(buffer,0,num,charset));
						}
						String regex="<td *>([\\s\\S]*?)</td>";
						Pattern pattern=Pattern.compile(regex);
						Matcher matcher=pattern.matcher(sb);
						List<String> tempList=new ArrayList<String>();
						while(matcher.find()){
							tempList.add(matcher.group(1));
							
							j++;
						}
						list.add(tempList);
					}catch(IOException ioe){
						ioe.printStackTrace();
					}
					
				}
			});
			
		}
		es.shutdown();
		boolean finished=false;
		
		try {
			finished=es.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<list.size();i++)
		{
			System.out.println(""+i+":");
			/*for(String ele:list.get(i)){
				System.out.println(ele);
			}*/
			for(int j=0;j<list.get(i).size();j++){
				System.out.println(list.get(i).get(j));
			}
		}
	}
	
	private void robot() throws IOException{
		
		SimpleDateFormat sdf =
		        new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss 'GMT+0800 (中国标准时间)'",Locale.ENGLISH);
		Date date=new Date();
		String urlString="http://210.42.121.241/servlet/Svlt_QueryStuScore?year=0&term=&learnType=&scoreFlag=0&t="+
				sdf.format(date);
		URL url=new URL(urlString);
		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		
		
 	}
}
