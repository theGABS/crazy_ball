package com.mygdx.crazyball;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.I18NBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Graph {
    int NOT_VISIT = 0;
    int     VISIT = 1;
    int LAST_VISIT = 2;

    private class GraphNode {
        ArrayList<Integer> link = new ArrayList<Integer>();
        int type = 0;
    }

    ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
    ArrayList<Integer> removes = new ArrayList<Integer>();

    int MAX_COUNT = 4;

    public void createGraph(int n){
        removes.clear();
        nodes.clear();
        for(int i = 0; i < n; i++){
            nodes.add(new GraphNode());
        }
    }

    public void addEdge(int a, int b){
        nodes.get(a).link.add(b);
        nodes.get(b).link.add(a);
    }

    public void bfs(int startNode){
        int count = 1;
        Queue<GraphNode> queue = new LinkedList<GraphNode>();
        queue.add(nodes.get(startNode));
        nodes.get(startNode).type = VISIT;
        while(!queue.isEmpty()) {
            GraphNode node = queue.remove();

            for(int i : node.link){
                GraphNode localNode = nodes.get(i);
                if(localNode.type == NOT_VISIT){
                    localNode.type = VISIT;
                    queue.add(localNode);
                    count++;
                }
            }
        }

        if(count >= MAX_COUNT){
            for(int i = 0; i < nodes.size(); i++){
                if(nodes.get(i).type == VISIT){
                    removes.add(i);
                }
            }
        }

        for(GraphNode node : nodes){
            if(node.type == VISIT){
                node.type = LAST_VISIT;
            }
        }
    }

    public ArrayList<Integer> searchBigGroups(){
        for(int i = 0; i < nodes.size(); i++){
            if(nodes.get(i).type == NOT_VISIT){
                bfs(i);
            }
        }

        Collections.sort(removes, Collections.reverseOrder());
        return removes;
    }
}

public class MyGame extends Game implements ApplicationListener{

    BitmapFont fontGeneration(int size) {

        String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
        String RUSSIAN_CHARACTERS = "АБВГДЕЁЖЗИІЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
                + "абвгдеёжзиійклмнопрстуфхцчшщъыьэюя";

        BitmapFont font;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-CondBold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.characters = RUSSIAN_CHARACTERS + FONT_CHARACTERS;
        font = generator.generateFont(parameter);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        return font;

    }

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

    MenuScreen menuScreen;
    GameScreen gameScreen;

    I18NBundle myBundle;

    Texture background;

    @Override
    public void create() {
        background =     new Texture("fon5.jpg");
        myBundle =       I18NBundle.createBundle(Gdx.files.internal("i18n/MyBundle"));
        camera =         new OrthographicCamera();
        fullCamera =     new OrthographicCamera();
        batch =          new SpriteBatch();
        menuScreen =     new MenuScreen(this);
        gameScreen =     new GameScreen(this);

        setScreen(menuScreen);
        myRequestHandler.showAds(false); // Its load....  todo rename
    }

    class MenuScreen implements Screen  {
        float colorValue = 0;
        MyGame game;
        float time;
        Texture line = new Texture("line.png");



        public MenuScreen(MyGame game){
            this.game = game;
        }

        @Override
        public void render(float delta) {
            time += delta;
            colorValue = (float) ((1 + Math.sin(time*1.6f)) * 0.5f);


            font.setColor(new Color(1, 1, 1, 1));


            batch.setProjectionMatrix(fullCamera.combined);
            batch.begin();
            batch.draw(background, 0, 0, fullCamera.viewportWidth, fullCamera.viewportHeight);



            String[] helloText = {myBundle.get("helloText1") , myBundle.get("helloText2") , myBundle.get("helloText3")};


            float sm;
            for(int j = 0; j < 2; j++) {
                float acm = fullCamera.viewportHeight*0.98f;
//                if(j == 0){
//                    sm = 1;
//                    font.setColor(new Color(0, 0, 0, 1));
//                }
//
//                if(j == 1){
//                    sm = 0;
//                    font.setColor(new Color(1, 1, 1, 1));
//                }

                sm = 1-j;
                font.setColor(new Color(j, j, j, 1));

                for (int i = 0; i < 3; i++) {
                    font.drawWrapped(batch, helloText[i], fullCamera.viewportWidth * 0.02f - sm , acm - sm, fullCamera.viewportWidth * 0.96f);
                    acm -= font.getWrappedBounds(helloText[i], fullCamera.viewportWidth * 0.96f).height;
                    acm -= 25;
                    batch.draw(line, fullCamera.viewportWidth * 0.02f, acm, fullCamera.viewportWidth * 0.96f, 15);
                    acm -= 10;
                }
            }


            String howStart = myBundle.get("howStart");
            font.setColor(new Color(colorValue, colorValue, colorValue, 1));
            font.draw(batch, howStart, fullCamera.viewportWidth / 2 - font.getBounds(howStart).width / 2,
                    fullCamera.viewportHeight*0.12f + font.getBounds(howStart).height / 2);

            batch.end();

            if (Gdx.input.justTouched()) {
                game.setScreen(game.gameScreen);
            }
        }


