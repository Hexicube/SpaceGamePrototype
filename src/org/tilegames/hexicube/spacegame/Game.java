package org.tilegames.hexicube.spacegame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.JointEdge;
import org.jbox2d.dynamics.joints.WeldJointDef;
import org.tilegames.hexicube.spacegame.bodypart.data.BodyData;
import org.tilegames.hexicube.spacegame.bodypart.data.ControlledBodyData;
import org.tilegames.hexicube.spacegame.bodypart.data.JointHealth;
import org.tilegames.hexicube.spacegame.bodypart.data.LivingBodyData;
import org.tilegames.hexicube.spacegame.bodypart.data.PartScript;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Graphics.DisplayMode;

import sun.org.mozilla.javascript.internal.NativeArray;

public class Game implements ApplicationListener, InputProcessor {
	public static final String gameName = "Space Game";
	public static final String versionText = "Alpha 1.0";
	
	public static ArrayList<SuitableResolution> resolutions;
	
	public static boolean fullScreen;
	public static int[] currentRes;
	
	public static File optionsFile;
	public static int masterVolume, musicVolume, fxVolume;
	
	public static boolean gameActive;
	
	private static float currentDeltaPassed;
	
	public static int[] mousePos;
	
	
	public static World world;
	
	
	
	private static OrthographicCamera camera;
	private static ShapeRenderer renderer;
	
	public static SpriteBatch batch;
	
	
	
	public static ScriptEngineManager scriptEngine = new ScriptEngineManager();
	public static ScriptEngine e = scriptEngine.getEngineByName("js");
	public static ArrayList<PartScript> partScripts;
	public static ArrayList<BodyData> bodies;
	private static ArrayList<TextItem> textQueue;
	
	public static ArrayList<ScriptData> scriptData;
	
