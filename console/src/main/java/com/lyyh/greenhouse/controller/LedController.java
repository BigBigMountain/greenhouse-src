package com.lyyh.greenhouse.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lyyh.greenhouse.pojo.LedClimatic;
import com.lyyh.greenhouse.pojo.LedHouse;
import com.lyyh.greenhouse.pojo.LedProgram;
import com.lyyh.greenhouse.pojo.LedTable;
import com.lyyh.greenhouse.pojo.User;
import com.lyyh.greenhouse.service.LedService;

@Controller
@RequestMapping("/led")
public class LedController {
	
	@Autowired
	private LedService ledService;

	/*
	 * 去首页
	 */
	@RequestMapping("/listAll.do")
	public String listAll(Model model,HttpSession session){
		User user = (User) session.getAttribute("loginUser");
		
		List<LedProgram> ledPrograms = ledService.listAll(user.getZoneId());
		model.addAttribute("ledPrograms",ledPrograms);
		
		return "led/list";
	}
	/*
	 * 去添加页面
	 */
	@RequestMapping("/toAdd.do")
	public String toAdd(){
		return "led/add";
	}
	
	/*
	 * 添加led设置配置
	 */
	@RequestMapping(value = "/add.do",produces="text/json;charset=UTF-8")
	public @ResponseBody String add(Model model,HttpSession session,LedProgram ledProgram,LedTable ledTable,LedHouse ledHouse,LedClimatic ledClimatic){
		User user = (User) session.getAttribute("loginUser");
		ledProgram.setZ_id(user.getZoneId());
		String msg = ledService.add(ledProgram,ledTable,ledHouse,ledClimatic);
		return msg;
	}
	
	
	/*
	 * 去修改页面
	 */
	@RequestMapping("/toUpdate.do")
	public String toUpdate(Model model,Integer p_id){
		LedProgram ledProgram = ledService.findProgramByPid(p_id);
		model.addAttribute("ledProgram",ledProgram);
		
		LedTable ledTable = ledService.findLedTableByPid(p_id);
		model.addAttribute("ledTable",ledTable);
		
		LedHouse ledHouse = ledService.findLedHouseByPid(p_id);
		model.addAttribute("ledHouse",ledHouse);
		
		LedClimatic ledClimatic = ledService.findLedClimaticByPid(p_id);
		model.addAttribute("ledClimatic",ledClimatic);
		return "led/update";
	}

	/*
	 * 更新设置
	 */
	@RequestMapping(value = "/update.do",produces="text/json;charset=UTF-8")
	public @ResponseBody String update(LedProgram ledProgram,LedTable ledTable,LedHouse ledHouse,LedClimatic ledClimatic){
		String msg = ledService.update(ledProgram,ledTable,ledHouse,ledClimatic);
		return msg;
		
	}

	
//	/*
//	 * 显示led
//	 */
//	@RequestMapping(value="/showLed.do",produces="text/json;charset=UTF-8")
//	public @ResponseBody String showLed(HttpSession session,Integer p_id){
//		User user = (User)session.getAttribute("loginUser");
//		String msg = ledService.showLed(user,p_id);
//		return msg;
//	}
	
	/*
	 * 启动led
	 */
	@RequestMapping(value="/showLed.do",produces="text/json;charset=UTF-8")
	public @ResponseBody String showLed(HttpSession session,Integer p_id){
		User user = (User)session.getAttribute("loginUser");
		String msg=null;
		msg = ledService.showLed(user,p_id);
		return msg;
	}
	/*
	 * 预览led
	 */
	@RequestMapping(value="/preview.do",produces="text/json;charset=UTF-8")
	public @ResponseBody String preview(HttpSession session,LedProgram ledProgram,LedTable ledTable,LedHouse ledHouse,LedClimatic ledClimatic){
//		System.out.println(ledClimatic);
		User user = (User)session.getAttribute("loginUser");
		String zoneName = user.getZoneName();
		String msg = ledService.preview(zoneName, ledProgram, ledTable, ledHouse, ledClimatic);
		return msg;
	}
	
	/*
	 * 停止Led显示
	 * TODO
	 */
	@RequestMapping(value="/stopLed.do",produces="test/json;charset=UTF-8")
	public @ResponseBody String stopLed(){
		ledService.stopLed();
		return "已停止led显示";
	}
	/*
	 * 删除节目
	 */
	@RequestMapping(value="/del.do")
	public String del(Integer p_id){
		ledService.delProgramByPid(p_id);
		return "forward:/led/listAll.do";
	}
}
