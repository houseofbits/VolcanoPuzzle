package com.volcanopuzzle.vcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class VStaticAssets {
	//Called in VMain constructor before create()
	public static void Init(){
		Fonts.calibri25Font.getData().setLineHeight(25);
		Fonts.calibriLightFont.getData().setLineHeight(45);
		Fonts.futuraFont.getData().setLineHeight(25);
		//TODO Button on/off state images
		GUI.buttonsSkin.addRegions(new TextureAtlas("gui.txt"));
	}
	public static class Text{
		public static final String selectorTitle = "Saliec kādu no 12 minerālu attēliem. Zemāk vari izvēlēties puzles gabaliņu skaitu."; 	
		public static final String continueButtonTitle = "Turpināt";
	}
	public static class Fonts{
		public static final BitmapFont calibriLightFont = new BitmapFont(Gdx.files.internal("fonts/calibri-light-50.fnt"));
		public static final BitmapFont calibri25Font = new BitmapFont(Gdx.files.internal("fonts/calibri-25.fnt"));
		public static final BitmapFont calibri18Font = new BitmapFont(Gdx.files.internal("fonts/calibri-18.fnt"));
		public static final BitmapFont futuraFont = new BitmapFont(Gdx.files.internal("fonts/futura-25.fnt"));
	}
	public static class GUI{
		public static final Skin buttonsSkin = new Skin();
	}
}
