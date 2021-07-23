package me.cube.engine.editor;

import me.cube.engine.*;
import me.cube.engine.file.Assets;
import me.cube.engine.file.VoxelFile;
import me.cube.engine.game.world.Terrain;
import me.cube.engine.game.world.World;
import me.cube.engine.model.SimpleVoxelMesh;
import me.cube.engine.model.VoxelMesh;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

import static me.cube.engine.Input.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;

public class EditorGame implements Game {

    private static final int MAX_ACTION_MEMORY = 20;

    private Terrain terrain;

    private Vector3f cameraPosition;
    private float yaw, pitch;

    private Vector3f mouseWorldProjection = new Vector3f();

    private int selectedModel;
    private VoxelMesh selectedModelMesh;

    private List<EditAction> editActions;

    private List<VoxelFile> placeableModels;

    @Override
    public void init() {

        placeableModels = Assets.loadVoxelDataFolder("editor/models/");

        editActions = new ArrayList<>(MAX_ACTION_MEMORY);

        cameraPosition = new Vector3f();

        cameraPosition.y = 250;

        terrain = new Terrain(10, "test");

        Camera.projectionMatrix = new Matrix4f()
                .perspective(Math.toRadians(90f), 1280f / 720f, 0.5f, 5000);

        Camera.cameraMatrix = new Matrix4f();

        yaw = 0f;
        pitch = 45f;

        selectModel(0);


    }

    private void selectModel(int index){
        if(index < 0 || index >= placeableModels.size()){
            return;
        }

        VoxelFile model = placeableModels.get(index);
        selectedModel = index;

        selectedModelMesh = new SimpleVoxelMesh(model.toVoxelColorArray(), model.width(), model.height(), model.length());
    }

    private void executeAction(EditAction editAction){
        editActions.add(0, editAction);
        editAction.execute(terrain);

        if(editActions.size() > MAX_ACTION_MEMORY){
            editActions.remove(editActions.size()-1);
        }
    }

    private void undo(){
        if(editActions.size() > 0){
            EditAction editAction = editActions.remove(0);

            editAction.undo(terrain);
        }
    }

    public Vector3f getCameraForward(){
        float rYaw = Math.toRadians(yaw);
        float rPitch = Math.toRadians(pitch);

        Matrix4f m = new Matrix4f().identity().rotate(rYaw, 0, 1, 0).rotate(rPitch, 1, 0, 0);

        Vector4f forward = new Vector4f(0, 0, 1, 0);

        forward.mul(m);

        return new Vector3f(forward.x, forward.y, forward.z);
    }

    public Vector3f getCameraRight(){
        Vector3f forward = getCameraForward();
        Vector3f right = new Vector3f();

        forward.rotateAxis(Math.toRadians(-90f), 0, 1, 0, right);

        return right;
    }

