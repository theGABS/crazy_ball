package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.InputProcessor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Graf {

    class GrafNode {
        ArrayList<Integer> link;
        int type; // its need for BFS
        GrafNode(){
            type = 0;
            link = new ArrayList<Integer>();
        }
    }

    ArrayList<GrafNode> nodes = new ArrayList<GrafNode>();
    int size;

    ArrayList<Integer> removes = new ArrayList<Integer>();
    int whenBig = 4;

    public void createGraf(int n){
        removes.clear();
        nodes.clear();
        size = n;
        for(int i = 0; i < size; i++){
            nodes.add(new GrafNode());
        }
    }

    public void addEdge(int a, int b){
        if(a > size - 1 || b > size - 1 ){
            return;
        }
        nodes.get(a).link.add(b);
        nodes.get(b).link.add(a);
    }

    public void bfs(int startNode){
        int count = 1;
        Queue<GrafNode> queue = new LinkedList();
        queue.add(nodes.get(startNode));
        nodes.get(startNode).type = 1;
        while(!queue.isEmpty()) {
            GrafNode node = queue.remove();
            for(int i = 0; i < node.link.size(); i++){
                if(nodes.get(node.link.get(i)).type == 0) {
                    nodes.get(node.link.get(i)).type = 1;
                    queue.add(nodes.get(node.link.get(i)));
                    count++;
                }
            }

        }

        if(count >= whenBig){
            for(int i = 0; i < nodes.size(); i++){
                if(nodes.get(i).type == 1){
                    removes.add(i);
                }

            }
        }

        for(int i = 0; i < nodes.size(); i++){
            if(nodes.get(i).type == 1){
                nodes.get(i).type = 2;
            }

        }
    }

    public ArrayList<Integer> searchBigGroups(){
        for(int i = 0; i < nodes.size(); i++){
            if(nodes.get(i).type == 0){
                bfs(i);
            }
        }

        Collections.sort(removes);
        return removes;
    }
}

public class MyGame extends Game implements ApplicationListener{

    class Ball{
        Body body;
        int color;
        public Ball(Body body, int color){
            this.color = color;
            this.body = body;
        }
    }

    SpriteBatch batch;
    BitmapFont font;
    OrthographicCamera camera, fullCamera;
    public IActivityRequestHandler myRequestHandler;
    public MyGame(IActivityRequestHandler handler) {
        myRequestHandler = handler;
    }

