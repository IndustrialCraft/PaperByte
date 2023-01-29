package com.github.industrialcraft.paperbyte;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.industrialcraft.folder.Node;
import com.github.industrialcraft.folder.SaverLoader;
import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.netx.ClientMessage;
import com.github.industrialcraft.netx.NetXClient;
import com.github.industrialcraft.paperbyte.common.net.*;
import com.github.industrialcraft.paperbyte.common.util.NonClosingInputStreamWrapper;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class PaperByteMain extends ApplicationAdapter implements InputProcessor {
	public static float METER_TO_PIXEL = 50;
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
	private Map<Identifier,Texture> imageTextures;
	private Map<Integer,Identifier> imagesNetId;
	private final CustomAPI customAPI;
	private GUI gui;
	private ArrayList<Integer> typedChars;
	private float scrollX;
	private float scrollY;
	private List<ServerCollisionsDebugPacket.RenderData> collisionRenderData;
	private int collisionRenderDataInvalidationTimer;
	private List<ParticleSystem> particleSystems;
	private final Random random;
	private final String host;
	private final int port;
	public PaperByteMain(CustomAPI customAPI, String host, int port) {
		this.customAPI = customAPI;
		this.host = host;
		this.port = port;
		this.random = new Random();
	}
	@Override
	public void create() {
		this.scrollX = 0;
		this.scrollY = 0;
		FONT = new BitmapFont();
		//PaperByteMain.FONT.getData().setScale(0.05f);
		this.batch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
		this.clientEntities = new HashMap<>();
		this.sounds = new HashMap<>();
		this.playingSounds = new HashMap<>();
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.update();
		this.networkClient = new NetXClient(host, port, MessageRegistryCreator.createMessageRegistry());
		this.networkClient.start();
		this.gui = new GUI(this);
		typedChars = new ArrayList<>();
		this.entityNodes = null;
		this.collisionRenderData = null;
		this.collisionRenderDataInvalidationTimer = -1;
		this.imageTextures = new HashMap<>();
		this.particleSystems = new ArrayList<>();
	}
	public Random getRandom() {
		return random;
	}
	public Map<Identifier, Texture> getImageTextures() {
		return imageTextures;
	}
	public Map<Integer, Identifier> getImagesNetId() {
		return imagesNetId;
	}
	@Override
	public void render() {
		processNetworkMessages();
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
		if(collisionRenderDataInvalidationTimer >= 0){
			collisionRenderDataInvalidationTimer--;
			if(collisionRenderDataInvalidationTimer == -1)
				collisionRenderData = null;
		}
		if(collisionRenderData != null) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.setAutoShapeType(true);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.begin();
			for(var e : collisionRenderData){
				switch (e.getType()){
					case Circle -> {
						ServerCollisionsDebugPacket.ServerCollisionRenderDataCircle circle = (ServerCollisionsDebugPacket.ServerCollisionRenderDataCircle) e;
						Vector2 vec = circle.pos().cpy();
						e.getTransform().mul(vec);
						shapeRenderer.circle(vec.x*METER_TO_PIXEL, vec.y*METER_TO_PIXEL, circle.radius()*METER_TO_PIXEL);
					}
					case Edge -> {
						Vector2[] vertices = new Vector2[2];
						ServerCollisionsDebugPacket.ServerCollisionRenderDataEdge edge = (ServerCollisionsDebugPacket.ServerCollisionRenderDataEdge) e;
						vertices[0] = edge.v1().cpy();
						vertices[1] = edge.v2().cpy();
						e.getTransform().mul(vertices[0]);
						e.getTransform().mul(vertices[1]);
						drawSolidPolygon(vertices, 2, true);
					}
					case Polygon -> {
						ServerCollisionsDebugPacket.ServerCollisionRenderDataPolygon polygon = (ServerCollisionsDebugPacket.ServerCollisionRenderDataPolygon) e;
						Vector2[] vertices = new Vector2[polygon.vertices().length];
						for(int i = 0;i < polygon.vertices().length;i++){
							vertices[i] = polygon.vertices()[i].cpy();
							e.getTransform().mul(vertices[i]);
						}
						drawSolidPolygon(vertices, vertices.length, true);
					}
					case Chain -> {
						ServerCollisionsDebugPacket.ServerCollisionRenderDataChain chain = (ServerCollisionsDebugPacket.ServerCollisionRenderDataChain) e;
						Vector2[] vertices = new Vector2[chain.vertices().length];
						for(int i = 0;i < chain.vertices().length;i++){
							vertices[i] = chain.vertices()[i].cpy();
							e.getTransform().mul(vertices[i]);
						}
						drawSolidPolygon(vertices, vertices.length, true);
					}
				}
			}
			shapeRenderer.end();
		}
		particleSystems.removeIf(ParticleSystem::shouldRemove);
		batch.begin();
		for(ParticleSystem particleSystem : particleSystems){
			particleSystem.render(batch);
		}
		batch.end();
		this.gui.draw();
		this.playingSounds.values().removeIf(soundWithID -> !customAPI.isPlaying((int) soundWithID.id()));
		//todo: sound volume based on distance
		sendInputPacket();
		Gdx.input.setInputProcessor(this);
	}
	private Vector2 lv = new Vector2();
	private Vector2 f = new Vector2();
	private void drawSolidPolygon (Vector2[] vertices, int vertexCount, boolean closed) {
		lv.set(vertices[0]);
		f.set(vertices[0]);
		for (int i = 1; i < vertexCount; i++) {
			Vector2 v = vertices[i];
			shapeRenderer.line(lv.x*METER_TO_PIXEL, lv.y*METER_TO_PIXEL, v.x*METER_TO_PIXEL, v.y*METER_TO_PIXEL);
			lv.set(v);
		}
		if (closed) shapeRenderer.line(f.x*METER_TO_PIXEL, f.y*METER_TO_PIXEL, lv.x*METER_TO_PIXEL, lv.y*METER_TO_PIXEL);
	}
	public void sendInputPacket(){
		Vector3 worldMouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		float worldMouseX = worldMouse.x / PaperByteMain.METER_TO_PIXEL;
		float worldMouseY = worldMouse.y / PaperByteMain.METER_TO_PIXEL;
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
		int[] typed = new int[typedChars.size()];
		for(int i = 0;i < typed.length;i++){
			typed[i] = typedChars.get(i);
		}
		networkClient.send(new ClientInputPacket(worldMouseX, worldMouseY, screenMouseX, screenMouseY, screenSizeX, screenSizeY, scrollX, scrollY, isMouse1, isMouse2, isMouse3, keys, typed));
		typedChars.clear();
		scrollX = 0;
		scrollY = 0;
	}
	private ClientMessage.Visitor CLIENT_MESSAGE_VISITOR = new ClientMessage.Visitor() {
		@Override
		public void connect(NetXClient user) {
			user.send(new ClientLoginPacket(Locale.getDefault().toString()));
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
				PaperByteMain.this.imagesNetId = gameDataPacket.imageRegistry;
				try {
					loadNodes(new ByteArrayInputStream(gameDataPacket.clientData));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			if(msg instanceof AddEntityPacket addEntityPacket){
				System.out.println("addentity" + entityRegistry);
				clientEntities.put(addEntityPacket.entityId, new ClientEntity(addEntityPacket.entityId, entityRegistry.get(addEntityPacket.entityType), addEntityPacket.position, addEntityPacket.animation));
			}
			if(msg instanceof RemoveEntityPacket removeEntityPacket){
				clientEntities.remove(removeEntityPacket.entityId);
			}
			if(msg instanceof EntityAnimationPacket animationPacket){
				clientEntities.get(animationPacket.entityId).setAnimation(animationPacket.animationId, entityNodes);
			}
			if(msg instanceof ChangeWorldPacket){
				clientEntities.clear();
				collisionRenderData = null;
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
				Gdx.graphics.setTitle("x: " + (camera.position.x/METER_TO_PIXEL) + " y: " + (camera.position.y/METER_TO_PIXEL));
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
			if(msg instanceof ServerCollisionsDebugPacket serverCollisionsDebugPacket){
				collisionRenderData = serverCollisionsDebugPacket.data;
				collisionRenderDataInvalidationTimer = 10;
			}
			if(msg instanceof ParticleSystemPacket particleSystemPacket){
				particleSystems.add(new ParticleSystem(PaperByteMain.this, particleSystemPacket));
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
				if(entry.getName().startsWith("images/")){
					imageTextures.put(Identifier.parse(entry.getName().split("/")[1].replace(".png","")), new Texture(new FileHandle(entry.getName()){
						@Override
						public InputStream read() {
							return new NonClosingInputStreamWrapper(zip);
						}
					}));
				} else {
					ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
					zip.transferTo(stream1);
					byte[] data = stream1.toByteArray();
					textures.put(entry.getName(), new Texture(new FileHandle(entry.getName()) {
						@Override
						public InputStream read() {
							return new ByteArrayInputStream(data);
						}
					}));
				}
			} else if(entry.getName().endsWith(".json")){
				renderData.put(entry.getName(), new String(zip.readAllBytes()));
			} else if(entry.getName().endsWith(".wav")){
				sounds.put(Identifier.parse(entry.getName().split("/")[1].replace(".wav","")), Gdx.audio.newSound(new FileHandle(entry.getName()){
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

	@Override
	public boolean keyDown(int keycode) {
		typedChars.add(keycode);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		typedChars.add(keycode | (1<<30));
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		typedChars.add(((int) character)|(1<<31));
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		this.scrollX += amountX;
		this.scrollY += amountY;
		return false;
	}
}