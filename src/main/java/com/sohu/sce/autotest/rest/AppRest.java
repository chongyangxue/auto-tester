package com.sohu.sce.autotest.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

import com.sohu.sce.autotest.services.HttpService;
import com.sohu.sce.autotest.services.HttpService.HttpResult;
import com.sohu.sce.autotest.utils.Constants;

@Controller
public class AppRest{
	@Autowired
	private HttpService httpService;
	
	private Map<String, Object> getSuccessResult() {
		Map<String, Object> resultMap = new HashMap<String, Object>(2);
		resultMap.put("status", Constants.STATUS_CODE.SC_OK);
		resultMap.put("messages", "ok");
		resultMap.put("version", 1);
		return resultMap;
	}

	private Map<String, Object> getFailResult(String errorMsg, int errorCode) {
		Map<String, Object> resultMap = new HashMap<String, Object>(2);
		resultMap.put("status", errorCode);
		resultMap.put("messages", errorMsg);
		resultMap.put("version", 1);
		return resultMap;
	}
	
	@RequestMapping("/")
	public @ResponseBody Map<String, Object> index(){
		return getSuccessResult();
	}
	
	@RequestMapping(value= "/check_public", method = {RequestMethod.GET})
	public @ResponseBody Map<String, Object> checkAccessPublic() {
		Map<String, Object> map = getSuccessResult();
		HttpResult result = httpService.syncHttpGet("http://www.baidu.com", null, null, 1000);
		if(result.getCode() == HttpStatus.SC_OK){
			return map;
		}else{
			return getFailResult("Access public failed", result.getCode());
		}
	}
	
	@RequestMapping(value = "/check_memcache", method = {RequestMethod.POST})
	public @ResponseBody Map<String, Object> testMemcache(@RequestParam String url, 
			@RequestParam String uid, @RequestParam String password) {

		HttpResult result = httpService.syncHttpGet(url, null, null, 12000);
		if(result.getCode() != HttpStatus.SC_OK){
			return getFailResult("Get address failed", result.getCode());
		}
		List<JSONObject> list = new ArrayList<JSONObject>();
		StringBuffer sb = new StringBuffer();
		JSONObject addrObj = JSONObject.fromObject(result.getResult());
		JSONArray array = addrObj.getJSONArray("nodes");
		for(int i = 0; i < array.size(); i++){
			String ip = array.getJSONObject(i).getString("ip");
			String port = array.getJSONObject(i).getString("port");
			list.add(array.getJSONObject(i));
			sb.append(ip + ":" + port + " ");
		}
		String addr = sb.deleteCharAt(sb.length() - 1).toString();
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses(addr));
		for(JSONObject obj : list){
			builder.addAuthInfo(
					AddrUtil.getOneAddress(obj.get("ip") + ":"
							+ obj.get("port")), AuthInfo.plain(uid, password));
		}
		builder.setSessionLocator(new KetamaMemcachedSessionLocator());
		builder.setCommandFactory(new BinaryCommandFactory());
		try{
			MemcachedClient client = builder.build();
			client.set("autotest_memcache", 60, "CloudScape");
			String value = client.get("autotest_memcache");
			if(value.equals("CloudScape")){
				return getSuccessResult();
			}else{
				return getFailResult("Memcache set/get failed", 801);
			}
		}catch (Exception e){
			e.printStackTrace();
			return getFailResult(e.getMessage(), 802);
		}
	}
	
	@RequestMapping(value = "/check_redis", method = {RequestMethod.POST})
	public @ResponseBody Map<String, Object> checkRedis(String url, String password){
		HttpService.HttpResult result = httpService.syncHttpGet(url, null, null, 12000);
		if (result.getCode() != HttpStatus.SC_OK) {
			System.err.println("app: get redis nodes failed, " + result.getCode());
		}
		JSONObject master = new JSONObject();
		JSONObject obj = JSONObject.fromObject(result.getResult());
		JSONArray array = obj.getJSONArray("nodes");
		if(array.size() < 1){
			return getFailResult("Redis nodes is empty!", 801);
		}
		for(int i = 0; i < array.size(); i++){
			JSONObject node = array.getJSONObject(i);
			if(node.getInt("master") == 1){
				master = array.getJSONObject(i);
			}
		}
		String value = null;
		try{
			Jedis jedis = new Jedis(master.getString("ip"), master.getInt("port"));
			jedis.auth(password);
			jedis.setex("autotest_redis", 60, "CloudScape");
			for(int i = 0; i < array.size(); i++){
				JSONObject node = array.getJSONObject(i);
				Jedis slaveJedis = new Jedis(node.getString("ip"), node.getInt("port"));
				slaveJedis.auth(password);
				value = slaveJedis.get("autotest_redis");
				if(!StringUtils.isEmpty("value")){
					break;
				}
			}
		}catch(Exception e){
			return getFailResult(e.getMessage(), 802);
		}
		if(!StringUtils.isEmpty("value") && value.equals("CloudScape"))
			return getSuccessResult();
		else{
			return getFailResult("Redis set/get failed", 801);
		}
	}
}
