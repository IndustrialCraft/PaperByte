package com.github.industrialcraft.paperbyte;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.industrialcraft.folder.Node;
import com.github.industrialcraft.folder.SaverLoader;
import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.netx.ClientMessage;
import com.github.industrialcraft.netx.NetXClient;
import com.github.industrialcraft.paperbyte.common.net.*;
import com.github.industrialcraft.paperbyte.common.util.NonClosingInputStreamWrapper;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class PaperByteMain extends ApplicationAdapter {
	public static float METER_TO_PIXEL = 10;
	public static BitmapFont FONT;

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private NetXClient networkClient;
	private OrthographicCamera camera;
	private HashMap<Integer,ClientEntity> clientEntities;
	private Map<Identifier, Node> entityNodes;
	private Map<Integer, Identifier> entityRegistry;
	private Map<Integer, Identifier> soundRegistry;
	private Map<Identifier, Sound> sounds;
	private Map<Integer, SoundWithID> playingSounds;
	private final CustomAPI customAPI;
	private GUI gui;
	public PaperByteMain(CustomAPI customAPI) {
		this.customAPI = customAPI;
	}
	@Override
	public void create() {
		FONT = new BitmapFont();
		//PaperByteMain.FONT.getData().setScale(0.05f);
		this.batch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
		this.clientEntities = new HashMap<>();
		this.sounds = new HashMap<>();
		this.playingSounds = new HashMap<>();
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.update();
		this.networkClient = new NetXClient("localhost", 4321, MessageRegistryCreator.createMessageRegistry());
		this.networkClient.start();
		this.gui = new GUI();
		try {
			loadNodes(new BufferedInputStream(new FileInputStream("out.zip")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void render() {
		if(networkClient.getClientChannel() == null || this.entityNodes == null)
			return;
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(ClientEntity clientEntity : clientEntities.values()){
			clientEntity.render(batch, entityNodes);
		}
		batch.end();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.setAutoShapeType(true);
		//shapeRenderer.begin();
		//shapeRenderer.end();
		this.gui.draw();
		this.playingSounds.values().removeIf(soundWithID -> !customAPI.isPlaying((int) soundWithID.id()));
		//todo: sound volume based on distance
		processNetworkMessages();
		sendInputPacket();
	}
	public void sendInputPacket(){
		float worldMouseX = 0;
		float worldMouseY = 0;
		int screenMouseX = Gdx.input.getX();
		int screenMouseY = Gdx.input.getY();
		int screenSizeX = Gdx.graphics.getWidth();
		int screenSizeY = Gdx.graphics.getHeight();
		boolean isMouse1 = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		boolean isMouse2 = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
		boolean isMouse3 = Gdx.input.isButtonPressed(Input.Buttons.MIDDLE);
		boolean[] keys = new boolean[ClientInputPacket.KEYS.length];
		for(int i = 0;i < ClientInputPacket.KEYS.length;i++){
			keys[i] = Gdx.input.isKeyPressed(ClientInputPacket.KEYS[i]);
		}
		networkClient.send(new ClientInputPacket(worldMouseX, worldMouseY, screenMouseX, screenMouseY, screenSizeX, screenSizeY, isMouse1, isMouse2, isMouse3, keys));
	}
	private ClientMessage.Visitor CLIENT_MESSAGE_VISITOR = new ClientMessage.Visitor() {
		@Override
		public void connect(NetXClient user) {

		}
		@Override
		public void disconnect(NetXClient user) {

		}
		@Override
		public void message(NetXClient user, Object msg) {
			if(msg instanceof GameDataPacket gameDataPacket){
				System.out.println("gamedata");
				PaperByteMain.this.entityRegistry = gameDataPacket.entityRegistry;
				PaperByteMain.this.soundRegistry = gameDataPacket.soundRegistry;
			}
			if(msg instanceof AddEntityPacket addEntityPacket){
				System.out.println("addentity" + entityRegistry);
				clientEntities.put(addEntityPacket.entityId, new ClientEntity(addEntityPacket.entityId, entityRegistry.get(addEntityPacket.entityType), addEntityPacket.position));
			}
			if(msg instanceof RemoveEntityPacket removeEntityPacket){
				clientEntities.remove(removeEntityPacket.entityId);
			}
			if(msg instanceof EntityAnimationPacket animationPacket){
				clientEntities.get(animationPacket.entityId).setAnimation(animationPacket.animationId, entityNodes);
			}
			if(msg instanceof ChangeWorldPacket){
				clientEntities.clear();
			}
			if(msg instanceof MoveEntitiesPacket moveEntitiesPacket){
				for(int i = 0;i < moveEntitiesPacket.entityIds.size();i++){
					ClientEntity clientEntity = clientEntities.get(moveEntitiesPacket.entityIds.get(i));
					if(clientEntity != null)
						clientEntity.position = moveEntitiesPacket.entityPositions.get(i);
				}
			}
			if(msg instanceof CameraUpdatePacket cameraUpdatePacket){
				camera.position.x = cameraUpdatePacket.x * PaperByteMain.METER_TO_PIXEL;
				camera.position.y = cameraUpdatePacket.y * PaperByteMain.METER_TO_PIXEL;
				camera.zoom = cameraUpdatePacket.zoom;
				camera.update();
				Gdx.graphics.setTitle("x: " + camera.position.x + " y: " + camera.position.y);
			}
			if(msg instanceof PlaySoundPacket playSoundPacket){
				Identifier soundId = soundRegistry.get(playSoundPacket.soundType);
				Sound sound = sounds.get(soundId);
				SoundWithID playingSound = playingSounds.get(playSoundPacket.soundId);
				if(playingSound != null){
					playingSound.sound().stop(playingSound.id());
				}
				playingSound = new SoundWithID(sound, sound.play());
				playingSounds.put(playSoundPacket.soundId, playingSound);
			}
			if(msg instanceof SetGUIPacket setGUIPacket){
				gui.fromPacket(setGUIPacket);
			}
		}
		@Override
		public void exception(NetXClient user, Throwable exception) {
			ClientMessage.Visitor.super.exception(user, exception);
		}
	};
	@SuppressWarnings("StatementWithEmptyBody")
	public void processNetworkMessages(){
		while(networkClient.visitMessage(CLIENT_MESSAGE_VISITOR));
	}

	public void loadNodes(InputStream stream) throws IOException {
		HashMap<String,Texture> textures = new HashMap<>();
		HashMap<String,String> renderData = new HashMap<>();
		ZipInputStream zip = new ZipInputStream(stream);
		this.entityNodes = new HashMap<>();
		ZipEntry entry;
		while((entry = zip.getNextEntry()) != null){
			if(entry.getName().endsWith(".png")){
				textures.put(entry.getName(), new Texture(new FileHandle(entry.getName()){
					@Override
					public InputStream read() {
						return new NonClosingInputStreamWrapper(zip);
					}
				}));
			} else if(entry.getName().endsWith(".json")){
				renderData.put(entry.getName(), new String(zip.readAllBytes()));
			} else if(entry.getName().endsWith(".wav")){
				sounds.put(Identifier.parse(entry.getName().split("/")[0].replace(".wav","")), Gdx.audio.newSound(new FileHandle(entry.getName()){
					@Override
					public InputStream read() {
						return new NonClosingInputStreamWrapper(zip);
					}
				}));
			}
		}
		for(var e : renderData.entrySet()){
			Identifier id = Identifier.parse(e.getKey().split("/")[0]);
			this.entityNodes.put(id, SaverLoader.fromJson(JsonParser.parseString(e.getValue()).getAsJsonObject(), s -> textures.get(id + "/" + s + ".png")));
		}
	}
	@Override
	public void resize(int width, int height) {
		this.camera.viewportWidth = width;
		this.camera.viewportHeight = height;
		this.camera.update();
	}
	@Override
	public void dispose() {
		networkClient.disconnect();
		batch.dispose();
		shapeRenderer.dispose();
	}
}