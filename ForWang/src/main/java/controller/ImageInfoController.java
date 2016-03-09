package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bean.ImageInfo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import dao.ImageInfoDao;

@Controller
public class ImageInfoController {
	
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	ImageInfoDao dao;
	
	@RequestMapping(path="checkImageInfo",method=RequestMethod.GET)
	public void getImageInfo(@RequestParam String url,HttpServletResponse response) throws IOException{
		System.out.println(url);
		String url1=url.split("/")[1];
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		JsonGenerator jgen=null;
		try{
			jgen=objectMapper.getFactory().createGenerator(response.getOutputStream());
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		List<ImageInfo> infos=dao.getImageInfo(url1);
		jgen.writeStartObject();
		for(ImageInfo info:infos)
		{
			jgen.writeStringField("id", info.getId().toString());
			jgen.writeStringField("url", info.getUrl());
			jgen.writeStringField("title",info.getTitle());
			SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");
			String timeString=format.format(info.getTake_time());
			jgen.writeStringField("take_time",timeString);
			jgen.writeStringField("location", info.getLocation());
			jgen.writeStringField("content", info.getContent());
		}
		jgen.writeEndObject();
		//必须加
		jgen.flush();
		jgen.close();
	}
	@RequestMapping(path="setImageInfo",method=RequestMethod.POST)
	public void setImageInfo(ImageInfo info,HttpServletResponse response) throws IOException{
		dao.setImageInfo(info);
		response.setStatus(200);
	}
}