        @Override
        public void resize(int width, int height) {
//            if(width*4/3.0f >= height){
//                Gdx.graphics.setDisplayMode((int) (height*0.75f), height, false);
//            }
            camera.viewportWidth = 480f;
            camera.viewportHeight = 480f * height/width;
            camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight/2f , 0); // show in top
            camera.update();

            fullCamera.viewportWidth = width;
            fullCamera.viewportHeight = height;
            fullCamera.position.set(fullCamera.viewportWidth / 2f, fullCamera.viewportHeight / 2f, 0);
            fullCamera.update();

            font = fontGeneration(width/13);
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

    enum State{
        PAUSE,
        RUN,
    }


    class GameScreen implements Screen, InputProcessor {
        @Override
        public boolean mouseMoved(int screenX, int screenY) { return false; }

        @Override
        public boolean scrolled(int amount) { return false; }

        @Override
        public boolean keyDown(int keycode) {
            if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){
                switch (state) {
                case PAUSE:
                    Gdx.app.exit();
                    break;
                case RUN:
                    state = State.PAUSE;
                }
            }
            return false;
        }

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
            switch (state) {
            case PAUSE:
                state = State.RUN;
                break;
            case RUN:
                createBall(new Vector2(screenX - Gdx.graphics.getWidth() / 2f, -screenY).scl(0.075f * 480 / Gdx.graphics.getWidth()));
            }
            return true;
        }







        Texture  tFloor;
        ShapeRenderer shapeRenderer;
        FPSLogger fpsLogger =    new FPSLogger();
        Random rn =              new Random();
        ArrayList<Ball> pBalls = new ArrayList<Ball>();
        Texture[] tBall =        new Texture[5];
        Graph graphBall =        new Graph();
        World world =            new World(new Vector2(0, -20), true);

        State state = State.RUN;
        final int BOX_VELOCITY_ITERATIONS=6;
        final int BOX_POSITION_ITERATIONS=3;
        final int MAX_BALL = 54;
        int       score = 0;
        int       level = 0;
        int       nextColor = rn.nextInt(5);
        float     timeShot = 0.4f;
        float     timePassed;
        MyGame    game;

        public void createBall(Vector2 velocity, Vector2 position ){
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.DynamicBody;

            CircleShape dynamicCircle = new CircleShape();
            dynamicCircle.setRadius(3.6f);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = dynamicCircle;
            fixtureDef.density = 1.0f;
            fixtureDef.friction = 0.0f;
            fixtureDef.restitution = 0.5f;

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
            Gdx.input.setCatchBackKey(true);
            game = _game;
            shapeRenderer = new ShapeRenderer();


            tBall[0] = new Texture("ball.png"); // BLUE
            tBall[1] = new Texture("ballred.png");
            tBall[2] = new Texture("ballgreen.png");
            tBall[3] = new Texture("ballpurple.png");
            tBall[4] = new Texture("ballyellow.png");
            tFloor   = new Texture("floor3.jpg");



            Gdx.input.setInputProcessor(this);

            BodyDef groundBodyDef = new BodyDef();

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
            score = level*100;

            if(!first){
                myRequestHandler.showAds(true);
            }

            for(int i = pBalls.size() - 1; i >= 0; i--){
                world.destroyBody(pBalls.get(i).body);
                pBalls.remove(i);
            }

            for(int i = 0; i < 14; i++){
                createBall(new Vector2(rn.nextInt(11)-5,-30) , new Vector2(rn.nextInt(48) , 10 + rn.nextInt(40)));
            }


        }



