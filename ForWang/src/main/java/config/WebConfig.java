package config;

import java.lang.reflect.Method;

import net.sf.ehcache.config.CacheConfiguration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebMvc
@Configuration
@ComponentScan({"controller","test","bean","dao"})
public class WebConfig extends WebMvcConfigurerAdapter{
	
	@Bean
	public ViewResolver viewResolver()
	{
		InternalResourceViewResolver resolver=new InternalResourceViewResolver();
		resolver.setPrefix("WEB-INF/views/");
		resolver.setOrder(2);
		resolver.setSuffix(".jsp");
		return resolver;
	}
	@Bean
	public ObjectMapper getObjectMapper(){
		return new ObjectMapper();
	}
	@Bean(destroyMethod="shutdown")
	public net.sf.ehcache.CacheManager ehCacheManager() {
		CacheConfiguration cacheConfiguration=new CacheConfiguration();
		cacheConfiguration.setName("conditions");
		cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
		cacheConfiguration.setMaxEntriesLocalHeap(500);
		cacheConfiguration.setTimeToLiveSeconds(3600);

		net.sf.ehcache.config.Configuration configuration=new net.sf.ehcache.config.Configuration();
		configuration.addCache(cacheConfiguration);
		
		return net.sf.ehcache.CacheManager.newInstance(configuration);
	}
	@Bean
	public CacheManager cacheManager(){
		
		return new EhCacheCacheManager(ehCacheManager());
		
	}
	
	@Bean(name="keyGenerator")
	public KeyGenerator keyGenerator(){
		return new KeyGenerator() {
			
			
			public Object generate(Object o, Method method, Object... params) {
				// TODO Auto-generated method stub
				StringBuilder sb = new StringBuilder();
		        sb.append(o.getClass().getName());
		        sb.append(method.getName());
		        
	        	if(o.getClass().getName().equals("com.financial.dao.BenefitAnalysisDao")){
	        		for (Object param : params) {
		        		for(Object temp:(String[])param)
		        			sb.append(temp.toString());
	        		}
	        	}else{
	        		for (Object param : params) 
	        			sb.append(param.toString());
	        	}
	        	
		        System.out.println(sb.toString());
		        return sb.toString();
			}
		};
		
	}
}
