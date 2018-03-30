package com.volcanopuzzle;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import com.badlogic.gdx.ApplicationAdapter;
import com.volcanopuzzle.vcore.VMain;

public class VolcanoPuzzle extends ApplicationAdapter {
	
//	public VStage stage = new VStage();
	public VMain main = new VMain();

	
	@Override
	public void create () {
		
		/**
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		String filename = "./logs/"+year+""+(month+1)+""+day+""+hour+""+minute+""+second+".log";			
		try{
			System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(filename, true)), true));
		}catch(FileNotFoundException e){	}		   
		System.out.printf("===========VolcanoPuzzle %4d/%02d/%02d %02d:%02d:%02d==========\n", year, month+1, day, hour, minute, second);
		/**/
		
		main.create();
	}

	@Override
	public void render () {
		main.render();
	}
	
	@Override
	public void dispose () {

	}
}