        @Override
        public void render(float dt) {

            level = score / 100;
            timeShot = (float) (1.8 * Math.pow(2.7, -level/5) + 0.4);

            fpsLogger.log();
            //Gdx.app.log(Float.toString(dt),"3434");


            for(int i = 0; i < pBalls.size(); i++){
                pBalls.get(i).body.setUserData(i);
            }

            Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

            batch.setProjectionMatrix(fullCamera.combined);
            batch.begin();
            batch.draw(tFloor, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth()*tFloor.getHeight()/tFloor.getWidth());
            batch.end();

            batch.setProjectionMatrix(camera.combined);
            batch.enableBlending();
            batch.begin();
            batch.draw(background, 0, 0, 480, 640);

            for(Ball ball : pBalls){
                batch.draw(tBall[ball.color],ball.body.getPosition().x*10 -36f,
                        ball.body.getPosition().y*10 -36f , 72, 72);
            }

            graphBall.createGraph(pBalls.size());

            for(Contact contact : world.getContactList()){
                if(contact.isTouching()) {
                    if(contact.getFixtureA().getShape() instanceof CircleShape &&
                            contact.getFixtureB().getShape() instanceof CircleShape) {
                        if(contact.getFixtureA().getBody().getUserData() != null &&
                                contact.getFixtureB().getBody().getUserData() != null) {
                            int a = (Integer) contact.getFixtureA().getBody().getUserData();
                            int b = (Integer) contact.getFixtureB().getBody().getUserData();

                            if (pBalls.get(a).color == pBalls.get(b).color) {
                                graphBall.addEdge(a, b);
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

//            shapeRenderer.setProjectionMatrix(fullCamera.combined);
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            shapeRenderer.rect(0,0, 480 * fullCamera.viewportHeight/640, fullCamera.viewportHeight*0.9f);
//            shapeRenderer.end();

            float fullWidth = Math.min(480 * fullCamera.viewportHeight/640 , fullCamera.viewportWidth);

            batch.setProjectionMatrix(fullCamera.combined);
            batch.begin();

            String textScore = "score: " + Integer.toString(score) + "  ";
            font.draw(batch, textScore , fullWidth - font.getBounds(textScore).width , fullCamera.viewportHeight*0.98f );

            String textLeft = "  ball " + Integer.toString(pBalls.size()) + " / "+Integer.toString(MAX_BALL);
            font.draw(batch, textLeft, 0 , fullCamera.viewportHeight*0.98f);

            String textLevel = "  level: " + Integer.toString(level+1);
            font.draw(batch, textLevel, 0 , font.getBounds(textLevel).height*1.1f);






            ArrayList<Integer> tmp = graphBall.searchBigGroups();
            score += tmp.size()*3;

            for(int index : tmp){
                world.destroyBody(pBalls.get(index).body);
                pBalls.remove(index);
            }


            switch (state){
            case RUN:
                timePassed += dt;
                if(timePassed > timeShot){
                    createBall(new Vector2(rn.nextInt(11)-5,-30));
                }

                world.step(dt , BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);

                if(pBalls.size() > MAX_BALL){
                    batch.end();
                    newGame(false);
                    return;
                }
                break;
            case PAUSE:
                batch.setColor(1, 1, 1, 0.7f);
                batch.draw(background, 0, 0, fullCamera.viewportWidth, fullCamera.viewportHeight);
                String howContinue = myBundle.get("howToContinue");
                font.draw(batch, howContinue, fullWidth/2 - font.getBounds(howContinue).width/2, fullCamera.viewportHeight/2 + font.getBounds("Pause").height/2);
                break;
            }

            batch.end();
            batch.setColor(1, 1, 1, 1f);


        }

        @Override
        public void resize(int width, int height) {
            float aspectRatio = (float) width / (float) height;


// This better, but hard, maybe will be in next version
            if(width * 640 > height * 480){
                camera.viewportWidth = 480f *width/height *640/480;
                camera.viewportHeight = 640f;
                camera.position.set(240,  camera.viewportHeight/2 , 0);
                camera.update();
            }else {
                camera.viewportWidth = 480f;
                camera.viewportHeight = 640f * height / width * 480 / 640;
                camera.position.set(camera.viewportWidth / 2f,  - camera.viewportHeight / 2f + 640 , 0);
                camera.update();
            }


//            camera.viewportWidth = 480f;
//            camera.viewportHeight = 640f * height / width * 480 / 640;
//            camera.position.set(camera.viewportWidth / 2f, 0 - camera.viewportHeight / 2f + 640, 0);
//            camera.update();

            if(width * 640 > height * 480){
                fullCamera.viewportWidth = height*aspectRatio;
                fullCamera.viewportHeight = height;
                fullCamera.position.set(240 * height/640, 0 - fullCamera.viewportHeight / 2f + height, 0);
                fullCamera.update();
                font = fontGeneration((int) (height/12 * (480/640f)));
            }else {
                fullCamera.viewportWidth = width;
                fullCamera.viewportHeight = 640f * height / width * width / 640;
                fullCamera.position.set(fullCamera.viewportWidth / 2f, 0 - fullCamera.viewportHeight / 2f + height, 0);
                fullCamera.update();
                font = fontGeneration(width/12);
            }


//            fullCamera.viewportWidth = Gdx.graphics.getWidth();
//            fullCamera.viewportHeight = Gdx.graphics.getHeight();
//            fullCamera.position.set(fullCamera.viewportWidth / 2f, fullCamera.viewportHeight / 2f, 0);
//            fullCamera.update();


        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }
    }
}

