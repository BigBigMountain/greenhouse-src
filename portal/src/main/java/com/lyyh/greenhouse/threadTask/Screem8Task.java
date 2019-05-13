package com.lyyh.greenhouse.threadTask;

import java.util.List;

import com.lyyh.greenhouse.dao.HouseDataDao;
import com.lyyh.greenhouse.dao.Screem8Dao;
import com.lyyh.greenhouse.pojo.HouseData;
import com.lyyh.greenhouse.pojo.HouseSetting;
import com.lyyh.greenhouse.pojo.Screem8;
import com.lyyh.greenhouse.util.constant.Screem8Constant;
import com.lyyh.greenhouse.util.serial.SerialTool;
import com.lyyh.greenhouse.util.serial.SerialUtil;
import com.lyyh.greenhouse.util.serial.serialException.SendDataToSerialPortFailure;
import com.lyyh.greenhouse.util.serial.serialException.SerialPortOutputStreamCloseFailure;

import gnu.io.SerialPort;

public class Screem8Task implements Runnable {

	private List<HouseSetting> houseSettings;

	private HouseDataDao houseDataDao;

	private String zoneName;
	
	private Screem8Dao screem8Dao;

	public Screem8Task(List<HouseSetting> houseSettings, HouseDataDao houseDataDao,Screem8Dao screem8Dao, String zoneName) {
		super();
		this.houseSettings = houseSettings;
		this.houseDataDao = houseDataDao;
		this.screem8Dao = screem8Dao;
		this.zoneName = zoneName;
	}

	@Override
	public void run() {
		for (;;) {
			List<HouseData> allNewestByZoneName = houseDataDao.getAllNewestByZoneName(zoneName);
			if (null != allNewestByZoneName && allNewestByZoneName.size() > 0) {
				for (HouseData houseData : allNewestByZoneName) {
					try {
					byte[] bytes = SerialUtil.houseDateConvert(houseData);
					
					SerialPort currentPort = Screem8Constant.getCurrentPort(zoneName);
					if (null == currentPort) {
						Screem8 screem8 = screem8Dao.selectByZoneName(zoneName);
						if(screem8 == null || screem8.getCurrentPortName() == null){
							System.out.println(">>>>>>>>>>>>>>>>>8字屏未配置参数<<<<<<<<<<<<<<<");
							Thread.currentThread().stop();
						}
						currentPort = SerialTool.openPort(screem8.getCurrentPortName(), screem8.getBaudRate());
						Screem8Constant.setCurrentPort(zoneName, currentPort);
					}
					SerialTool.sendToPort(currentPort, bytes);
					System.out.print("发送的数据:");
					for (byte b : bytes) {
						System.out.print(b + " ");
					}
					System.out.println();
					Screem8 setting = Screem8Constant.getSetting(zoneName);
					int intervalTime;
					if(setting == null || setting.getIntervalTime() == null){
						Screem8 screem8 = screem8Dao.selectByZoneName(zoneName);
						Screem8Constant.setSetting(zoneName, screem8);
						intervalTime = screem8.getIntervalTime() == null ? 1 : screem8.getIntervalTime();
					}else {
						intervalTime = setting.getIntervalTime();
					}
					Thread.sleep(intervalTime * 1000);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			}
		}
	}

}