    private void updateCamera(float delta){
        float speed = 300f;

        if(Input.isActionActive(ACTION_EDITOR_SPEED)){
            speed = 600f;
        }

        speed *= delta;

        Vector3f forward = getCameraForward();
        Vector3f right = getCameraRight();
        right.y = 0;

        forward.mul(speed);
        right.normalize().mul(speed);



        if(Input.isActionActive(ACTION_FORWARD)){
            cameraPosition.add(forward);
        }else if(Input.isActionActive(ACTION_BACK)){
            cameraPosition.sub(forward);
        }

        if(Input.isActionActive(ACTION_LEFT)){
            cameraPosition.sub(right);
        }else if(Input.isActionActive(ACTION_RIGHT)){
            cameraPosition.add(right);
        }

        if(Input.isActionActive(ACTION_JUMP)){
            cameraPosition.y += speed;
        }

        Camera.cameraMatrix.identity().lookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z,
                cameraPosition.x + forward.x, cameraPosition.y + forward.y, cameraPosition.z + forward.z,
                0, 1, 0);
    }

    @Override
    public void update(float delta) {

        updateCamera(delta);

        Vector2f mouse = Input.getNormalizedCursorPosition();

        Vector3f world = Camera.screenToWorld(mouse);

        Vector3f direction = Camera.screenToDirection(mouse);

        mouseWorldProjection.set(terrain.rayTrace(world.add(0, 0, 0), direction, 5000f));


        terrain.updateTerrain(delta, new Vector3f(cameraPosition));



    }

    @Override
    public void render() {

        glEnable(GL_BLEND);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);

        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);

        terrain.render();

        glDisable(GL_CULL_FACE);

        if(Input.getCursorMode() == GLFW_CURSOR_NORMAL){
            /*glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);*/
            Voxel voxel = new Voxel("Cursor", selectedModelMesh);
            voxel.getTransform().scale(World.WORLD_SCALE);

            Vector3f worldGridPosition = new Vector3f(mouseWorldProjection).mul(1f / World.WORLD_SCALE);

            VoxelFile model = placeableModels.get(selectedModel);

            voxel.getTransform().translate((int) worldGridPosition.x, (int) worldGridPosition.y + Math.ceil(model.height() / 2f) + 1, (int) worldGridPosition.z).translate(0.5f, 0.01f, 0.5F);

            voxel.render();
            //glDisable(GL_CULL_FACE);
        }

        glDisable(GL_MULTISAMPLE);
        glDisable(GL_DEPTH_TEST);

        glDisable(GL_BLEND);
    }

    @Override
    public void onKeyPress(int key, int action) {

        if(key == GLFW_KEY_W){
            Input.setActionState(ACTION_FORWARD, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_S){
            Input.setActionState(ACTION_BACK, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_A){
            Input.setActionState(ACTION_LEFT, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_D){
            Input.setActionState(ACTION_RIGHT, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_SPACE){
            Input.setActionState(ACTION_JUMP, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_LEFT_SHIFT){
            Input.setActionState(ACTION_EDITOR_SPEED, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_LEFT_CONTROL){
            Input.setModifier(MODIFIER_CONTROL, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_Z && action == GLFW_PRESS && Input.isModifierActive(MODIFIER_CONTROL)){
            undo();
        }
    }

    @Override
    public void onCursorMove(double dx, double dy) {
        yaw -= dx / 9f;
        pitch -= dy / 9f;
        if(pitch > 89){
            pitch = 89;
        }else if(pitch < -89){
            pitch = -89;
        }
    }

    @Override
    public void onMouseScroll(double delta) {
        if(delta > 0){
            selectModel(selectedModel + 1);
        }else if(delta < 0){
            selectModel(selectedModel - 1);
        }
    }

    @Override
    public void onMousePress(int button, int action) {

        if(button == GLFW_MOUSE_BUTTON_2){
            if(action == GLFW_PRESS){
                Input.setCursorMode(GLFW_CURSOR_HIDDEN);
            }else{
                Input.setCursorMode(GLFW_CURSOR_NORMAL);
            }
        }

        if(button == GLFW_MOUSE_BUTTON_1){
            if(action == GLFW_PRESS){
                Vector3f worldGridPosition = new Vector3f(mouseWorldProjection).mul(1f / World.WORLD_SCALE);

                VoxelFile model = placeableModels.get(selectedModel);

                worldGridPosition.set((int) worldGridPosition.x - model.width() / 2f, (int) worldGridPosition.y, (int) worldGridPosition.z - model.length() / 2f);

                int pasteX = (int) worldGridPosition.x;
                int pasteY = (int) worldGridPosition.y + 1;
                int pasteZ = (int) worldGridPosition.z;

                executeAction(new PasteStructureAction(model, pasteX, pasteY, pasteZ));

            }
        }

    }

    @Override
    public String getTitle() {
        return "Cube Game Editor - "+(cameraPosition != null ? cameraPosition.toString() : "");
    }

    @Override
    public void destroy() {
        terrain.dispose();
    }
}