    MainMenuScreen mainMenuScreen;
    GameScreen gameScreen;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        fullCamera = new OrthographicCamera();
        batch = new SpriteBatch();
        mainMenuScreen = new MainMenuScreen(this);
        gameScreen = new GameScreen(this);
        setScreen(mainMenuScreen);
        myRequestHandler.showAds(false);
    }

    class MainMenuScreen implements Screen  {
        MyGame game;
        Texture tMan= new Texture("man.png");

        public MainMenuScreen(MyGame game){
            this.game = game;

            tMan.setFilter(Texture.TextureFilter.Linear , Texture.TextureFilter.Linear);
        }

        @Override
        public void render(float delta) {
            GL20 OpenGL = Gdx.graphics.getGL20();
            OpenGL.glClearColor(0.5f, 0.8f, 0.8f, 1);
            OpenGL.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(fullCamera.combined);
            batch.begin();
            String text = "four ball in contact \n will be deleted. \n touch to start";
            font.drawMultiLine(batch, text, fullCamera.viewportWidth / 2 - font.getMultiLineBounds(text).width / 2,
                    fullCamera.viewportHeight - 10);
            batch.draw(tMan,0,0,fullCamera.viewportWidth , fullCamera.viewportWidth);
            batch.end();

            if (Gdx.input.justTouched()) {
                game.setScreen(game.gameScreen);
            }
        }


        @Override
        public void resize(int width, int height) {
            camera.viewportWidth = 480f;
            camera.viewportHeight = 480f * height/width;
            camera.position.set(camera.viewportWidth / 2f,0 -camera.viewportHeight/2f + 640 , 0); // show in top
            camera.update();

            fullCamera.viewportWidth = width;
            fullCamera.viewportHeight = height;
            fullCamera.position.set(fullCamera.viewportWidth / 2f, fullCamera.viewportHeight / 2f, 0);
            fullCamera.update();

            font = new BitmapFont();
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-CondBold.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = width/12;
            font = generator.generateFont(parameter);
            generator.dispose(); // don't forget to dispose to avoid memory leaks!
        }

        @Override
        public void show() {  }

        @Override
        public void hide() {  }

        @Override
        public void pause() {  }

        @Override
        public void resume() {  }

        @Override
        public void dispose() {  }
    }


    class GameScreen implements Screen, InputProcessor {
        @Override
        public boolean mouseMoved(int screenX, int screenY) { return false; }

        @Override
        public boolean scrolled(int amount) { return false; }

        @Override
        public boolean keyDown(int keycode) { return false; }

        @Override
        public boolean keyUp(int keycode) { return false; }

        @Override
        public boolean keyTyped(char character) { return false; }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) { return true; }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

        @Override
        public void show() {  }

        @Override
        public void hide() {  }

        @Override
        public void dispose() {  }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            createBall(new Vector2(screenX - Gdx.graphics.getWidth()/2f , -screenY).scl(0.1f * 480/Gdx.graphics.getWidth()));
            return true;
        }




        FPSLogger fpsLogger = new FPSLogger();
        Random rn = new Random();
        int nextColor = rn.nextInt(5);
        Texture tBackground, tFloor;
        Texture[] tBall = new Texture[5];
        ShapeRenderer shapeRenderer;
        Graf grafBall = new Graf();
        ArrayList<Ball> pBalls = new ArrayList<Ball>();
        World world = new World(new Vector2(0, -20), true);

        static final int BOX_VELOCITY_ITERATIONS=6;
        static final int BOX_POSITION_ITERATIONS=3;
        final int MAX_BALL = 54;
        int score = 0;
        float timeShot = 2.2f;
        float timePassed;
        MyGame game;

        public void createBall(Vector2 velocity, Vector2 position ){
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.DynamicBody;

            CircleShape dynamicCircle = new CircleShape();
            dynamicCircle.setRadius(3.6f);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = dynamicCircle;
            fixtureDef.density = 1.0f;
            fixtureDef.friction = 0.0f;
            fixtureDef.restitution = 0.7f;

            bodyDef.position.set(position); // center top screen
            Body tmpBall = world.createBody(bodyDef);

            tmpBall.createFixture(fixtureDef);
            tmpBall.setLinearVelocity(velocity);

            pBalls.add(new Ball(tmpBall,nextColor)); nextColor = rn.nextInt(5);
            timePassed = 0;
        }

        public void createBall(Vector2 velocity){
            createBall(velocity, new Vector2(24f, 61f));
        }

        public GameScreen(MyGame _game) {
            game = _game;
            shapeRenderer = new ShapeRenderer();


            tBall[0] = new Texture("ball.png");
            tBall[1] = new Texture("ballred.png");
            tBall[2] = new Texture("ballgreen.png");
            tBall[3] = new Texture("ballpurple.png");
            tBall[4] = new Texture("ballyellow.png");
            tBackground = new Texture("fon4.png");
            tFloor = new Texture("floor.jpg");



            Gdx.input.setInputProcessor(this);

            BodyDef groundBodyDef =new BodyDef();

            groundBodyDef.position.set(new Vector2(0, 0));
            PolygonShape groundBox = new PolygonShape();
            groundBox.setAsBox((48.0f) * 2, 0.0f);
            world.createBody(groundBodyDef).createFixture(groundBox, 0.0f);

            groundBodyDef.position.set(new Vector2(0, 0));
            groundBox.setAsBox(0, 100.0f);
            world.createBody(groundBodyDef).createFixture(groundBox, 0.0f);

            groundBodyDef.position.set(new Vector2(48, 0));
            groundBox.setAsBox(0, 100.0f);
            world.createBody(groundBodyDef).createFixture(groundBox, 0.0f);

            newGame(true);

        }

        void newGame(boolean first){
            score = 0;

            if(!first){
                myRequestHandler.showAds(true);
            }

            for(int i = pBalls.size() - 1; i >= 0; i--){
                world.destroyBody(pBalls.get(i).body);
                pBalls.remove(i);
            }

            for(int i = 0; i < 14; i++){
                createBall(new Vector2(rn.nextInt(11)-5,-30) , new Vector2(rn.nextInt(24) , rn.nextInt(77)));
            }


        }



        @Override
        public void render(float dt) {
            fpsLogger.log();
            timePassed += dt;
            if(timePassed > timeShot){
                createBall(new Vector2(rn.nextInt(11)-5,-30));
            }

            for(int i = 0; i < pBalls.size(); i++){
                pBalls.get(i).body.setUserData(i);
            }

            batch.setProjectionMatrix(fullCamera.combined);
            batch.begin();
            batch.draw(tFloor,0,0, Gdx.graphics.getWidth() , 200);
            batch.end();


            batch.setProjectionMatrix(camera.combined);
            batch.enableBlending();
            batch.begin();
            batch.draw(tBackground, 0, 72, 480, 640);


            for(Ball ball : pBalls){
                batch.draw(tBall[ball.color],ball.body.getPosition().x*10 -36f ,
                        ball.body.getPosition().y*10 -36f , 72, 72);
            }

            grafBall.createGraf(pBalls.size());

            for(Contact contact : world.getContactList()){
                if(contact.isTouching()) {
                    if(contact.getFixtureA().getShape() instanceof CircleShape &&
                            contact.getFixtureB().getShape() instanceof CircleShape) {
                        if(contact.getFixtureA().getBody().getUserData() != null &&
                                contact.getFixtureB().getBody().getUserData() != null) {
                            int a = (Integer) contact.getFixtureA().getBody().getUserData();
                            int b = (Integer) contact.getFixtureB().getBody().getUserData();

                            if (pBalls.get(a).color == pBalls.get(b).color) {
                                grafBall.addEdge(a, b);
                            }
                        }
                    }
                }
            }

            batch.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            float opacity = 0.2f + 0.8f*timePassed/timeShot;
            switch(nextColor){
                case 0:
                    shapeRenderer.setColor(0, 0, 1, opacity);
                    break;
                case 1:
                    shapeRenderer.setColor(1, 0, 0, opacity);
                    break;
                case 2:
                    shapeRenderer.setColor(0, 1, 0, opacity);
                    break;
                case 3:
                    shapeRenderer.setColor(1, 0, 1, opacity);
                    break;
                case 4:
                    shapeRenderer.setColor(0.8f, 0.8f, 0, opacity);
            }

            shapeRenderer.rect(0, 640-60, 480, 60);
            shapeRenderer.end();

            batch.setProjectionMatrix(fullCamera.combined);
            batch.begin();

            String textScore = "score: " + Integer.toString(score) + "  ";
            font.draw(batch, textScore , fullCamera.viewportWidth - font.getBounds(textScore).width , fullCamera.viewportHeight*0.98f );

            String textLeft = "ball" + Integer.toString(pBalls.size()) + " / "+Integer.toString(MAX_BALL);
            font.draw(batch, textLeft, 10 , fullCamera.viewportHeight*0.98f);



            batch.end();

            ArrayList<Integer> tmp = grafBall.searchBigGroups();
            score += tmp.size();

            for(int i = tmp.size() - 1; i >= 0; i--){
                int index = tmp.get(i);
                world.destroyBody(pBalls.get(index).body);
                pBalls.remove(index);
            }

            world.step(dt , BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);

            if(pBalls.size() > MAX_BALL){
                newGame(false);
                return;
            }
        }
        @Override
        public void resize(int width, int height) {
            camera.viewportWidth = 480f;
            camera.viewportHeight = 640f * height/width *480/640;
            camera.position.set(camera.viewportWidth / 2f,0 -camera.viewportHeight/2f + 640 , 0);
            camera.update();

            fullCamera.viewportWidth = Gdx.graphics.getWidth();
            fullCamera.viewportHeight = Gdx.graphics.getHeight();
            fullCamera.position.set(fullCamera.viewportWidth / 2f, fullCamera.viewportHeight / 2f, 0);
            fullCamera.update();

            font = new BitmapFont();
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-CondBold.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = width/12;
            font = generator.generateFont(parameter);
            generator.dispose(); // don't forget to dispose to avoid memory leaks!
        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }
    }
}