	@Override
	public void create()
	{
		org.jbox2d.common.Settings.maxPolygonVertices = 16;
		org.jbox2d.common.Settings.maxTranslation = 16;
		org.jbox2d.common.Settings.maxTranslationSquared = org.jbox2d.common.Settings.maxTranslation * org.jbox2d.common.Settings.maxTranslation;
		//System.out.println(org.jbox2d.common.Settings.maxTranslation);
		
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(new File("parts"+File.separator+"partlist.txt")));
			partScripts = new ArrayList<PartScript>();
			while(in.ready())
			{
				partScripts.add(new PartScript(in.readLine()));
			}
			in.close();
		}
		catch (FileNotFoundException e)
		{
			System.err.println("NO PART LIST!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		currentRes = new int[3];
		currentRes[2] = -999;
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		renderer = new ShapeRenderer();
		
		batch = new SpriteBatch();
		
		FontHolder.prep();
		
		masterVolume = 100;
		musicVolume = 75;
		fxVolume = 100;
		
		Gdx.input.setInputProcessor(this);
		Gdx.graphics.setVSync(true);
		
		DisplayMode[] modes = Gdx.graphics.getDisplayModes();
		DisplayMode currentMode = Gdx.graphics.getDesktopDisplayMode();
		resolutions = new ArrayList<SuitableResolution>();
		for (int a = 0; a < modes.length; a++) {
			if(modes[a].bitsPerPixel == currentMode.bitsPerPixel) {
				boolean found = false;
				int width = modes[a].width;
				int height = modes[a].height;
				for (int b = 0; b < resolutions.size(); b++) {
					SuitableResolution res = resolutions.get(b);
					if (res.width == width && res.height == height) {
						found = true;
					}
				}
				if (!found) {
					SuitableResolution res = new SuitableResolution();
					res.width = width;
					res.height = height;
					resolutions.add(res);
				}
			}
		}
		for (int a = 0; a < resolutions.size() - 1;) {
			SuitableResolution res1 = resolutions.get(a);
			SuitableResolution res2 = resolutions.get(a + 1);
			if (res1.width > res2.width
			|| (res1.width == res2.width && res1.height > res2.height)) {
				resolutions.set(a, res2);
				resolutions.set(a + 1, res1);
				if (a > 0)
					a--;
				else
					a++;
			}
			else
				a++;
		}
		for (int a = 0; a < resolutions.size(); a++) {
			SuitableResolution res = resolutions.get(a);
			System.out.println(
					String.valueOf(res.width)+
					"x"+String.valueOf(res.height));
			if(currentRes[1] == res.height &&
			   currentRes[0] == res.width)
			{
				System.out.println("\tCurrent window size");
				currentRes[2] = a;
			}
			if(currentMode.height == res.height &&
			   currentMode.width == res.width)
			{
				System.out.println("\tCurrent resolution");
			}
		}
		if(currentRes[2] == -999)
		{
			for (int a = 0; a < resolutions.size(); a++) {
				SuitableResolution res = resolutions.get(a);
				if(currentMode.height == res.height &&
				   currentMode.width == res.width)
				{
					currentRes[0] = res.width;
					currentRes[1] = res.height;
					currentRes[2] = a;
					break;
				}
			}
		}
		
		FontHolder.prep();
		
		Gdx.graphics.setTitle(gameName+" - "+versionText);
		
		currentDeltaPassed = 0;
		mousePos = new int[2];
		
		scriptData = new ArrayList<ScriptData>();
		
		world = new World(new Vec2(0, 0), true);
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		genWorldBox(-width/2+10, 0, 10, height-10);
		genWorldBox(width/2-10, 0, 10, height-10);
		genWorldBox(0, -height/2+10, width-10, 10);
		genWorldBox(0, height/2-10, width-10, 10);
		
		world.setContactListener(new HarmfulCollisionListener());
		
		bodies = new ArrayList<BodyData>();
		textQueue = new ArrayList<TextItem>();
		
		String script = "";
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(new File("scripts"+File.separator+"world.js")));
			while(in.ready()) script += in.readLine()+"\n";
			in.close();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e)
		{
			e.printStackTrace();
			script = "";
		}
		try
		{
			e.eval(script);
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void pause() {
	}
	
	@Override
	public void render() {
		currentDeltaPassed += Gdx.graphics.getDeltaTime();
		while(currentDeltaPassed >= .0166666667)
		{
			currentDeltaPassed -= .0166666667;
			
			textQueue.clear();
			
			int size = bodies.size();
			for(int a = 0; a < size; a++)
			{
				BodyData data = bodies.get(a);
				if(data instanceof ControlledBodyData)
				{
					ControlledBodyData data2 = (ControlledBodyData)data;
					data2.resetTick();
					if(data2.health <= 0) continue;
					data2.startTick();
					data2.endTick();
				}
			}
			
			world.step(.0166666667f, 10, 10);
			
			for(Body body = world.getBodyList(); body != null; body = body.getNext())
			{
				if(body.getUserData() instanceof LivingBodyData &&
				   ((LivingBodyData)body.getUserData()).health <= 0)
				{
					LivingBodyData livingData = (LivingBodyData)body.getUserData();
					if(livingData.killOnDeath)
					{
						world.destroyBody(body);
						if(livingData instanceof ControlledBodyData)
						{
							ControlledBodyData data = (ControlledBodyData)livingData;
							if(data.scripts != null) scriptData.remove(data.scripts.data);
							data.scripts = null;
						}
						bodies.remove(livingData);
						continue;
					}
					Vec2 pos = body.getPosition();
					Color col = livingData.colour;
					for(Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture.getNext())
					{
						BodyDef def = new BodyDef();
						def.type = BodyType.DYNAMIC;
						def.linearVelocity = body.getLinearVelocity().clone();
						Body body2 = Game.world.createBody(def);
						FixtureDef fix = new FixtureDef();
						fix.shape = fixture.getShape();
						fix.density = fixture.getDensity();
						fix.friction = fixture.getFriction();
						body2.createFixture(fix);
						body2.setTransform(pos.clone(), body.getAngle());
						BodyData newData = new BodyData(col, livingData.heatRate);
						newData.heat = livingData.heat;
						body2.setUserData(newData);
					}
					for(JointEdge j = body.getJointList(); j != null; j = j.next)
					{
						if(j.joint.getUserData() != null && j.joint.getUserData() instanceof JointHealth)
							((JointHealth)j.joint.getUserData()).health = 0;
						world.destroyJoint(j.joint);
					}
					bodies.remove(body.getUserData());
					body.setUserData(null);
					world.destroyBody(body);
				}
				for(JointEdge j = body.getJointList(); j != null; j = j.next)
				{
					if(j.joint.getUserData() != null && j.joint.getUserData() instanceof JointHealth)
					{
						if(((JointHealth)j.joint.getUserData()).health <= 0) world.destroyJoint(j.joint);
					}
				}
			}
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		//camera.position.set(ship.getPosition().x, ship.getPosition().y, 0);
		camera.update();
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Line);
		for(Body body = world.getBodyList(); body != null; body = body.getNext())
		{
			Vec2 pos = body.getPosition();
			for(Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture.getNext())
			{
				Object data = body.getUserData();
				if(data != null && data instanceof BodyData)
				{
					renderer.setColor(((BodyData)data).colour);
				}
				else renderer.setColor(1, 1, 1, 1);
				Shape shape = fixture.getShape();
				if(shape instanceof PolygonShape)
				{
					PolygonShape poly = (PolygonShape)shape;
					Vec2[] vertices = poly.getVertices();
					if(vertices.length > 1)
					{
						float rotation = body.getAngle();
						int count = poly.getVertexCount();
						for(int a = 0; a < count; a++)
						{
							Vec2 v1 = vertices[a];
							Vec2 v2;
							if(a+1 != count) v2 = vertices[a+1];
							else v2 = vertices[0];
							double dist = Math.sqrt(v1.x*v1.x+v1.y*v1.y);
							double angle = Math.atan2(v1.y, v1.x);
							angle += rotation;
							Vec2 newv1 = new Vec2((float)(dist*Math.cos(angle)+pos.x), (float)(dist*Math.sin(angle)+pos.y));
							dist = Math.sqrt(v2.x*v2.x+v2.y*v2.y);
							angle = Math.atan2(v2.y, v2.x);
							angle += rotation;
							Vec2 newv2 = new Vec2((float)(dist*Math.cos(angle)+pos.x), (float)(dist*Math.sin(angle)+pos.y));
							renderer.line(newv1.x, newv1.y, newv2.x, newv2.y);
						}
					}
				}
				else
				{
					System.err.println("SHAPE NOT POLY!");
					System.exit(0);
				}
			}
		}
		renderer.end();
		batch.begin();
		int size = textQueue.size();
		for(int a = 0; a < size; a++)
		{
			TextItem text = textQueue.get(a);
			batch.setColor(text.colour);
			FontHolder.render(batch, FontHolder.getCharList(text.text), text.x, text.y, text.doubleScale);
		}
		batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		currentRes[0] = width;
		currentRes[1] = height;
		currentRes[2] = -999;
		for (int a = 0; a < resolutions.size(); a++) {
			SuitableResolution res = resolutions.get(a);
			if(currentRes[1] == res.height &&
			   currentRes[0] == res.width)
			{
				currentRes[0] = res.width;
				currentRes[1] = res.height;
				currentRes[2] = a;
				break;
			}
		}
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		renderer = new ShapeRenderer();
	}
	
	@Override
	public void resume() {
	}
	
	@Override
	public boolean keyDown(int key) {
		int size = bodies.size();
		for(int a = 0; a < size; a++)
		{
			BodyData data = bodies.get(a);
			if(data instanceof ControlledBodyData)
			{
				ControlledBodyData data2 = (ControlledBodyData)data;
				data2.handleControl(key, true);
			}
		}
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean keyUp(int key) {
		int size = bodies.size();
		for(int a = 0; a < size; a++)
		{
			BodyData data = bodies.get(a);
			if(data instanceof ControlledBodyData)
			{
				ControlledBodyData data2 = (ControlledBodyData)data;
				data2.handleControl(key, false);
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int poniter, int button) {
		mousePos[0] = x;
		mousePos[1] = y;
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int poniter, int button) {
		mousePos[0] = x;
		mousePos[1] = y;
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		mousePos[0] = x;
		mousePos[1] = y;
		return false;
	}
	
	@Override
	public boolean touchMoved(int x, int y) {
		mousePos[0] = x;
		mousePos[1] = y;
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		/*if(amount > 0)
		{
			if(camera.zoom < 1f) camera.zoom *= 1.1;
		}
		else
		{
			if(camera.zoom > 0.25f) camera.zoom /= 1.1;
		}*/
		return false;
	}
	
	public static Texture loadImage(String name) {
		return new Texture(Gdx.files.internal("images" + File.separator + name + ".png"));
	}
	
	public static Sound loadSound(String name) {
		return Gdx.audio.newSound(Gdx.files.internal("sounds" + File.separator + name + ".mp3"));
	}
	
	public static Music loadMusic(String name) {
		return Gdx.audio.newMusic(Gdx.files.internal("sounds" + File.separator + name + ".mp3"));
	}
	
	public static BufferedReader loadTextFile(String name, String folder)
	{
		return getFileReader(loadFile(name, folder));
	}
	
	public static File loadFile(String name, String folder)
	{
		File f = new File(folder+File.separator+name);
		if(!f.exists())
		{
			System.out.println(name+" doesn't exist, creating...");
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return f;
	}
	
	public static BufferedReader getFileReader(File f)
	{
		try {
			return new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setResolution(int resID, boolean full)
	{
		if(currentRes[2] != resID || fullScreen != full)
		{
			SuitableResolution r = resolutions.get(resID);
			Gdx.graphics.setDisplayMode(r.width, r.height, full);
			currentRes[0] = r.width;
			currentRes[1] = r.height;
			currentRes[2] = resID;
			fullScreen = full;
			camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}
	
	private static void genWorldBox(float x, float y, float width, float height)
	{
		BodyDef def = new BodyDef();
		def.type = BodyType.STATIC;
		def.position.set(x, y);
		Body body = world.createBody(def);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2, height/2);
		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;
		fixture.density = 100;
		fixture.friction = 0.1f;
		body.createFixture(fixture);
	}
	
	//BEGIN DECLARING FUNCTIONS FOR SCRIPTS
	
	public static void destroyWhenDead(Body body)
	{
		if(body.getUserData() instanceof LivingBodyData)
		{
			((LivingBodyData)body.getUserData()).killOnDeath = true;
		}
	}
	
	public static Body fire(Body source, String bodyName, String scripts, NativeArray colour, float angle, float distance, float force, int health, double heatRate, double heat)
	{
		PartScript script = getScripts(scripts);
		Body b;
		if(script != null) b = controlledBody(bodyName, null, 0, (colour==null?((BodyData)source.getUserData()).colour:nativeArrayToColour(colour)), health, scripts, heatRate, 1, null);
		else if(health > 0) b = livingBody(bodyName, health, (colour==null?((BodyData)source.getUserData()).colour:nativeArrayToColour(colour)), heatRate, 1);
		else b = basicBody(bodyName, (colour==null?((BodyData)source.getUserData()).colour:nativeArrayToColour(colour)), heatRate);
		((BodyData)b.getUserData()).heat = heat;
		b.setLinearVelocity(source.getLinearVelocity().clone());
		Vec2 pos = source.getPosition();
		float angle2 = (float) (source.getAngle()+Math.PI/2+angle);
		setBodyPos(b, (float)(pos.x+distance*Math.cos(angle2)), (float)(pos.y+distance*Math.sin(angle2)));
		b.applyForce(new Vec2((float)(force*Math.cos(angle2)), (float)(force*Math.sin(angle2))), b.getPosition());
		source.applyForce(new Vec2((float)(-force*Math.cos(angle2)), (float)(-force*Math.sin(angle2))), source.getPosition());
		return b;
	}
	
	public static void attach(Body b1, Body b2, int hp)
	{
		WeldJointDef def = new WeldJointDef();
		def.bodyA = b1;
		def.bodyB = b2;
		def.collideConnected = false;
		def.localAnchorA.set(b2.getPosition().clone());
		def.localAnchorB.set(b1.getPosition().clone());
		def.userData = new JointHealth(hp);
		def.initialize(b1, b2, new Vec2());
		world.createJoint(def);
	}
	
	public static void requiresParent(Body child, Body parent)
	{
		if(child.getUserData() instanceof ControlledBodyData)
		{
			((ControlledBodyData)child.getUserData()).parent = parent;
		}
	}
	
	private static Body getBody(String bodyName)
	{
		String script = "";
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(new File("parts"+File.separator+bodyName+".js")));
			while(in.ready()) script += in.readLine()+"\n";
			in.close();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e)
		{
			e.printStackTrace();
			script = "";
		}
		e.put("fixtures", null);
		e.put("colour", null);
		try
		{
			e.eval(script);
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
			return null;
		}
		
		Object fric = e.get("friction");
		if(fric == null)
		{
			System.err.println("[ERROR] No friction for body: "+bodyName);
			return null;
		}
		float friction;
		if(fric instanceof Double)
			friction = ((Double)fric).floatValue();
		else if(fric instanceof Integer)
			friction = ((Integer)fric).floatValue();
		else
		{
			System.err.println("[ERROR] Friction not a number for body: "+bodyName);
			return null;
		}

		Object dens = e.get("density");
		if(dens == null)
		{
			System.err.println("[ERROR] No density for body: "+bodyName);
			return null;
		}
		float density;
		if(dens instanceof Double)
			density = ((Double)dens).floatValue();
		else if(dens instanceof Integer)
			density = ((Integer)dens).floatValue();
		else
		{
			System.err.println("[ERROR] Density not a number for body: "+bodyName);
			return null;
		}

		Object damp = e.get("dampening");
		float dampening;
		if(damp == null)
		{
			dampening = 0;
		}
		else if(damp instanceof Double)
			dampening = ((Double)damp).floatValue();
		else if(damp instanceof Integer)
			dampening = ((Integer)damp).floatValue();
		else
		{
			System.err.println("[ERROR] Dampening not a number for body: "+bodyName);
			return null;
		}
		
		Vec2[][] fixtures = null;
		Object fix = e.get("fixtures");
		if(fix == null)
		{
			System.err.println("[ERROR] No fixtures in body: "+bodyName);
			System.err.println("[ERROR] If you do not want any geometry, use \"fixtures = [];\".");
			return null;
		}
		if(!(fix instanceof NativeArray))
		{
			System.err.println("[ERROR] Fixtures are not Number[][][] in body: "+bodyName);
			return null;
		}
		try
		{
			Object[] data = nativeArrayToNormalArray((NativeArray)fix);
			fixtures = new Vec2[data.length][];
			for(int a = 0; a < data.length; a++)
			{
				Object[] data2 = (Object[])data[a];
				fixtures[a] = new Vec2[data2.length];
				for(int b = 0; b < data2.length; b++)
				{
					Object[] data3 = (Object[])data2[b];
					float x;
					if(data3[0] instanceof Double)
						x = ((Double)data3[0]).floatValue();
					else if(data3[0] instanceof Integer)
						x = ((Integer)data3[0]).floatValue();
					else
					{
						System.err.println("[ERROR] Bad thingy: "+data3[0]);
						return null;
					}
					float y;
					if(data3[0] instanceof Double)
						y = ((Double)data3[1]).floatValue();
					else if(data3[0] instanceof Integer)
						y = ((Integer)data3[1]).floatValue();
					else
					{
						System.err.println("[ERROR] Bad thingy: "+data3[1]);
						return null;
					}
					fixtures[a][b] = new Vec2(x, y);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		BodyDef def = new BodyDef();
		def.type = BodyType.DYNAMIC;
		def.angularDamping = dampening;
		Body b = world.createBody(def);
		for(int a = 0; a < fixtures.length; a++)
		{
			PolygonShape shape = new PolygonShape();
			shape.set(fixtures[a], fixtures[a].length);
			FixtureDef fixture = new FixtureDef();
			fixture.density = density;
			fixture.friction = friction;
			fixture.shape = shape;
			b.createFixture(fixture);
		}
		return b;
	}
	
	public static Body basicBody(String bodyName, NativeArray colour, double heatRate)
	{
		return basicBody(bodyName, nativeArrayToColour(colour), heatRate);
	}
	
	public static Body basicBody(String bodyName, Color colour, double heatRate)
	{
		Body b = getBody(bodyName);
		BodyData data = new BodyData(colour, heatRate);
		b.setUserData(data);
		bodies.add(data);
		return b;
	}
	
	public static Body livingBody(String bodyName, int health, NativeArray colour, double heatRate, double damageRate)
	{
		return livingBody(bodyName, health, nativeArrayToColour(colour), heatRate, damageRate);
	}
	
	public static Body livingBody(String bodyName, int health, Color colour, double heatRate, double damageRate)
	{
		Body b = getBody(bodyName);
		BodyData data = new LivingBodyData(health, colour, heatRate, damageRate);
		b.setUserData(data);
		bodies.add(data);
		return b;
	}
	
	public static Body controlledBody(String bodyName, String owner, int team, NativeArray colour, int health, String scriptName, double heatRate, double damageRate, Body cockpit)
	{
		return controlledBody(bodyName, owner, team, nativeArrayToColour(colour), health, scriptName, heatRate, damageRate, cockpit);
	}
	
	public static Body controlledBody(String bodyName, String owner, int team, NativeArray colour, int health, String scriptName, double heatRate, double damageRate, String name)
	{
		Body b = controlledBody(bodyName, owner, team, nativeArrayToColour(colour), health, scriptName, heatRate, damageRate, null);
		if(name.equalsIgnoreCase("self"))
		{
			((ControlledBodyData)b.getUserData()).cockpit = b;
			((ControlledBodyData)b.getUserData()).calcDistFromCockpit();
		}
		return b;
	}
	
	public static Body controlledBody(String bodyName, String owner, int team, Color colour, int health, String scriptName, double heatRate, double damageRate, Body cockpit)
	{
		Body b = getBody(bodyName);
		PartScript scripts = getScripts(scriptName);
		BodyData data = null;
		if(scripts == null)
		{
			if(scriptName != null) System.err.println("[WARNING] Missing scripts: "+scriptName);
		}
		else
		{
			scripts.body = b;
		}
		data = new ControlledBodyData(b, owner, team, colour, health, scripts, heatRate, damageRate, cockpit);
		((ControlledBodyData)data).calcDistFromCockpit();
		b.setUserData(data);
		bodies.add(data);
		return b;
	}
	
	public static void setBodyPos(Body body, float x, float y)
	{
		body.setTransform(new Vec2(x, y), body.getAngle());
	}
	
	public static void setBodyRotation(Body body, float rot)
	{
		body.setTransform(body.getPosition(), rot);
	}
	
	public static void setBodyPosRotation(Body body, float x, float y, float rot)
	{
		body.setTransform(new Vec2(x, y), rot);
	}
	
	public static void scriptSay(String text)
	{
		System.out.println("SCRIPT: "+text);
	}
	
	public static void print(String text, int x, int y, boolean doubleScale, NativeArray colour)
	{
		textQueue.add(new TextItem(text, x, y, doubleScale, nativeArrayToColour(colour)));
	}
	
	private static Object[] nativeArrayToNormalArray(NativeArray array)
	{
		long len = array.getLength();
		Object[] data = new Object[(int)len];
		for(int a = 0; a < len; a++)
		{
			data[a] = array.get(a, null);
			if(data[a] instanceof NativeArray) data[a] = nativeArrayToNormalArray((NativeArray)data[a]);
		}
		return data;
	}
	
	private static Color nativeArrayToColour(NativeArray array)
	{
		Object[] data = nativeArrayToNormalArray(array);
		float[] cols = new float[4];
		for(int a = 0; a < 4; a++)
		{
			if(data[0] instanceof Double) cols[a] = ((Double)data[a]).floatValue();
			else if(data[0] instanceof Integer) cols[a] = ((Integer)data[a]).floatValue();
			else cols[a] = 0;
		}
		return new Color(cols[0], cols[1], cols[2], cols[3]);
	}
	
	private static PartScript getScripts(String name)
	{
		if(name == null) return null;
		PartScript scripts = null;
		int size = partScripts.size();
		for(int a = 0; a < size; a++)
		{
			PartScript s = partScripts.get(a);
			if(s.equals(name))
			{
				scripts = s;
				break;
			}
		}
		if(scripts == null) return null;
		return scripts.copy();
	}
	
	public static void scriptSet(long UUID, String name, Object value)
	{
		int size = scriptData.size();
		for(int a = 0; a < size; a++)
		{
			ScriptData data = scriptData.get(a);
			if(data.UUID == UUID)
			{
				data.set(name, value);
				return;
			}
		}
		System.err.println("[SCRIPTSET-WARNING] Creating missing script data UUID: "+UUID);
		ScriptData data = new ScriptData(UUID);
		data.set(name, value);
		scriptData.add(data);
	}
	
	public static Object scriptGet(long UUID, String name)
	{
		int size = scriptData.size();
		for(int a = 0; a < size; a++)
		{
			ScriptData data = scriptData.get(a);
			if(data.UUID == UUID) return data.get(name);
		}
		System.err.println("[SCRIPTGET-WARNING] Missing script data UUID: "+UUID);
		return null;
	}
	
	public static Object scriptDel(long UUID, String name)
	{
		int size = scriptData.size();
		for(int a = 0; a < size; a++)
		{
			ScriptData data = scriptData.get(a);
			if(data.UUID == UUID) return data.del(name);
		}
		System.err.println("[SCRIPTDEL-WARNING] Missing script data UUID: "+UUID);
		return null;
	}
	
	public static double requestResource(String name, double amount, Body body, boolean acceptPartial)
	{
		if(body.getUserData() instanceof ControlledBodyData)
		{
			ControlledBodyData data = (ControlledBodyData)body.getUserData();
			return data.requestResource(name, amount, acceptPartial);
		}
		return 0;
	}
	
	public static double depositResource(String name, double amount, Body body, boolean acceptPartial)
	{
		if(body.getUserData() instanceof ControlledBodyData)
		{
			ControlledBodyData data = (ControlledBodyData)body.getUserData();
			return data.depositResource(name, amount, acceptPartial);
		}
		return 0;
	}
	
	public static void addResource(String name, double amount, boolean acceptPartial, Body body)
	{
		if(body.getUserData() instanceof ControlledBodyData)
		{
			ControlledBodyData data = (ControlledBodyData)body.getUserData();
			data.addResource(name, amount, acceptPartial);
		}
	}
	
	public static void removeResource(String name, double amount, Body body)
	{
		if(body.getUserData() instanceof ControlledBodyData)
		{
			ControlledBodyData data = (ControlledBodyData)body.getUserData();
			data.removeResource(name, amount);
		}
	}
}